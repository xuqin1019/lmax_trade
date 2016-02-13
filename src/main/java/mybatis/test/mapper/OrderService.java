package mybatis.test.mapper;

import mybatis.test.model.Order;
import mybatis.test.util.MyBatisUtil;

import org.apache.ibatis.session.SqlSession;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: OrderService.java, v 0.1 2/13/16 7:31 PM qiancheng.xq Exp $
 */
public class OrderService {
    public void insertOrder(Order order) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
            orderMapper.insert(order);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
