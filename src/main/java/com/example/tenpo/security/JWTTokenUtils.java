package com.example.tenpo.security;


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
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@CommonsLog
@Component
public class JWTTokenUtils implements Serializable {

	public static final String HEADER_STRING = "Authorization";
	protected static final Long TOKEN_VALIDITY = 18000L;
	protected static final String TOKEN_PREFIX = "Bearer ";
	protected static final Integer STARTING_TOKEN_INDEX = 7;

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

	public Optional<String> getToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(HEADER_STRING))
				.filter(token -> StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX))
				.map(bearerToken -> bearerToken.substring(7, bearerToken.length()));
	}


    //genera el jwt token a partir de la autenticacion
    public String generateToken(Authentication authentication) {
        		return  TOKEN_PREFIX + Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY*1000))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();
    }

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}


    public UsernamePasswordAuthenticationToken getAuthenticationToken(String token, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, "", new ArrayList<>());
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


	public String getUserEmail() {
		return getPrincipal().getUsername();
	}



}
