package com.demo.dummy.config;

import com.demo.dummy.util.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTProvider {

    private static SecretKey key = Keys.hmacShaKeyFor(Constants.SECRET.getBytes());

    //generate jwt token using SecretKEy
    public static String generateToken(Authentication authentication) {
        String jwt = Jwts.builder().setIssuer("Shanawaj").setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("username", authentication.getName())
                .signWith(key).compact();

        return jwt;
    }

    //Extracting email or username from jwt
    public static String getUsernameFromJwtToken(String jwt) {
        //Bearer token
        jwt = jwt.substring(7);
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        String usernameOrEmail = String.valueOf(claims.get("username")); //this value should match with the one set in generateToken Method

        return usernameOrEmail;
    }
}
