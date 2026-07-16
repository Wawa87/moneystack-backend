package com.wawa87.moneystack.service.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {
    private final String SECRET;
    private final String ISSUER;
    private final long EXPIRATION_TIME = 3600_000; // 1 hour.

    private final Algorithm ALGORITHM;
    private final JWTVerifier verifier;

    public JwtUtil(String secret, String issuer) {
        this.SECRET = secret;
        this.ISSUER = issuer;
        this.ALGORITHM = Algorithm.HMAC256(SECRET);
        this.verifier = JWT.require(ALGORITHM)
                .withIssuer(ISSUER)
                .build();
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + this.EXPIRATION_TIME);

        return JWT.create()
                .withIssuer(this.ISSUER)
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(this.ALGORITHM);
    }

    public String validateAndGetSubject(String token) {
        try {
            DecodedJWT jwt = this.verifier.verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
