package com.rookie.bigdata.filter;


import com.alibaba.fastjson.JSON;
import com.rookie.bigdata.domain.CustomUserDetails;
import com.rookie.bigdata.domain.User;
import com.rookie.bigdata.util.JWTUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Classname JWTAuthenticationFilter
 * @Description
 * @Author rookie
 * @Date 2023/3/10 11:28
 * @Version 1.0
 */
@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        // 浏览器访问 http://localhost:18902/auth/login 会通过 JWTAuthenticationFilter
        super.setFilterProcessesUrl("/auth/login");
        super.setUsernameParameter("name");
    }

    /**
     * 在AbstractAuthenticationProcessingFilter.requiresAuthentication匹配的时候只有 /auth/login请求uri才会匹配该过滤器
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 数据是通过 requestBody 传输
        User user = JSON.parseObject(request.getInputStream(), StandardCharsets.UTF_8, User.class);

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword())
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        log.debug("authentication filter successful authentication: {}", authResult);

        // 如果验证成功, 就生成 Token 并返回
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        response.setHeader("access-token",
                JWTUtils.TOKEN_PREFIX + JWTUtils.create(customUserDetails.getName(), false, customUserDetails));
    }

    /**
     * 如果 attemptAuthentication 抛出 AuthenticationException 则会调用这个方法
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        log.debug("authentication filter unsuccessful authentication: {}", failed.getMessage());
        response.getWriter().write("authentication failed, reason: " + failed.getMessage());
    }
}
