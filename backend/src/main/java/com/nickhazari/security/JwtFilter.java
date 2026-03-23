package com.nickhazari.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String token = extractTokenFromHeader(request);

    if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        String email = jwtService.extractEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (jwtService.isTokenValid(token, userDetails)) {
          UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities());

          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (RuntimeException ex) {
        // Invalid token - continue without authentication
      }
    }

    filterChain.doFilter(request, response);
  }

  private String extractTokenFromHeader(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }
}
