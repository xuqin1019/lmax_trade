package com.lmax.trade.mapper;

import java.io.Serializable;
import java.util.List;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: BaseMapper.java, v 0.1 2/27/16 6:09 PM qiancheng.xq Exp $
 */
public interface BaseMapper<T, Q, PK extends Serializable> {
    List<T> getAll();

    List<T> query(Q q);

    int create(T t);

    void update(T t);

    void delete(PK id);
}
