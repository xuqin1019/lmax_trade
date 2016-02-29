package com.lmax.trade.model;

import java.util.Date;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: OrderExecution.java, v 0.1 2/27/16 4:25 PM qiancheng.xq Exp $
 */
public class OrderExecution {

    private int id;

    private int orderRequestId;

    private OrderRequest orderRequest;

    private Double price;

    private int filledQuantity;

    private int cancelledQuantity;

    private Date executionDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderRequestId() {
        return orderRequestId;
    }

    public void setOrderRequestId(int orderRequestId) {
        this.orderRequestId = orderRequestId;
    }

    public OrderRequest getOrderRequest() {
        return orderRequest;
    }

    public void setOrderRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getFilledQuantity() {
        return filledQuantity;
    }

    public void setFilledQuantity(int filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    public int getCancelledQuantity() {
        return cancelledQuantity;
    }

    public void setCancelledQuantity(int cancelledQuantity) {
        this.cancelledQuantity = cancelledQuantity;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    @Override
    public String toString() {
        return "OrderExecution{" +
               "id=" + id +
               ", orderRequestId=" + orderRequestId +
               ", orderRequest=" + orderRequest +
               ", price='" + price + '\'' +
               ", filledQuantity=" + filledQuantity +
               ", cancelledQuantity=" + cancelledQuantity +
               ", executionDate=" + executionDate +
               '}';
    }
}
