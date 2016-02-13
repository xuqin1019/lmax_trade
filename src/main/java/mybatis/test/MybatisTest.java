package mybatis.test;

import mybatis.test.mapper.OrderMapper;
import mybatis.test.model.Order;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: MybatisTest.java, v 0.1 2/13/16 5:00 PM qiancheng.xq Exp $
 */
public class MybatisTest {

    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession session = sqlSessionFactory.openSession();

        List<Order> orderList = new ArrayList<Order>();
        for (int i = 1; i <= 5; ++i) {
            Order order = new Order();
            order.setInstrument("instrument" + i);
            order.setOrderType("orderType" + i);
            order.setQuantity(i);
            orderList.add(order);
        }

        try {
            OrderMapper mapper = session.getMapper(OrderMapper.class);
            // do work
            for (Order order : orderList) {
                mapper.insert(order);
            }
            List<Order> all = mapper.getAll();
            for (Order order : all) {
                System.out.println(order);
            }
            session.commit();
        } finally {
            session.close();
        }
    }
}
