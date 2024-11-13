package laxy.server.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import laxy.server.exception.ApiException;
import laxy.server.exception.ErrorType;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 안전한 랜덤 키 생성
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // 토큰 유효 시간 (30분)
    private static final long EXPIRATION_TIME = 30 * 60 * 1000;

    // 리프래시 토큰 유효 시간 (7일)
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    // 엑세스 토큰 생성
    public static String generateAccessToken(Long memberId) {
        return generateToken(memberId, EXPIRATION_TIME);
    }

    // 리프래시 토큰 생성
    public static String generateRefreshToken(Long memberId) {
        return generateToken(memberId, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private static String generateToken(Long memberId, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(memberId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    // 엑세스 토큰 추출
    public static Long extractAccessToken(String accessToken) {
        return extractMemberId(accessToken, new ApiException(ErrorType.ACCESS_TOKEN_EXPIRED));
    }

    // 리프레시 토큰 추출
    public static Long extractRefreshToken(String refreshToken) {
        return extractMemberId(refreshToken, new ApiException(ErrorType.REFRESH_TOKEN_EXPIRED));
    }

    private static Long extractMemberId(String token, ApiException error) {
        if (token == null || token.isEmpty()) {
            throw new ApiException(ErrorType.JWT_TOKEN_NOT_FOUND);
        }

        if (token.startsWith("Bearer ")) { // Bearer 접두사 제거
            token = token.substring(7);
        }

        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return Long.parseLong(subject);
        } catch (MalformedJwtException | NumberFormatException e) {
            throw new ApiException(ErrorType.INVALID_JWT_TOKEN);
        } catch (ExpiredJwtException e) {
            throw error;
        } catch (Exception e) {
            throw new ApiException(ErrorType.INVALID_JWT_TOKEN);
        }
    }
}
