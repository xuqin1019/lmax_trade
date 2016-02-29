package com.lmax.trade;

import junit.framework.Assert;
import com.lmax.trade.model.OrderExecution;
import com.lmax.trade.model.OrderRequest;
import com.lmax.trade.model.query.OrderExecutionQuery;
import com.lmax.trade.model.query.OrderRequestQuery;
import com.lmax.trade.repository.OrderExecutionRepository;
import com.lmax.trade.repository.OrderRequestRepository;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: RepositoryTest.java, v 0.1 2/13/16 5:00 PM qiancheng.xq Exp $
 */
public class RepositoryTest {

    private static OrderRequestRepository orderRequestRepository;

    private static OrderExecutionRepository orderExecutionRepository;

    @BeforeClass
    public static void init() {
        orderRequestRepository = new OrderRequestRepository();
        orderExecutionRepository = new OrderExecutionRepository();
    }

    @Test
    public void testOrderRequestService() {
        //query
        OrderRequest orderRequest = queryRequestById(1);
        Assert.assertNotNull(orderRequest);
        Assert.assertEquals(1, orderRequest.getOrderExecutions().size());
        orderRequest = queryRequestById(-1);
        Assert.assertNull(orderRequest);

        //add
        orderRequest = new OrderRequest();
        orderRequest.setOrderId("123");
        orderRequest.setAccountId("456");
        orderRequest.setInstructionId("instructionId");
        orderRequest.setOrderType("orderType");
        orderRequest.setQuantity(0);
        orderRequest.setCreateTime(new Date());
        int orderRequestId = orderRequestRepository.create(orderRequest);
        Assert.assertTrue(orderRequestId > 0);

        //update
        orderRequest = queryRequestById(orderRequestId);
        Assert.assertNotNull(orderRequest);
        orderRequest.setInstructionId(orderRequest.getInstructionId() + "_updated");
        orderRequest.setUpdateTime(new Date());
        orderRequestRepository.update(orderRequest);
        orderRequest = queryRequestById(orderRequestId);
        Assert.assertNotNull(orderRequest);
        Assert.assertTrue(orderRequest.getInstructionId().endsWith("_updated"));

        //remove
        orderRequestRepository.delete(orderRequestId);
        orderRequest = queryRequestById(orderRequestId);
        Assert.assertNull(orderRequest);
    }

    private OrderRequest queryRequestById(int id) {
        OrderRequestQuery query = new OrderRequestQuery();
        query.setId(id);
        List<OrderRequest> results = orderRequestRepository.query(query);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    private OrderExecution queryExecutionById(int id) {
        OrderExecutionQuery query = new OrderExecutionQuery();
        query.setId(id);
        List<OrderExecution> results = orderExecutionRepository.query(query);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    @Test
    public void testOrderExecutionService() {
        //query
        OrderExecution orderExecution = queryExecutionById(1);
        Assert.assertNotNull(orderExecution);
        Assert.assertNotNull(orderExecution.getOrderRequest());

        int orderRequestId = orderExecution.getOrderRequest().getId();

        //insert
        orderExecution = new OrderExecution();
        orderExecution.setCancelledQuantity(1);
        orderExecution.setExecutionDate(new Date());
        orderExecution.setOrderRequestId(orderRequestId);
        orderExecution.setPrice(2.0);
        int orderExecutionId = orderExecutionRepository.create(orderExecution);
        Assert.assertTrue(orderExecutionId > 1);

        orderExecution = queryExecutionById(orderExecutionId);
        orderExecution.setCancelledQuantity(0);
        orderExecutionRepository.update(orderExecution);

        orderExecution = queryExecutionById(orderExecutionId);
        Assert.assertTrue(orderExecution.getCancelledQuantity() == 0);

        orderExecutionRepository.delete(orderExecutionId);
        orderExecution = queryExecutionById(orderExecutionId);
        Assert.assertNull(orderExecution);

    }
}
