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

	@Resource(name = "usersServiceImpl")
	private UserDetailsService userDetailsService;

	@Autowired
	private JWTTokenUtils jwtTokenUtil;

	@Override protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
			throws ServletException, IOException {


		Optional<String> tokenOpt = jwtTokenUtil.getToken(httpServletRequest);
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
