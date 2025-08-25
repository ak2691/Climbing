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

import com.allan.climberanalyzer.UserHandling.model.RefreshToken;
import com.allan.climberanalyzer.UserHandling.model.UserProfile;
import com.allan.climberanalyzer.UserHandling.repo.RefreshTokenRepo;
import com.allan.climberanalyzer.UserHandling.repo.UserProfileRepo;
import com.allan.climberanalyzer.UserHandling.repo.UserRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${app.jwtSecret}")
    private String secretKey;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Value("${jwt.access-expiration:900000}") // 15 minutes
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days
    private long refreshTokenExpiration;

    private final RefreshTokenRepo refreshTokenRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserProfileRepo userProfileRepo;
    private final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public JwtService(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public String generateAccessToken(String username) {
        Long userId = userRepo.findIdByUsername(username).orElse(null);
        return createToken(username, userId, accessTokenExpiration, "ACCESS");
    }

    public String generateRefreshToken(String username) {
        Long userId = userRepo.findIdByUsername(username).orElse(null);
        String token = createToken(username, userId, refreshTokenExpiration, "REFRESH");

        // Store refresh token in database
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUsername(username);
        refreshToken.setUserId(userId); // Also store userId in refresh token record
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + refreshTokenExpiration));
        refreshTokenRepo.save(refreshToken);

        return token;
    }

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

    private String createToken(String username, Long userId, Long expiration, String type) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("type", type);
        return Jwts.builder()
                .claims()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
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

    public Boolean validateToken(String token, UserDetails userDetails, String expectedType) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            final String username = claims.getSubject();
            final String tokenType = claims.get("type", String.class);
            final Date expiration = claims.getExpiration();
            return username.equals(userDetails.getUsername()) &&
                    expectedType.equals(tokenType) &&
                    !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean validateAccessToken(String token, UserDetails userDetails) {
        return validateToken(token, userDetails, "ACCESS");
    }

    // Validate refresh token
    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        if (!validateToken(token, userDetails, "REFRESH"))
            return false;

        // Check if refresh token exists in database and isn't revoked
        return refreshTokenRepo.findByTokenAndRevokedFalse(token).isPresent();
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
