package com.pickmebackend.config.jwt;

import com.pickmebackend.domain.dto.AccountDto;
import com.pickmebackend.properties.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
/**
 * Reference
 * https://dzone.com/articles/spring-boot-security-json-web-tokenjwt-hello-world
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider implements Serializable {

    private static final long TOKEN_VALIDITY =  5 * 60 * 60;

    private static final long serialVersionUID = -2550185165626007488L;

    private String secret;

    String getUsernameFromToken(String token)    {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Date getExpirationDateFromToken(String token)    {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token)    {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(AccountDto accountDto)    {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, accountDto.getEmail());
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {
        log.info(secret);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
