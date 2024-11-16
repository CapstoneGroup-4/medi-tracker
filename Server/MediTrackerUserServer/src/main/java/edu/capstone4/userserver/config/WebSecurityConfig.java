package edu.capstone4.userserver.config;

import edu.capstone4.userserver.jwt.AuthEntryPointJwt;
import edu.capstone4.userserver.jwt.AuthTokenFilter;
import edu.capstone4.userserver.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfig {
  private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    log.info("In authenticationJwtTokenFilter");
    return new AuthTokenFilter();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // 1. 禁用 CSRF（前后端分离通常禁用）
    http.csrf(csrf -> csrf.disable())
            // 2. 设置未授权处理逻辑
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            // 3. 设置为无状态的会话管理，RESTful API 不需要保存 session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 4. 配置允许未认证的端点，其他端点需要认证
            .authorizeHttpRequests(auth ->
                    auth.requestMatchers("/api/auth/**").permitAll()  // 允许未认证访问 /api/auth/signup 和 /api/auth/signin
                            .requestMatchers("/api/verification/**").permitAll()
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/index.html").permitAll()

                            // 5. 基于角色的授权控制
                            .requestMatchers("/api/doctor/**").hasRole("DOCTOR") // 仅医生可以访问 /api/doctor/** 端点
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")  // 仅管理员可以访问 /api/admin/** 端点
                            .anyRequest().authenticated()// 其他所有请求需要认证
            );

    // 6. 配置认证提供者
    http.authenticationProvider(authenticationProvider());
    // 7. 添加 JWT 过滤器，在 UsernamePasswordAuthenticationFilter 之前执行
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    // 8. 构建配置
    return http.build();
  }

  @Bean
  public GrantedAuthorityDefaults grantedAuthorityDefaults() {
    return new GrantedAuthorityDefaults(""); // Remove the "ROLE_" prefix
  }
}
