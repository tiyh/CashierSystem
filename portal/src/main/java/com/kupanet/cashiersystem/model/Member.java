package com.kupanet.cashiersystem.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@TableName("cs_member")
public class Member implements UserDetails {
    private static final long serialVersionUID = 5569868658394399067L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private Integer status;

    @TableField("create_time")
    private Date createTime;
    @TableField("password_reset_time")
    private Date lastPasswordResetDate;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //返回当前用户的权限
        return Arrays.asList(new SimpleGrantedAuthority("TEST"));
    }

    public Long getId(){
        return id;
    }
    public void setId(Long mId){
        id=mId;
    }
    public void setPassword(String mPassword) {
         password=mPassword;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String mNickname) {
        nickname=mNickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String mPhone) {
        phone=mPhone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer mStatus) {
        status=mStatus;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String mUsername) {
        username=mUsername;
    }

    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date mDate) {
        createTime=mDate;
    }
    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }
    public void setLastPasswordResetDate(Date mDate) {
        lastPasswordResetDate=mDate;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == 1;
    }


}
