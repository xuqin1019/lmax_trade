package com.lmax.trade.model;

import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: OrderRequest.java, v 0.1 2/27/16 4:20 PM qiancheng.xq Exp $
 */
public class OrderRequest {

    private int id;

    private String instrumentId;

    private String orderId;

    private String accountId;

    private String instructionId;

    private String originalInstructionId;

    private String orderType;

    private String orderDirection;

    private String timeInForce;

    private Double limitPrice;

    private Double stopLossOffset;

    private Double stopProfitOffset;

    private List<OrderExecution> orderExecutions;

    private int quantity;

    private int filledQuantity;

    private int cancelledQuantity;

    private String status;

    private String errorMsg;

    private Date createTime;

    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId;
    }

    public String getOriginalInstructionId() {
        return originalInstructionId;
    }

    public void setOriginalInstructionId(String originalInstructionId) {
        this.originalInstructionId = originalInstructionId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public Double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(Double limitPrice) {
        this.limitPrice = limitPrice;
    }

    public Double getStopLossOffset() {
        return stopLossOffset;
    }

    public void setStopLossOffset(Double stopLossOffset) {
        this.stopLossOffset = stopLossOffset;
    }

    public Double getStopProfitOffset() {
        return stopProfitOffset;
    }

    public void setStopProfitOffset(Double stopProfitOffset) {
        this.stopProfitOffset = stopProfitOffset;
    }

    public List<OrderExecution> getOrderExecutions() {
        return orderExecutions;
    }

    public void setOrderExecutions(List<OrderExecution> orderExecutions) {
        this.orderExecutions = orderExecutions;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
               "id=" + id +
               ", instrumentId='" + instrumentId + '\'' +
               ", orderId='" + orderId + '\'' +
               ", accountId='" + accountId + '\'' +
               ", instructionId='" + instructionId + '\'' +
               ", originalInstructionId='" + originalInstructionId + '\'' +
               ", orderType='" + orderType + '\'' +
               ", orderDirection='" + orderDirection + '\'' +
               ", timeInForce='" + timeInForce + '\'' +
               ", limitPrice=" + limitPrice +
               ", stopLossOffset=" + stopLossOffset +
               ", stopProfitOffset=" + stopProfitOffset +
               ", orderExecutions=" + orderExecutions +
               ", quantity=" + quantity +
               ", filledQuantity=" + filledQuantity +
               ", cancelledQuantity=" + cancelledQuantity +
               ", status='" + status + '\'' +
               ", errorMsg='" + errorMsg + '\'' +
               ", createTime=" + createTime +
               ", updateTime=" + updateTime +
               '}';
    }
}
