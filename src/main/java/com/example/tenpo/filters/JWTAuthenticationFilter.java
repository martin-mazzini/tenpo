package com.example.tenpo.filters;

import com.example.tenpo.security.JWTTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.example.tenpo.security.JWTTokenUtils.HEADER_STRING;

public class JWTAuthenticationFilter extends OncePerRequestFilter {


	private Long tokenValidty = 18000L;
	private String signingKey = "hfgry463hf746hf573ydh475fhy5739";
	private String Bearer = "Bearer";
	private String header = "Authorization";



	@Resource(name = "usersServiceImpl") //...hay más de un bean del tipo.
	private UserDetailsService userDetailsService;

	@Autowired
	private JWTTokenUtils jwtTokenUtil;

	@Override protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
			throws ServletException, IOException {


		Optional<String> tokenOpt = Optional.ofNullable(httpServletRequest.getHeader(HEADER_STRING));
		Optional<String> usernameOpt = tokenOpt.map(token -> jwtTokenUtil.parseUsername(token));

		if (usernameOpt.isPresent() && isNotAuthenticated()) {
			String username = usernameOpt.get();
			String authToken = tokenOpt.get();
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (jwtTokenUtil.validateToken(authToken, userDetails)) {
				UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthenticationToken(authToken, userDetails);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);

	}


	private boolean isNotAuthenticated() {
		return SecurityContextHolder.getContext().getAuthentication() == null;
	}



}
