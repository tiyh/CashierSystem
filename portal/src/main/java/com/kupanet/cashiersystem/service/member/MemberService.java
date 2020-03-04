package com.kupanet.cashiersystem.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kupanet.cashiersystem.model.Member;
import com.kupanet.cashiersystem.util.CommonResult;

import java.util.Map;

public interface MemberService extends IService<Member> {
    /**
     * 根据用户名获取会员
     */
    Member getByUsername(String username);

    /**
     * 根据会员编号获取会员
     */
    Member getById(Long id);

    /**
     * 用户注册
     */
    CommonResult register(String username, String password, String telephone, String authCode);

    /**
     * 生成验证码
     */
    CommonResult generateAuthCode(String telephone);

    /**
     * 修改密码
     */
    CommonResult updatePassword(String telephone, String password, String authCode);

    /**
     * 获取当前登录会员
     */
    Member getCurrentMember();


    Map<String, Object> login(String username, String password);

    String refreshToken(String token);

    Object register(Member umsMember);
}
