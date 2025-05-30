package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.services.JwtBlackListService;
import com.ecommerce.project.security.services.UserDetailsImplementation;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSceret}")
    private String jwtSecret;
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.app.jwtCookieName}")
    private String jwtCookie;

    @Autowired
    private JwtBlackListService jwtBlackListService;

//    public String getJwtFromHeader(HttpServletRequest request){
//        String bearerToken = request.getHeader("Authorization");
//        logger.debug("bearerToken: {}", bearerToken);
//        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            System.out.println("Cookie:" + cookie.getValue());
            return cookie.getValue();
        }
        else{
            return null;
        }
    }

    public ResponseCookie generateJwtCookie(UserDetailsImplementation userPrinciple) {
        String jwt = generateTokenFromUsername(userPrinciple.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookie,jwt).path("/api").maxAge(24*60*60).httpOnly(false).build();
        return cookie;
    }

    public void getCleanJwtCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("springBootEcom", "")
                .path("/api")
                .httpOnly(true)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime()+jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateToken(String authToken) {
        try{
            System.out.println("Validate Token");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e){
            logger.error("JWT token is expired : {}", e.getMessage());
        } catch (UnsupportedJwtException e){
            logger.error("JWT token is unsupported : {}", e.getMessage());
        } catch (IllegalArgumentException e){
            logger.error("JWT claim string is empty : {}", e.getMessage());
        }
        return false;
    }

    public void logout(String token){
        jwtBlackListService.addJwtToBlackList(token);
        System.out.println("logout jwt token: " + token);
        SecurityContextHolder.clearContext();
    }

    public boolean isTokenInBlackList(String token){
        System.out.println("isTokenVaild token: " + token);
        System.out.println(jwtBlackListService.isJwtInBlackList(token));
        return jwtBlackListService.isJwtInBlackList(token);
    }
}
