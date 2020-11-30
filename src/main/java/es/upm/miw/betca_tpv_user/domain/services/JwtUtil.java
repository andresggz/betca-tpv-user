package es.upm.miw.betca_tpv_user.domain.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Optional;

public class JwtUtil {
    private static final String BEARER = "Bearer ";
    private static final String USER_CLAIM = "user";
    private static final String NAME_CLAIM = "name";
    private static final String ROLE_CLAIM = "role";
    private static final String ISSUER = "es-upm-miw";
    private static final int EXPIRES_IN_MILLISECOND = 3600000;
    private static final String SECRET = "secret-password-test";

    public static String extractToken(String bearer) {
        if (bearer != null && bearer.startsWith(BEARER) && 3 == bearer.split("\\.").length) {
            return bearer.substring(BEARER.length());
        } else {
            return "";
        }
    }

    public static String createToken(String user, String name, String role) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRES_IN_MILLISECOND))
                .withClaim(USER_CLAIM, user)
                .withClaim(NAME_CLAIM, name)
                .withClaim(ROLE_CLAIM, role)
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static String user(String authorization) {
        return JwtUtil.verify(authorization)
                .map(jwt -> jwt.getClaim(USER_CLAIM).asString())
                .orElse("");
    }

    public static String name(String authorization) {
        return JwtUtil.verify(authorization)
                .map(jwt -> jwt.getClaim(NAME_CLAIM).asString())
                .orElse("");
    }

    public static String role(String authorization) {
        return JwtUtil.verify(authorization)
                .map(jwt -> jwt.getClaim(ROLE_CLAIM).asString())
                .orElse("");
    }

    private static Optional< DecodedJWT > verify(String token) {
        try {
            return Optional.of(JWT.require(Algorithm.HMAC256(SECRET))
                    .withIssuer(ISSUER).build()
                    .verify(token));
        } catch (Exception exception) {
            return Optional.empty();
        }

    }

}
