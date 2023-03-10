package com.rookie.bigdata.config;


import com.rookie.bigdata.filter.HttpServletRequestWrapFilter;
import com.rookie.bigdata.filter.JWTAuthenticationFilter;
import com.rookie.bigdata.filter.JWTAuthorizationFilter;
import com.rookie.bigdata.provider.UserPasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;


/**
 * @Classname SecurityConfig
 * @Description
 * @Author rookie
 * @Date 2023/3/3 17:26
 * @Version 1.0
 */
@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private UserPasswordAuthenticationProvider userPasswordAuthenticationProvider;

    @Autowired
    private CsrfTokenRepository csrfTokenMemoryRepository;

    /**
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf()
                //???????????????uri
                .ignoringAntMatchers("/login")
                //??????token?????????
                .csrfTokenRepository(csrfTokenMemoryRepository)
                .and()
                .authorizeRequests()
//                .antMatchers("/auth/**").permitAll()
                .anyRequest().hasAnyAuthority("ROLE_ADMIN")
                .and()
                .formLogin().disable()
                //???????????????????????????CsrfFilter????????????????????????????????????????????????
                .addFilterBefore(new HttpServletRequestWrapFilter(), CsrfFilter.class)
                //
                .addFilterAt(new JWTAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JWTAuthorizationFilter(), JWTAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();


    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * ???????????????
     *
     * @return
     */
    @Bean
    public AuthenticationManager authenticationManager() {

        return new ProviderManager(userPasswordAuthenticationProvider);

    }


}
