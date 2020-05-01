package com.kupanet.cashiersystem.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class CookieUtil {
    public static String getCookie(HttpServletRequest request, String cookieName){

        Cookie[] cookies =  request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(cookieName)){
                    String decodeValue = cookie.getValue();
                    try {
                        decodeValue =URLDecoder.decode(cookie.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return decodeValue;
                }
            }
        }

        return null;
    }
    public static void setCookie(HttpServletResponse response, String cookieName, String value){
        setCookie(response,cookieName,cookieName,-1);
    }

    public static void setCookie(HttpServletResponse response, String cookieName, String value, int cookieMaxAge){
        String encodeValue  = value;
        try {
            encodeValue = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Cookie cookie = new Cookie(cookieName,encodeValue);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response, String cookieName){
        setCookie(response,cookieName,null,0);
    }
}
