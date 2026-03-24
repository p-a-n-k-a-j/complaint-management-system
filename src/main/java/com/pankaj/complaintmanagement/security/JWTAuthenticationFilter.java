package com.pankaj.complaintmanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pankaj.complaintmanagement.exception.payload.ErrorResponse;
import com.pankaj.complaintmanagement.util.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.message.ObjectArrayMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    @Autowired
    JWTAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService, ObjectMapper objectMapper){
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper =objectMapper;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        Claims claims = null;
        TokenType tokenType= null;
        if(requestHeader !=null && requestHeader.startsWith("Bearer ")){
            String token = requestHeader.substring(7);
           claims= jwtService.extractAllClaims(token);
           tokenType = jwtService.extractTokenType(claims);
         if(tokenType ==null ||claims ==null){
             filterChain.doFilter(request, response);
             return;
         }
        }


        //step 1 check if the requestUri is refresh or token is also refresh then send ErrorResponse
        String requestUri = request.getRequestURI();
        boolean isRefreshEndpoint = requestUri.contains("auth/refresh") || requestUri.contains("/refresh");
        boolean tryingWithRefreshEndpointOrAccessToken = isRefreshEndpoint && !Objects.equals(tokenType, TokenType.ACCESS);
        boolean tryingWithNotRefreshEndpointOrRefreshToken = !isRefreshEndpoint && Objects.equals(tokenType, TokenType.REFRESH);
        if(tryingWithRefreshEndpointOrAccessToken || tryingWithNotRefreshEndpointOrRefreshToken){
            ErrorResponse error = new ErrorResponse.Builder()
                    .status(HttpStatus.UNAUTHORIZED)
                            .message("Invalid token for this endpoint")
                                    .path(requestUri)
                                            .build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(error));
            return;
        }

        String username;
        CustomUserDetails customUserDetails=null;
        if(tokenType != TokenType.REFRESH){
            username = jwtService.extractUsername(claims);
            if(username != null){
                customUserDetails = userDetailsService.loadUserByUsername(username);
            }
        }

        if(customUserDetails != null && SecurityContextHolder.getContext().getAuthentication() == null){
            boolean isValid =jwtService.isValidAccessToken(claims, customUserDetails);
            if(isValid) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }
        filterChain.doFilter(request, response);

    }
}
