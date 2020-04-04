package com.kupanet.cashiersystem.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kupanet.cashiersystem.model.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    Product getUpdateInfo(Long id);
    List<Product> selectByCategoryId(Long id);
}
