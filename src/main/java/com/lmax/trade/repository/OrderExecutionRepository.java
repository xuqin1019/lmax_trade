package com.lmax.trade.repository;

import com.lmax.trade.mapper.BaseMapper;
import com.lmax.trade.mapper.OrderExecutionMapper;
import com.lmax.trade.model.OrderExecution;
import com.lmax.trade.model.query.OrderExecutionQuery;
import com.lmax.trade.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: OrderExecutionRepository.java, v 0.1 2/27/16 8:03 PM qiancheng.xq Exp $
 */
public class OrderExecutionRepository implements BaseMapper<OrderExecution, OrderExecutionQuery, Integer> {

    @Override
    public List<OrderExecution> getAll() {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderExecutionMapper orderExecutionMapper = sqlSession.getMapper(OrderExecutionMapper.class);
            List<OrderExecution> orderExecutions = orderExecutionMapper.getAll();
            sqlSession.commit();
            return orderExecutions;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<OrderExecution> query(OrderExecutionQuery orderExecutionQuery) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderExecutionMapper orderExecutionMapper = sqlSession.getMapper(OrderExecutionMapper.class);
            List<OrderExecution> orderExecutions = orderExecutionMapper.query(orderExecutionQuery);
            sqlSession.commit();
            return orderExecutions;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public int create(OrderExecution orderExecution) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderExecutionMapper orderExecutionMapper = sqlSession.getMapper(OrderExecutionMapper.class);
            orderExecutionMapper.create(orderExecution);
            sqlSession.commit();
            return orderExecution.getId();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void update(OrderExecution orderExecution) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderExecutionMapper orderExecutionMapper = sqlSession.getMapper(OrderExecutionMapper.class);
            orderExecutionMapper.update(orderExecution);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void delete(Integer id) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderExecutionMapper orderExecutionMapper = sqlSession.getMapper(OrderExecutionMapper.class);
            orderExecutionMapper.delete(id);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
