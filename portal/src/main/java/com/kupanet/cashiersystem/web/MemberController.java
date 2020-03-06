package com.kupanet.cashiersystem.web;

import com.kupanet.cashiersystem.model.Member;
import com.kupanet.cashiersystem.service.member.MemberService;
import com.kupanet.cashiersystem.util.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Value("${jwt.header}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @GetMapping(value = "/member/login")
    @ResponseBody
    public Object login(Member member) {
        if (member==null){
            return new CommonResult().validateFailed("用户名或密码错误");
        }
        try {
            Map<String, Object> token = memberService.login(member.getUsername(), member.getPassword());
            if (token.get("token") == null) {
                return new CommonResult().validateFailed("用户名或密码错误");
            }
            return new CommonResult().success(token);
        } catch (AuthenticationException e) {
            return new CommonResult().validateFailed("用户名或密码错误");
        }

    }

    @PostMapping(value = "/member/register")
    @ResponseBody
    public Object register(Member member) {
        if (member==null){
            return new CommonResult().validateFailed("用户名或密码错误");
        }
        return memberService.register(member);
    }
    @GetMapping(value = "/member/getAuthCode")
    @ResponseBody
    public Object getAuthCode(@RequestParam String telephone) {
        return memberService.generateAuthCode(telephone);
    }

    @PutMapping(value = "/member/updatePassword")
    @ResponseBody
    public Object updatePassword(@RequestParam String telephone,
                                 @RequestParam String password,
                                 @RequestParam String authCode) {
        return memberService.updatePassword(telephone, password, authCode);
    }
    @GetMapping("/member/all")
    @ResponseBody
    public Object user() {
        Member member = memberService.getCurrentMember();
        if (member != null && member.getId() != null) {
            return new CommonResult().success(member);
        }
        return new CommonResult().failed();

    }

    @GetMapping(value = "/member/token/refresh")
    @ResponseBody
    public Object refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = memberService.refreshToken(token);
        if (refreshToken == null) {
            return new CommonResult().failed();
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return new CommonResult().success(tokenMap);
    }
    //TODO
    @DeleteMapping(value = "/member/logout")
    @ResponseBody
    public Object logout() {
        return new CommonResult().success(null);
    }
}
