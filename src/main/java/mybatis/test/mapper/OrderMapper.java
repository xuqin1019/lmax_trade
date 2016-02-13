package mybatis.test.mapper;

import mybatis.test.model.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: OrderMapper.java, v 0.1 2/13/16 5:11 PM qiancheng.xq Exp $
 */
public interface OrderMapper {
    @Select("SELECT * FROM `order` WHERE id = #{id}")
    @Results(value = { @Result(property = "id", column = "id"),
            @Result(property = "instrument", column = "instrument"),
            @Result(property = "orderType", column = "order_type"),
            @Result(property = "quantity", column = "quantity"), })
    Order selectOrder(int id);

    @Select("select * from `order`")
    @Results(value = { @Result(property = "id", column = "id"),
            @Result(property = "instrument", column = "instrument"),
            @Result(property = "orderType", column = "order_type"),
            @Result(property = "quantity", column = "quantity"), })
    List<Order> getAll();

    @Insert("INSERT INTO `order` (`instrument`, `order_type`, `quantity`) VALUES (#{instrument},#{orderType},#{quantity})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Order order);

    @Update("update `order` set instrument=#{instrument}, order_type=#{orderType}, quantity=#{quantity} where id=#{id}")
    void update(Order order);
}
