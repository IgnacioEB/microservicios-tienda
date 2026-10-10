package usuario.service.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarToken(String email, String rol){
        Date now= new Date();
        Date expires= new Date(now.getTime()+expiration);

        return Jwts.builder()
                .subject(email)
                .claim("rol",rol)
                .issuedAt(now)
                .expiration(expires)
                .signWith(getKey())
                .compact();

    }
    public String extraerEmail(String token){
        return parsearClaims(token).getSubject();
    }
    public Boolean esTokenValido(String token){
        try{
            parsearClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private Claims parsearClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



}

