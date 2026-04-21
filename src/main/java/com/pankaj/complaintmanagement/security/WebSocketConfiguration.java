package com.pankaj.complaintmanagement.security;

import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private final JwtService jwtService;
    public WebSocketConfiguration(JwtService jwtService){
        this.jwtService = jwtService;
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
       registry.addEndpoint("/ws")
               .setAllowedOriginPatterns("*")
               .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry http) {
        http.enableSimpleBroker("/topic", "/queue"); //topic sab ke liye or queue private message
        http.setApplicationDestinationPrefixes("/app");
    }

    public void configureChannelInbound(ChannelRegistration registration){
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                //jab user phali baar connect karega
                if(StompCommand.CONNECT.equals(accessor.getCommand())){
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if(token != null && token.startsWith("Bearer ")){
                        String jwt = token.substring(7);
                        Claims claims = jwtService.extractAllClaims(jwt);
                        String userEmail = jwtService.extractUsername(claims);
                        List<SimpleGrantedAuthority> simpleGrantedAuthorities = jwtService.extractAuthority(claims);
                        // here we tell the spring security ki who is this gay
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userEmail, null, simpleGrantedAuthorities);
                        //Most important thing : yhi vo dori hai jo session ko user se bandti hai.
                        accessor.setUser(auth);
                    }
                }
                return message;
            }
        });
    }
}
