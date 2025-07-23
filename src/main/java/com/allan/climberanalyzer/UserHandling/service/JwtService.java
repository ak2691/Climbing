package com.allan.climberanalyzer.UserHandling.service;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.allan.climberanalyzer.UserHandling.model.UserProfile;
import com.allan.climberanalyzer.UserHandling.repo.UserProfileRepo;
import com.allan.climberanalyzer.UserHandling.repo.UserRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${app.jwtSecret}")
    private String secretKey;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserProfileRepo userProfileRepo;
    private final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public String generatetoken(String username) throws IllegalAccessException {
        Map<String, Object> claims = new HashMap<>();
        Long userId = userRepo.findIdByUsername(username).orElse(null);

        claims.put("id", userId);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .and()
                .signWith(getKey())
                .compact();

    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Long.valueOf(claims.get("id").toString());
    }

    public UserProfile getUserProfileFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Long userId = Long.valueOf(claims.get("id").toString());
        UserProfile userProfile = userProfileRepo.findByUserId(userId).orElse(null);
        return userProfile;
    }

    /*
     * public UserProfile getUserProfileFromToken(String token) {
     * Claims claims = getAllClaimsFromToken(token);
     * UserProfile profile = new UserProfile();
     * 
     * if (claims.get("fingerStrengthGrade") != null) {
     * profile.setFingerStrengthGrade(claims.get("fingerStrengthGrade").toString());
     * }
     * if (claims.get("pullingStrengthGrade") != null) {
     * profile.setPullingStrengthGrade(claims.get("pullingStrengthGrade").toString()
     * );
     * }
     * if (claims.get("verticalGrade") != null) {
     * profile.setVerticalGrade(claims.get("verticalGrade").toString());
     * }
     * if (claims.get("overhangGrade") != null) {
     * profile.setOverhangGrade(claims.get("overhangGrade").toString());
     * }
     * if (claims.get("slabGrade") != null) {
     * profile.setSlabGrade(claims.get("slabGrade").toString());
     * }
     * if (claims.get("heightCm") != null) {
     * profile.setHeightCm(Double.valueOf(claims.get("heightCm").toString()));
     * }
     * if (claims.get("heightIn") != null) {
     * profile.setHeightIn(Double.valueOf(claims.get("heightIn").toString()));
     * }
     * if (claims.get("weightKg") != null) {
     * profile.setWeightKg(Double.valueOf(claims.get("weightKg").toString()));
     * }
     * if (claims.get("weightLb") != null) {
     * profile.setWeightLb(Double.valueOf(claims.get("weightLb").toString()));
     * }
     * 
     * return profile;
     * }
     */
}
