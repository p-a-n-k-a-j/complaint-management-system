package com.pankaj.complaintmanagement.security;

import com.pankaj.complaintmanagement.util.DurationHelper;
import com.pankaj.complaintmanagement.util.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
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
                .claim("token_type", TokenType.ACCESS.name())

                .claim("role", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))

                .signWith(getPrivateKey())
                .compact();
    }
//this will generate refresh token
    public String refreshToken(String email){
        return Jwts.builder().setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRY))
                .claim("token_type", TokenType.REFRESH.name())
                .signWith(getPrivateKey())
                .compact();

    }
    public TokenType extractTokenType(Claims claims) {
        return TokenType.valueOf(claims.get("token_type", String.class));
    }
    public List<SimpleGrantedAuthority> extractAuthority(Claims claims) {
        //we will get roles in the form of list
        List<String> roles = claims.get("role", List.class);
        if(roles == null)new ArrayList<>();
        //after get roles in String format, we will convert into SimpleGrantAuthority
       return roles.stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String extractUsername(Claims claims){
        return claims.getSubject();
    }
    public boolean isExpired(Claims claims){
        return claims.getExpiration().before(new Date());
    }




    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getPrivateKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean isValidAccessToken(Claims claims, UserDetails userDetails){
            boolean notExpired = !isExpired(claims);
            boolean usernameMatched = Objects.equals(extractUsername(claims), userDetails.getUsername());
            boolean isAccessToken = Objects.equals(extractTokenType(claims), TokenType.ACCESS);

            return usernameMatched && notExpired && isAccessToken;

    }
    public boolean isValidRefreshToken(Claims claims){

        boolean notExpired = !isExpired(claims);
        boolean isRefreshToken = Objects.equals(extractTokenType(claims), TokenType.REFRESH);

        return  notExpired && isRefreshToken;

    }
    public boolean isValidToken(Claims claims, UserDetails userDetails){
        boolean notExpired = !isExpired(claims);
        boolean usernameMatched = Objects.equals(extractUsername(claims), userDetails.getUsername());
        return usernameMatched && notExpired;

    }

}
