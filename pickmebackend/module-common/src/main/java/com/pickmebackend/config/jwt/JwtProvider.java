package com.pickmebackend.config.jwt;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.AccountDto;
import static com.pickmebackend.properties.JwtConstants.SECRET;
import static com.pickmebackend.properties.JwtConstants.TOKEN_VALIDITY;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
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
@Component
@RequiredArgsConstructor
public class JwtProvider implements Serializable {

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
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token)    {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(Account account)    {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", account.getId());
        claims.put("email", account.getEmail());
        return doGenerateToken(claims, account.getEmail());
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
