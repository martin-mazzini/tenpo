package com.example.tenpo.security;


import com.google.common.base.Strings;
import io.jsonwebtoken.*;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@CommonsLog
@Component
public class JWTTokenUtils implements Serializable {


	@Value("${jwt.header.string}")
	public String HEADER_STRING;

    @Value("${jwt.token.validity}")
    public long TOKEN_VALIDITY;

    @Value("${jwt.signing.key}")
    public String SIGNING_KEY;




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
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }



    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //genera el jwt token a partir de la autenticacion
    public String generateToken(Authentication authentication) {

        		return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY*1000))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();
    }

    //chequea que un token sea valido
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}


    UsernamePasswordAuthenticationToken getAuthenticationToken(String token, UserDetails userDetails) {

        final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);

        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        final Claims claims = claimsJws.getBody();

		final Collection<? extends GrantedAuthority> grantedAuthorities = new ArrayList<>();

        return new UsernamePasswordAuthenticationToken(userDetails, "", grantedAuthorities);
    }


	public  Optional<String> parseHeader(String header) {
		if (header != null) {
			return Optional.of(header);
		}else {
			log.debug("No se halló el bearer string. Se ignorará el header");
			return Optional.empty();
		}
	}

	public String parseUsername(String authToken) {
		String username = null;
		try {
			username = getUsernameFromToken(authToken);
		} catch (IllegalArgumentException e) {
			log.error("Ocurrió un error al obtener el nombre del token", e);
		} catch (ExpiredJwtException e) {
			log.debug("El token expiró", e);
		} catch (SignatureException e) {
			log.error("Falló la autenticación, password o usuario no válidos.");
		}
		return username;
	}



	public User getPrincipal() {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return principal;
	}

	//el username es el mail
	public String getUserEmail() {
		return getPrincipal().getUsername();
	}

}
