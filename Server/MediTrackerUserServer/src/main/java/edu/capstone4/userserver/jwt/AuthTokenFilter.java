package edu.capstone4.userserver.jwt;

import java.io.IOException;

import edu.capstone4.userserver.exceptions.BusinessException;
import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.models.User;
import edu.capstone4.userserver.services.DoctorService;
import edu.capstone4.userserver.services.UserDetailsServiceImpl;
import edu.capstone4.userserver.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


public class AuthTokenFilter extends OncePerRequestFilter {
  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private UserService userService;

  @Autowired
  private DoctorService doctorService;

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String jwt = parseJwt(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long userId = jwtUtils.getUserIdFromJwtToken(jwt);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 检查用户是否启用，医生是否已激活（如果用户有 Doctor 信息）
        Doctor doctor = null;
        try {
         doctor = doctorService.getDoctorByUserId(userId);
        } catch (BusinessException e) {
          // 如果用户是医生，但是没有 Doctor 信息，不做处理
        }

        User user = userService.getUserById(userId);
        if (user != null && !user.isEnabled() || (doctor != null && !doctor.isActivated())) {
          logger.warn("User account is not active or doctor account is not activated: " + username);
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User or doctor account is not activated");
          return; // 中止后续认证
        }

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.info("Set SecurityContext for user: {}", userDetails.getUsername());
        logger.info("Authentication Authorities: {}", userDetails.getAuthorities());
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e);
    }

    filterChain.doFilter(request, response);
    logger.debug("Exiting JWT Filter for request: " + request.getRequestURI());
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }

    return null;
  }
}
