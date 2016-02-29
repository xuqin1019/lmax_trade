package com.lmax.trade.model.query;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: OrderExecutionQuery.java, v 0.1 2/27/16 11:36 PM qiancheng.xq Exp $
 */
public class OrderExecutionQuery extends BaseQuery {
    private String orderRequestId;

    public String getOrderRequestId() {
        return orderRequestId;
    }

    public void setOrderRequestId(String orderRequestId) {
        this.orderRequestId = orderRequestId;
    }
}
