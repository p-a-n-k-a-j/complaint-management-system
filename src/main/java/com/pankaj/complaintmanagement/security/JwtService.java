package com.pankaj.complaintmanagement.security;

import com.pankaj.complaintmanagement.util.DurationHelper;
import com.pankaj.complaintmanagement.util.TokenType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Key;
import java.util.Date;

public class JwtService {
    @Value("${jwt-secret}")
    private  String JWT_SECRET;
    private long ACCESS_EXPIRY = DurationHelper.MINUTE.of(30);
    private long REFRESH_EXPIRY = DurationHelper.DAY.of(7);

    private Key getPrivateKey(){
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public String accessToken(UserDetails userDetails){
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRY))
                .setIssuedAt(new Date())
                .claim("token_type", TokenType.ACCESS)
                .claim("role", userDetails.getAuthorities())
                .signWith(getPrivateKey())
                .compact();
    }

}
