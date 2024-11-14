package com.dev.utils;

import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.models.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthUtil {

    public String generateToken(
            User user,
            int expireDay,
            String secretKey,
            @Nullable Date expireTime
    ) {
        Date expirationTimeVar = expireTime == null ? new Date(
                Instant.now().plus(expireDay, ChronoUnit.DAYS).toEpochMilli()
        ) : expireTime;
        JWSHeader jwtHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new  JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("dev.com")
                .issueTime(new Date())
                .expirationTime(expirationTimeVar)
                .claim("scope",user.getRole().toString())
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwtHeader, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey));
            return jwsObject.serialize();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public SignedJWT verifyToken(String token,String secretKey) throws ParseException, JOSEException {
        var verifier = new MACVerifier(secretKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        var verify = signedJWT.verify(verifier);

        var expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if(!verify) {
            throw new AppException(ErrorEnum.UNAUTHENTICATED);
        }
        if(!expireTime.after(new Date())) {
            throw new AppException(ErrorEnum.TOKEN_EXPIRE);
        }
        return signedJWT;

    }
}
