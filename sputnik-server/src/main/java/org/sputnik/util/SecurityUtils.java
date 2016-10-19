package org.sputnik.util;

import io.jsonwebtoken.*;
import org.sputnik.model.ForbiddenException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Random;

public abstract class SecurityUtils {
    private static final String SECRET = "3d187564ff3243e8a526c1159e932f12";
    private static final int PASSWORD_NUM = 1000000000;
    private final static Random random = new Random();

    public static String getRandomPassword() {
        long value = (long) (random.nextDouble() * PASSWORD_NUM);
        return String.format("%09d", value);
    }

    public static String createToken(String username, Date expiration) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static String parseToken(String token) {
        try {
            Jws<Claims> jwt = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return jwt.getBody().getSubject();
        } catch (JwtException e) {
            throw new ForbiddenException(e.getMessage());
        }
    }

    public static String getUser(HttpServletRequest request) {
        try {
            String auth = request.getHeader("Authorization");
            String[] parts = auth.split("\\s");
            if ("Bearer".equals(parts[0])) {
                return parseToken(parts[1]);
            }
        } catch (Exception e) {
            //nop
        }
        throw new ForbiddenException("Not authenticated.");
    }
}
