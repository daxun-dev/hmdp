package com.hmdp.utils;

import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取session
        HttpSession session = request.getSession();

        //2.获取session里面的用户
        Object user = session.getAttribute("user");

        //3.判断session里面的用户是否存在
        if(user == null ){
            //4.不存在返回异常
            response.setStatus(401);
            //不放行
            return false;
        }

        //5.存在，保存用户信息到threadlocal里面
        UserHolder.saveUser((UserDTO) user);

        //6.放行
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
