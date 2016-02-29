package com.lmax.trade.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: MyBatisUtil.java, v 0.1 2/13/16 7:32 PM qiancheng.xq Exp $
 */
public class MyBatisUtil {
    private static SqlSessionFactory factory;

    private MyBatisUtil() {
    }

    static {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        factory = new SqlSessionFactoryBuilder().build(reader);
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return factory;
    }
}
