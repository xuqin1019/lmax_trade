package com.lmax.trade.repository;

import com.lmax.trade.mapper.BaseMapper;
import com.lmax.trade.mapper.OrderRequestMapper;
import com.lmax.trade.model.OrderRequest;
import com.lmax.trade.model.query.OrderRequestQuery;
import com.lmax.trade.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: OrderRequestRepository.java, v 0.1 2/27/16 7:41 PM qiancheng.xq Exp $
 */
public class OrderRequestRepository implements BaseMapper<OrderRequest, OrderRequestQuery, Integer> {

    @Override
    public List<OrderRequest> getAll() {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderRequestMapper orderRequestMapper = sqlSession.getMapper(OrderRequestMapper.class);
            List<OrderRequest> orderRequests = orderRequestMapper.getAll();
            sqlSession.commit();
            return orderRequests;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<OrderRequest> query(OrderRequestQuery query) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderRequestMapper orderRequestMapper = sqlSession.getMapper(OrderRequestMapper.class);
            List<OrderRequest> requests = orderRequestMapper.query(query);
            sqlSession.commit();
            return requests;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public int create(OrderRequest orderRequest) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderRequestMapper orderRequestMapper = sqlSession.getMapper(OrderRequestMapper.class);
            orderRequestMapper.create(orderRequest);
            sqlSession.commit();
            return orderRequest.getId();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void update(OrderRequest orderRequest) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderRequestMapper orderRequestMapper = sqlSession.getMapper(OrderRequestMapper.class);
            orderRequestMapper.update(orderRequest);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void delete(Integer id) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            OrderRequestMapper orderRequestMapper = sqlSession.getMapper(OrderRequestMapper.class);
            orderRequestMapper.delete(id);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
