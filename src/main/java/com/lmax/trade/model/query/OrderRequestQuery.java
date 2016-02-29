package com.lmax.trade.model.query;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: OrderRequestQuery.java, v 0.1 2/27/16 6:12 PM qiancheng.xq Exp $
 */
public class OrderRequestQuery extends BaseQuery {

    private String instructionId;

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId;
    }
}
