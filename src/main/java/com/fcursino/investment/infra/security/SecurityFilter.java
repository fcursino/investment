package com.fcursino.investment.infra.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fcursino.investment.entity.User;
import com.fcursino.investment.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
  @Autowired
  TokenService tokenService;
  @Autowired
  UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    //coleta e validação do token recebido
    var token = this.recoverToken(request);
    var login = tokenService.validateToken(token);

    if(login != null) {
      User user = userRepository.findByEmail(login).orElseThrow(() -> new RuntimeException("User not found."));
      //criação de roles para verificação
      var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
      //criação de objeto de autenticação
      var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
      //contexto que recebe validações já feitas
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");
    if(authHeader == null) return null;
    return authHeader.replace("Bearer ", "");
  }
}
