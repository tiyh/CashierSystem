package com.kupanet.cashiersystem.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kupanet.cashiersystem.model.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper  extends BaseMapper<CartItem> {

}
