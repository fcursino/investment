package com.fcursino.investment.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fcursino.investment.controller.dto.CreateUserDTO;
import com.fcursino.investment.controller.dto.LoginDTO;
import com.fcursino.investment.entity.User;
import com.fcursino.investment.infra.security.TokenService;
import com.fcursino.investment.repository.UserRepository;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private TokenService tokenService;

  @PostMapping("/login")
  public ResponseEntity login(@RequestBody LoginDTO body) {
    User user = this.userRepository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found."));
    if(passwordEncoder.matches(body.password(), user.getPassword())) {
      String token = this.tokenService.generateToken(user);
      return ResponseEntity.ok().body(token);
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/register")
  public ResponseEntity register(@RequestBody CreateUserDTO body) {
    Optional<User> user = this.userRepository.findByEmail(body.email());
    if(user.isEmpty()) {
      User newUser = new User();
      newUser.setPassword(passwordEncoder.encode(body.password()));
      newUser.setEmail(body.email());
      newUser.setUsername(body.username());
      this.userRepository.save(newUser);

      String token = this.tokenService.generateToken(newUser);
      return ResponseEntity.created(URI.create("/v1/auth/register/" + newUser.getUserId())).body(token);
    }
    return ResponseEntity.badRequest().build();
  }
}
