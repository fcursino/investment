package com.fcursino.investiment.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fcursino.investiment.controller.CreateUserDTO;
import com.fcursino.investiment.controller.UpdateUserDTO;
import com.fcursino.investiment.entity.User;
import com.fcursino.investiment.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setUsername(createUserDTO.username());
        user.setPassword(createUserDTO.password());
        user.setEmail(createUserDTO.email());
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String userId) {
        var id = UUID.fromString(userId);
        var userExists = userRepository.existsById(id);
        if (userExists) {
            userRepository.deleteById(id);
        }
    }

    public void updateUser(String userId, UpdateUserDTO updateUserDTO) {
        var id = UUID.fromString(userId);
        var userEntity = userRepository.findById(id);
        if (userEntity.isPresent()) {
            var user = userEntity.get();
            if (updateUserDTO.username() != null) {
                user.setUsername(updateUserDTO.username());
            }
            if (updateUserDTO.password() != null) {
                user.setPassword(updateUserDTO.password());
            }
            userRepository.save(user);
        }
    }
}
