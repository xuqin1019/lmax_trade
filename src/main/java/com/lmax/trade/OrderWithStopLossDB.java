package com.lmax.trade;

import com.lmax.api.*;
import com.lmax.api.account.LoginCallback;
import com.lmax.api.account.LoginRequest;
import com.lmax.api.order.*;
import com.lmax.api.orderbook.OrderBookEvent;
import com.lmax.api.orderbook.OrderBookEventListener;
import com.lmax.api.orderbook.OrderBookSubscriptionRequest;
import com.lmax.api.profile.Timer;
import com.lmax.api.reject.InstructionRejectedEvent;
import com.lmax.api.reject.InstructionRejectedEventListener;
import com.lmax.trade.model.OrderDirection;
import com.lmax.trade.model.OrderExecution;
import com.lmax.trade.model.OrderRequest;
import com.lmax.trade.model.OrderRequestStatus;
import com.lmax.trade.model.query.OrderRequestQuery;
import com.lmax.trade.repository.OrderExecutionRepository;
import com.lmax.trade.repository.OrderRequestRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: OrderWithStopLoss.java, v 0.1 2/13/16 4:54 PM qiancheng.xq Exp $
 */
public class OrderWithStopLossDB implements LoginCallback, OrderBookEventListener,
                                            OrderEventListener, ExecutionEventListener,
                                            InstructionRejectedEventListener {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(OrderWithStopLoss.class);

    private Lock priceLock = new ReentrantLock();

    private volatile FixedPointNumber lastBidPrice = FixedPointNumber.ZERO;

    private volatile FixedPointNumber lastAskPrice = FixedPointNumber.ZERO;

    private Session session;

    private OrderRequestRepository orderRequestRepository = new OrderRequestRepository();

    private OrderExecutionRepository orderExecutionRepository = new OrderExecutionRepository();

    private Map<String, Integer> instructionId2OrderRequestId = new ConcurrentHashMap<String, Integer>();

    private Map<String, PlacedLimitOrder> placedOrder = new ConcurrentHashMap<String, PlacedLimitOrder>();

    private final long instrumentId = 4001;

    static {
        DOMConfigurator.configure("log4j.xml");
    }

    @Override
    public void onLoginSuccess(Session session) {
        LOGGER.info("Log in successfully. AccountId is: {}", session.getAccountDetails()
                .getAccountId());

        this.session = session;

        this.session.registerOrderBookEventListener(Timer.forOrderBookEvents(this));

        this.session.registerOrderEventListener(Timer.forOrderEvents(this));

        this.session.registerExecutionEventListener(Timer.forExecutionEvents(this));

        this.session.registerInstructionRejectedEventListener(this);

        this.session.subscribe(new OrderBookSubscriptionRequest(instrumentId), new Callback() {
            @Override
            public void onSuccess() {
                LOGGER.info("Subscribe order book event successfully.");
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                LOGGER.error("Failed to subscribe order book event. msg=[{}],description=[{}]",
                        failureResponse.getMessage(), failureResponse.getDescription());
            }
        });

        this.session.subscribe(new OrderSubscriptionRequest(), new Callback() {
            @Override
            public void onSuccess() {
                LOGGER.info("Subscribe order event successfully.");
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                LOGGER.error("Failed to subscribe order event. msg=[{}],description=[{}]",
                        failureResponse.getMessage(), failureResponse.getDescription());
            }
        });

        this.session.subscribe(new ExecutionSubscriptionRequest(), new Callback() {
            @Override
            public void onSuccess() {
                LOGGER.info("Subscribe order execution event successfully.");
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                LOGGER.error(
                        "Failed to subscribe order execution event. msg=[{}],description=[{}]",
                        failureResponse.getMessage(), failureResponse.getDescription());
            }
        });

        //register task to place order (execute every 10s)
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new PlaceOrderTask(session), 10000, 15000);

        this.session.start();
    }

    @Override
    public void onLoginFailure(FailureResponse failureResponse) {
        LOGGER.error("Failed to log in. response={}", failureResponse);
    }

    @Override
    public void notify(OrderBookEvent orderBookEvent) {
        try {
            priceLock.lock();
            //            LOGGER.info("Updated Price for order book. BidPrice={}, AskPrice={}",
            //                orderBookEvent.getValuationBidPrice(), orderBookEvent.getValuationAskPrice());
            lastBidPrice = orderBookEvent.getValuationBidPrice();
            lastAskPrice = orderBookEvent.getValuationAskPrice();
        } finally {
            priceLock.unlock();
        }
    }

    @Override
    public void notify(Order order) {
        LOGGER
                .debug(
                        "Order(instructionId={},limitPrice={},quantity={},filledQuantity={},canceledQuantity={}) is executing by Broker",
                        order.getInstructionId(), order.getLimitPrice(), order.getQuantity(),
                        order.getFilledQuantity(), order.getCancelledQuantity());
        updateOrderRequest(order.getInstructionId(), FixedPointNumber.ZERO, FixedPointNumber.ZERO, OrderRequestStatus.ACCEPT, null);
    }

    @Override
    public void notify(Execution execution) {
        synchronized (this) {
            Order order = execution.getOrder();
            PlacedLimitOrder placedLimitOrder = placedOrder.get(order.getInstructionId());

            OrderExecution orderExecution = genOrderExecution(execution);
            orderExecutionRepository.create(orderExecution);

            if (placedLimitOrder != null) {
                placedLimitOrder.setFilledQuantity(FixedPointNumber.valueOf(placedLimitOrder
                                                                                    .getFilledQuantity().longValue() + execution.getQuantity().longValue()));
                placedLimitOrder
                        .setPendingQuantity(FixedPointNumber.valueOf(placedLimitOrder
                                                                             .getPendingQuantity().longValue()
                                                                     - execution.getQuantity()
                                .longValue()));
                placedLimitOrder.setCanceledQuantity(FixedPointNumber.valueOf(placedLimitOrder.getCanceledQuantity().longValue() + execution.getCancelledQuantity().longValue()));
                LOGGER
                        .info(
                                "Order(instructionId={},limitPrice={},stopLossOffset={},stopProfitOffset={},placedTime={}) is filled by Broker(price={},quantity={})",
                                execution.getOrder().getInstructionId(), placedLimitOrder.getLimitPrice(),
                                placedLimitOrder.getStopLossPriceOffset(),
                                placedLimitOrder.getStopProfitPriceOffset(),
                                DateUtils.format(placedLimitOrder.getPlacedTime()), execution.getPrice(),
                                execution.getQuantity());

                if (placedLimitOrder.isComplete()) {
                    LOGGER
                            .info(
                                    "Order(instructionId={},limitPrice={},stopLossOffset={},stopProfitOffset={},placedTime={}) is completely finished by Broker",
                                    execution.getOrder().getInstructionId(),
                                    placedLimitOrder.getLimitPrice(),
                                    placedLimitOrder.getStopLossPriceOffset(),
                                    placedLimitOrder.getStopProfitPriceOffset(),
                                    DateUtils.format(placedLimitOrder.getPlacedTime()));
                    placedOrder.remove(order.getInstructionId());
                    instructionId2OrderRequestId.remove(order.getInstructionId());
                    updateOrderRequest(order.getInstructionId(), placedLimitOrder.getFilledQuantity(), placedLimitOrder.getCanceledQuantity(), OrderRequestStatus.FILLED, null);
                } else {
                    updateOrderRequest(order.getInstructionId(), placedLimitOrder.getFilledQuantity(), placedLimitOrder.getCanceledQuantity(), OrderRequestStatus.PARTIAL_FILLED, null);
                }
                return;
            }
        }
        LOGGER.warn(
                "Unpredicted Order(instructionId={}) is filled by Broker(price={},quantity={})",
                execution.getOrder().getInstructionId(), execution.getPrice(), execution.getQuantity());
    }


    @Override
    public void notify(InstructionRejectedEvent instructionRejected) {
        LOGGER.error("InstructionRejected. instructionId={}, reason={}.",
                instructionRejected.getInstructionId(), instructionRejected.getReason());
        placedOrder.remove(instructionRejected.getInstructionId());
        updateOrderRequest(instructionRejected.getInstructionId(), FixedPointNumber.ZERO, FixedPointNumber.ZERO, OrderRequestStatus.REJECT, instructionRejected.getReason());
    }

    private OrderExecution genOrderExecution(Execution execution) {
        OrderExecution orderExecution = new OrderExecution();
        Integer orderRequestId = instructionId2OrderRequestId.get(execution.getOrder().getInstructionId());
        orderExecution.setOrderRequestId(orderRequestId);
        orderExecution.setPrice(Double.parseDouble(execution.getPrice().toString()));
        orderExecution.setFilledQuantity(Integer.parseInt(execution.getQuantity().toString()));
        orderExecution.setCancelledQuantity(Integer.parseInt(execution.getCancelledQuantity().toString()));
        orderExecution.setExecutionDate(new Date());
        return orderExecution;
    }

    private void updateOrderRequest(String instructionId, FixedPointNumber filledQuantity, FixedPointNumber cancelledQuantity, OrderRequestStatus orderStatus, String errorMsg) {
        OrderRequestQuery query = new OrderRequestQuery();
        query.setInstructionId(instructionId);
        List<OrderRequest> orderRequests = orderRequestRepository.query(query);
        if (CollectionUtils.isEmpty(orderRequests)) {
            return;
        }
        OrderRequest orderRequest = orderRequests.get(0);
        orderRequest.setFilledQuantity(Integer.parseInt(filledQuantity.toString()));
        orderRequest.setCancelledQuantity(Integer.parseInt(cancelledQuantity.toString()));
        orderRequest.setStatus(orderStatus.name());
        if (StringUtils.isNotBlank(errorMsg)) {
            orderRequest.setErrorMsg(errorMsg);
        }
        orderRequest.setUpdateTime(new Date());
        orderRequestRepository.update(orderRequest);
    }

    public class PlaceOrderAcceptCallback implements OrderCallback {

        @Override
        public void onSuccess(String instructionId) {
            LOGGER.info("Order(instructionId={}) has bean accepted by Broker.", instructionId);
            updateOrderRequest(instructionId, FixedPointNumber.ZERO, FixedPointNumber.ZERO, OrderRequestStatus.ACCEPT, null);
        }

        @Override
        public void onFailure(FailureResponse failureResponse) {
            LOGGER.error("Order is rejected by Broker. reason={}", failureResponse);
        }
    }

    class PlaceOrderTask extends TimerTask {

        private Session session;

        PlaceOrderTask(Session session) {
            this.session = session;
        }

        @Override
        public void run() {

            FixedPointNumber quantity = FixedPointNumber.valueOf(4000000);
            FixedPointNumber offset = FixedPointNumber.valueOf(1000L);

            long time = System.currentTimeMillis();
            if (!lastBidPrice.equals(FixedPointNumber.ZERO)) {
                String instructionId = String.valueOf(time);
                FixedPointNumber limitPrice = FixedPointNumbers.toFixedPointNumber(
                        FixedPointNumbers.doubleValue(lastAskPrice), offset.negate());
                //place buy order
                LimitOrderSpecification limitOrderSpecification = new LimitOrderSpecification(
                        instrumentId, instructionId, limitPrice, quantity, TimeInForce.GOOD_FOR_DAY,
                        offset, offset);
                //save order request
                OrderRequest orderRequest = genOrderRequest(instrumentId, instructionId, limitPrice, quantity, TimeInForce.GOOD_FOR_DAY, offset, offset, OrderDirection.BUY, OrderType.LIMIT);
                int orderRequestId = orderRequestRepository.create(orderRequest);
                instructionId2OrderRequestId.put(instructionId, orderRequestId);
                placedOrder.putIfAbsent(instructionId, PlacedLimitOrder.create(instrumentId,
                        quantity, limitPrice, offset, offset, System.currentTimeMillis()));
                LOGGER.info("Place buy order. lastAskPrice={}, orderSpecification={}",
                        lastAskPrice, limitOrderSpecification);
                session.placeLimitOrder(limitOrderSpecification, new PlaceOrderAcceptCallback());
            }

            if (!lastAskPrice.equals(FixedPointNumber.ZERO)) {
                String instructionId = String.valueOf(time + 1);
                FixedPointNumber limitPrice = FixedPointNumbers.toFixedPointNumber(
                        FixedPointNumbers.doubleValue(lastBidPrice), offset);
                //place sell order
                LimitOrderSpecification limitOrderSpecification = new LimitOrderSpecification(
                        instrumentId, instructionId, limitPrice, quantity.negate(),
                        TimeInForce.GOOD_FOR_DAY, offset, offset);
                OrderRequest orderRequest = genOrderRequest(instrumentId, instructionId, limitPrice, quantity.negate(), TimeInForce.GOOD_FOR_DAY, offset, offset, OrderDirection.SELL, OrderType.LIMIT);
                int orderRequestId = orderRequestRepository.create(orderRequest);
                instructionId2OrderRequestId.put(instructionId, orderRequestId);
                placedOrder.putIfAbsent(instructionId, PlacedLimitOrder.create(instrumentId,
                        quantity.negate(), limitPrice, offset, offset, System.currentTimeMillis()));
                LOGGER.info("Place sell order. lastBidPrice={}, orderSpecification={}",
                        lastBidPrice, limitOrderSpecification);
                session.placeLimitOrder(limitOrderSpecification, new PlaceOrderAcceptCallback());
            }
        }

        private OrderRequest genOrderRequest(long instrumentId, String instructionId, FixedPointNumber limitPrice, FixedPointNumber quantity, TimeInForce timeInForce, FixedPointNumber stopLossPriceOffset, FixedPointNumber stopProfitPriceOffset, OrderDirection orderDirection, OrderType orderType) {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setInstrumentId(String.valueOf(instrumentId));
            orderRequest.setInstructionId(instructionId);
            orderRequest.setLimitPrice(Double.parseDouble(limitPrice.toString()));
            orderRequest.setQuantity(Integer.parseInt(quantity.toString()));
            orderRequest.setOrderDirection(orderDirection.name());
            orderRequest.setOrderType(orderType.name());
            orderRequest.setStopLossOffset(Double.parseDouble(stopLossPriceOffset.toString()));
            orderRequest.setStopProfitOffset(Double.parseDouble(stopProfitPriceOffset.toString()));
            orderRequest.setTimeInForce(timeInForce.name());
            orderRequest.setStatus(OrderRequestStatus.TO_BE_ACCEPT.name());
            orderRequest.setCreateTime(new Date());
            return orderRequest;
        }
    }

    public static class PlacedLimitOrder {
        private long instrumentId;

        private FixedPointNumber limitPrice;

        private FixedPointNumber stopLossPriceOffset;

        private FixedPointNumber stopProfitPriceOffset;

        private FixedPointNumber quantity;

        private volatile FixedPointNumber filledQuantity;

        private volatile FixedPointNumber pendingQuantity;

        private volatile FixedPointNumber canceledQuantity;

        private long placedTime;

        public static PlacedLimitOrder create(long instrumentId, FixedPointNumber quantity,
                                              FixedPointNumber limitPrice,
                                              FixedPointNumber stopLossPriceOffset,
                                              FixedPointNumber stopProfitPriceOffset,
                                              long placedTime) {
            PlacedLimitOrder placedLimitOrder = new PlacedLimitOrder();
            placedLimitOrder.setInstrumentId(instrumentId);
            placedLimitOrder.setQuantity(quantity);
            placedLimitOrder.setLimitPrice(limitPrice);
            placedLimitOrder.setStopLossPriceOffset(stopLossPriceOffset);
            placedLimitOrder.setStopProfitPriceOffset(stopProfitPriceOffset);
            placedLimitOrder.setPlacedTime(placedTime);
            placedLimitOrder.setFilledQuantity(FixedPointNumber.ZERO);
            placedLimitOrder.setPendingQuantity(quantity);
            placedLimitOrder.setCanceledQuantity(FixedPointNumber.ZERO);
            return placedLimitOrder;
        }

        public boolean isComplete() {
            return (canceledQuantity.longValue() + filledQuantity.longValue() == quantity
                    .longValue() && pendingQuantity.longValue() == 0);
        }

        public long getInstrumentId() {
            return instrumentId;
        }

        public void setInstrumentId(long instrumentId) {
            this.instrumentId = instrumentId;
        }

        public FixedPointNumber getLimitPrice() {
            return limitPrice;
        }

        public void setLimitPrice(FixedPointNumber limitPrice) {
            this.limitPrice = limitPrice;
        }

        public FixedPointNumber getStopLossPriceOffset() {
            return stopLossPriceOffset;
        }

        public void setStopLossPriceOffset(FixedPointNumber stopLossPriceOffset) {
            this.stopLossPriceOffset = stopLossPriceOffset;
        }

        public FixedPointNumber getStopProfitPriceOffset() {
            return stopProfitPriceOffset;
        }

        public void setStopProfitPriceOffset(FixedPointNumber stopProfitPriceOffset) {
            this.stopProfitPriceOffset = stopProfitPriceOffset;
        }

        public FixedPointNumber getQuantity() {
            return quantity;
        }

        public void setQuantity(FixedPointNumber quantity) {
            this.quantity = quantity;
        }

        public long getPlacedTime() {
            return placedTime;
        }

        public void setPlacedTime(long placedTime) {
            this.placedTime = placedTime;
        }

        public FixedPointNumber getFilledQuantity() {
            return filledQuantity;
        }

        public void setFilledQuantity(FixedPointNumber filledQuantity) {
            this.filledQuantity = filledQuantity;
        }

        public FixedPointNumber getPendingQuantity() {
            return pendingQuantity;
        }

        public void setPendingQuantity(FixedPointNumber pendingQuantity) {
            this.pendingQuantity = pendingQuantity;
        }

        public FixedPointNumber getCanceledQuantity() {
            return canceledQuantity;
        }

        public void setCanceledQuantity(FixedPointNumber canceledQuantity) {
            this.canceledQuantity = canceledQuantity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PlacedLimitOrder)) {
                return false;
            }

            PlacedLimitOrder that = (PlacedLimitOrder) o;

            if (instrumentId != that.instrumentId) {
                return false;
            }
            if (placedTime != that.placedTime) {
                return false;
            }
            if (!canceledQuantity.equals(that.canceledQuantity)) {
                return false;
            }
            if (!filledQuantity.equals(that.filledQuantity)) {
                return false;
            }
            if (!limitPrice.equals(that.limitPrice)) {
                return false;
            }
            if (!pendingQuantity.equals(that.pendingQuantity)) {
                return false;
            }
            if (!quantity.equals(that.quantity)) {
                return false;
            }
            if (stopLossPriceOffset != null ? !stopLossPriceOffset.equals(that.stopLossPriceOffset)
                    : that.stopLossPriceOffset != null) {
                return false;
            }
            if (stopProfitPriceOffset != null ? !stopProfitPriceOffset
                    .equals(that.stopProfitPriceOffset) : that.stopProfitPriceOffset != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (instrumentId ^ (instrumentId >>> 32));
            result = 31 * result + limitPrice.hashCode();
            result = 31 * result
                     + (stopLossPriceOffset != null ? stopLossPriceOffset.hashCode() : 0);
            result = 31 * result
                     + (stopProfitPriceOffset != null ? stopProfitPriceOffset.hashCode() : 0);
            result = 31 * result + quantity.hashCode();
            result = 31 * result + filledQuantity.hashCode();
            result = 31 * result + pendingQuantity.hashCode();
            result = 31 * result + canceledQuantity.hashCode();
            result = 31 * result + (int) (placedTime ^ (placedTime >>> 32));
            return result;
        }
    }

    public static class DateUtils {
        private static final String pattern = "YYYY-MM-DD HH:mm:ss,SSS";

        public static String format(long time) {
            Date date = new Date(time);
            DateFormat formatter = new SimpleDateFormat(pattern);
            String dateFormatted = formatter.format(date);
            return dateFormatted;
        }
    }

    public static void main(String[] args) {
        String url = "https://web-order.london-demo.lmax.com";
        String username = "quinn1019";
        String password = "cui390112";
        LoginRequest.ProductType productType = LoginRequest.ProductType.CFD_DEMO;

        LmaxApi lmaxApi = new LmaxApi(url);

        OrderWithStopLossDB orderWithStopLoss = new OrderWithStopLossDB();
        lmaxApi.login(new LoginRequest(username, password, productType), orderWithStopLoss);

    }

}
