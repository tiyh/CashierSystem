package com.kupanet.cashiersystem.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kupanet.cashiersystem.model.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}
