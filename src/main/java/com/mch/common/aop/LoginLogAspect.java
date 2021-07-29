package com.mch.common.aop;

import com.mch.common.util.IPUtils;
import com.mch.system.model.User;
import com.mch.system.service.LoginLogService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
@ConditionalOnProperty(value = "my-project.log.login", havingValue = "true")
public class LoginLogAspect {

    @Resource
    private LoginLogService loginLogService;

    @Pointcut("execution(com.mch.common.util.ResultBean com.mch.system.controller..LoginController.login(com.mch.system.model.User, String) )")
    public void loginLogPointCut() {}

    @After("loginLogPointCut()")
    public void recordLoginLog(JoinPoint joinPoint) {
        // 获取登陆参数
        Object[] args = joinPoint.getArgs();
        User user = (User) args[0];

        Subject subject = SecurityUtils.getSubject();

        String ip = IPUtils.getIpAddr();
        loginLogService.addLog(user.getUsername(), subject.isAuthenticated(), ip);
    }
}