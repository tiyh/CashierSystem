package com.kupanet.cashiersystem.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kupanet.cashiersystem.DAO.MemberMapper;
import com.kupanet.cashiersystem.model.Member;
import com.kupanet.cashiersystem.service.member.MemberService;
import com.kupanet.cashiersystem.util.CommonResult;
import com.kupanet.cashiersystem.util.JwtTokenUtil;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member>
        implements MemberService {

    @Autowired
    private MemberMapper memberMapper;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberServiceImpl.class);
    /* @Resource
     private AuthenticationManager authenticationManager;*/
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${redis.key.prefix.authCode}")
    private String REDIS_KEY_PREFIX_AUTH_CODE;
    @Value("${authCode.expire.seconds}")
    private Long AUTH_CODE_EXPIRE_SECONDS;
    @Value("${jwt.tokenHead}")
    private String tokenHead;


    @Override
    public Member getByUsername(String username) {
        Member member = new Member();
        member.setUsername(username);
        return memberMapper.selectOne(new QueryWrapper<>(member));
    }

    @Override
    public Member getById(Long id) {
        return memberMapper.selectById(id);
    }

    @Override
    @Transactional
    public CommonResult register(String username, String password, String telephone, String authCode) {

        //没有该用户进行添加操作
        Member member = new Member();
        member.setUsername(username);
        member.setPhone(telephone);
        member.setPassword(password);
        this.register(member);
        return new CommonResult().success("注册成功", null);
    }
    @Override
    @Transactional
    public CommonResult register(Member user) {

        Member memberByRequest = new Member();
        memberByRequest.setUsername(user.getUsername());
        memberByRequest.setPassword(passwordEncoder.encode(user.getPassword()));
        Member memberInDatabase = memberMapper.selectOne(new QueryWrapper<>(memberByRequest));
        if (memberInDatabase!=null) {
            return new CommonResult().failed("该用户已经存在");
        }
        Member member = new Member();
        member.setUsername(user.getUsername());
        member.setPhone(user.getPhone());
        member.setPassword(passwordEncoder.encode(user.getPassword()));
        member.setCreateTime(new Date());
        member.setStatus(1);
        member.setLastPasswordResetDate(new Date());

        memberMapper.insert(member);
        member.setPassword(null);
        return new CommonResult().success("注册成功", null);
    }

    @Override
    public CommonResult generateAuthCode(String telephone) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        //验证码绑定手机号并存储到redis
        redisUtil.set(REDIS_KEY_PREFIX_AUTH_CODE + telephone, sb.toString());
        redisUtil.expire(REDIS_KEY_PREFIX_AUTH_CODE + telephone, AUTH_CODE_EXPIRE_SECONDS);
        return new CommonResult().success("获取验证码成功", sb.toString());
    }

    @Override
    @Transactional
    public CommonResult updatePassword(String telephone, String password, String authCode) {
        Member memberByRequest = new Member();
        memberByRequest.setPhone(telephone);
        Member member = memberMapper.selectOne(new QueryWrapper<>(memberByRequest));
        if (member==null) {
            return new CommonResult().failed("该账号不存在");
        }
        if (!verifyAuthCode(authCode, telephone)) {
            return new CommonResult().failed("验证码错误");
        }

        member.setPassword(passwordEncoder.encode(password));
        member.setLastPasswordResetDate(new Date());
        memberMapper.updateById(member);
        return new CommonResult().success("密码修改成功", null);
    }

    @Override
    public Member getCurrentMember() {
        try {
            SecurityContext ctx = SecurityContextHolder.getContext();
            Authentication auth = ctx.getAuthentication();
            Member member = (Member) auth.getPrincipal();
            return member;
        }catch (Exception e){
            return new Member();
        }
    }

    //对输入的验证码进行校验
    private boolean verifyAuthCode(String authCode, String telephone) {
        if (authCode==null||authCode.isEmpty()) {
            return false;
        }
        String realAuthCode = redisUtil.get(REDIS_KEY_PREFIX_AUTH_CODE + telephone).toString();
        return authCode.equals(realAuthCode);
    }


    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> tokenMap = new HashMap<>();
        String token = null;
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, passwordEncoder.encode(password));
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码不正确");
            }
            Member member = this.getByUsername(username);
            //   Authentication authentication = authenticationManager.authenticate(authenticationToken);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,null,userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
            tokenMap.put("userInfo",member);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }

        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);

        return tokenMap;

    }
    @Override
    public String refreshToken(String oldToken) {
        String token = oldToken.substring(tokenHead.length());
        if (jwtTokenUtil.canRefresh(token)) {
            return jwtTokenUtil.refreshToken(token);
        }
        return null;
    }

}
