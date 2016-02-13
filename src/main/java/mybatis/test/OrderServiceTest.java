package mybatis.test;

import mybatis.test.mapper.OrderService;
import mybatis.test.model.Order;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: OrderServiceTest.java, v 0.1 2/13/16 7:38 PM qiancheng.xq Exp $
 */

public class OrderServiceTest {

    private static OrderService orderService;

    @BeforeClass
    public static void init() {
        orderService = new OrderService();
    }

    @Test
    public void insert() {
        Order order = new Order();
        order.setInstrument("test");
        order.setOrderType("type");
        order.setQuantity(1);
        orderService.insertOrder(order);
    }
}
