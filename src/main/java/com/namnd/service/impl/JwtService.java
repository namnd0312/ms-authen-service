package com.namnd.service.impl;

import com.namnd.config.ApplicationProperties;
import com.namnd.model.UserPrinciple;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtService {

    private final ApplicationProperties applicationProperties;

    private final RedisTemplate<String, String> redisTemplate;


    private static final Logger logger = LoggerFactory.getLogger(JwtService.class.getName());

    public JwtService(ApplicationProperties applicationProperties, RedisTemplate<String, String> redisTemplate) {
        this.applicationProperties = applicationProperties;

        this.redisTemplate = redisTemplate;
    }

    public String generateTokenLogin(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        List<String> roles = userPrinciple.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String jwt = Jwts.builder()
                .setSubject(userPrinciple.getUsername())
                .setIssuedAt(new Date())
                .claim("roles", roles)
                .setExpiration(new Date(new Date().getTime() + this.applicationProperties.getJwt().getExpirationTime() * 1000))
                .signWith(SignatureAlgorithm.HS512, this.applicationProperties.getJwt().getSecretKey())
                .compact();

        this.redisTemplate.opsForValue().set(userPrinciple.getUsername(), jwt);
        return jwt;
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(this.applicationProperties.getJwt().getSecretKey()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature -> Message: {} ", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: {}", e);
        }

        return false;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(this.applicationProperties.getJwt().getSecretKey())
                .parseClaimsJws(token)
                .getBody().getSubject();
    }
}
