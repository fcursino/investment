package com.fcursino.investment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fcursino.investment.controller.dto.AccountResponseDTO;
import com.fcursino.investment.controller.dto.CreateAccountDTO;
import com.fcursino.investment.controller.dto.CreateUserDTO;
import com.fcursino.investment.controller.dto.UpdateUserDTO;
import com.fcursino.investment.entity.Account;
import com.fcursino.investment.entity.BillingAddress;
import com.fcursino.investment.entity.User;
import com.fcursino.investment.repository.AccountRepository;
import com.fcursino.investment.repository.BillingAddressRepository;
import com.fcursino.investment.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BillingAddressRepository billingAddressRepository;

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

    public void createAccount(String userId, CreateAccountDTO createAccountDTO) {
        var user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var account = new Account(
            null,
            createAccountDTO.description(),
            user,
            null,
            new ArrayList<>()
        );
        var accountCreated = accountRepository.save(account);
        var billingAddress = new BillingAddress(
            accountCreated.getAccountId(),
            createAccountDTO.street(),
            createAccountDTO.number(),
            accountCreated
        );

        billingAddressRepository.save(billingAddress);
    }

    public List<AccountResponseDTO> getAccounts(String userId) {
        var user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return user.getAccounts()
            .stream()
            .map(ac -> new AccountResponseDTO(
                ac.getAccountId().toString(),
                ac.getDescription()
            )
            ).toList();
    }

    
}
