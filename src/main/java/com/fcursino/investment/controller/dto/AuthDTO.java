package com.fcursino.investment.controller.dto;

import com.fcursino.investment.entity.User;

public record AuthDTO(User user, String token) {
  
}
