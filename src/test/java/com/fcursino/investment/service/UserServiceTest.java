package com.fcursino.investment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import com.fcursino.investment.controller.dto.CreateUserDTO;
import com.fcursino.investment.controller.dto.CreateAccountDTO;
import com.fcursino.investment.controller.dto.UpdateUserDTO;
import com.fcursino.investment.entity.Account;
import com.fcursino.investment.entity.BillingAddress;
import com.fcursino.investment.entity.User;
import com.fcursino.investment.repository.AccountRepository;
import com.fcursino.investment.repository.BillingAddressRepository;
import com.fcursino.investment.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BillingAddressRepository billingAddressRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    @Captor
    private ArgumentCaptor<BillingAddress> billingAddressArgumentCaptor;

    @Nested
    class createUser {

        @Test
        @DisplayName("should create a user with success")
        void shouldCreateAUserWithSuccess() {
            // arrange
            var user = new User(
                    UUID.randomUUID(),
                    "testuser",
                    "password123",
                    "testuser@email.com",
                    Instant.now(),
                    null,
                    new ArrayList<>());
            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());
            var input = new CreateUserDTO(
                    "testuser",
                    "password123",
                    "testuser@email.com");
            // act
            var output = userService.createUser(input);

            // assert
            assertNotNull(output);
            var userCaptured = userArgumentCaptor.getValue();
            assertEquals(input.username(), userCaptured.getUsername());
            assertEquals(input.password(), userCaptured.getPassword());
            assertEquals(input.email(), userCaptured.getEmail());
        }

        @Test
        @DisplayName("should throw an exception when user creation fails")
        void shouldThrowExceptionWhenUserCreationFails() {
            // arrange
            doThrow(new RuntimeException()).when(userRepository).save(any());
            var input = new CreateUserDTO(
                    "testuser",
                    "password123",
                    "testuser@email.com");
            // act & assert
            assertThrows(RuntimeException.class, () -> userService.createUser(input));
        }
    }

    @Nested
    class getUserById {

        @Test
        @DisplayName("should get user by id with success when user exists")
        void shouldGetUserByIdWithSuccess() {
            // arrange
            var user = new User(
                    UUID.randomUUID(),
                    "testuser",
                    "password123",
                    "testuser@email.com",
                    Instant.now(),
                    null,
                    new ArrayList<>());
            doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());
            // act
            var output = userService.getUserById(user.getUserId().toString());

            // assert
            assertTrue(output.isPresent());
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("should get user by id with success when user not exists")
        void shouldGetUserByIdEmpty() {
            // arrange
            var userId = UUID.randomUUID();
            doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentCaptor.capture());
            // act
            var output = userService.getUserById(userId.toString());

            // assert
            assertTrue(output.isEmpty());
            assertEquals(userId, uuidArgumentCaptor.getValue());
        }

        @Nested
        class getAllUsers {
            @Test
            @DisplayName("should get all users with success")
            void shouldGetAllUsersWithSuccess() {
                // arrange
                var user = new User(
                        UUID.randomUUID(),
                        "testuser",
                        "password123",
                        "testuser@email.com",
                        Instant.now(),
                        null,
                        new ArrayList<>());
                var userList = List.of(user);
                doReturn(userList).when(userRepository).findAll();
                // act
                var output = userService.getAllUsers();

                // assert
                assertNotNull(output);
                assertEquals(userList.size(), output.size());
            }
        }

        @Nested
        class deleteUser {
            @Test
            @DisplayName("should delete user with success when user exists")
            void shouldDeleteUserWithSuccess() {
                // arrange
                doReturn(true).when(userRepository).existsById(uuidArgumentCaptor.capture());
                doNothing().when(userRepository).deleteById(uuidArgumentCaptor.capture());
                var userId = UUID.randomUUID();
                // act
                userService.deleteUser(userId.toString());

                // assert
                // como o captor foi usado duas vezes, capturamos a lista e verificamos a ordem
                // de captura
                var idList = uuidArgumentCaptor.getAllValues();
                assertEquals(userId, idList.get(0));
                assertEquals(userId, idList.get(1));

                verify(userRepository, times(1)).existsById(idList.get(0));
                verify(userRepository, times(1)).deleteById(idList.get(1));
            }

            @Test
            @DisplayName("should not delete user when user does not exist")
            void shouldNotDeleteUserWhenNotExists() {
                // arrange
                var userId = UUID.randomUUID();
                doReturn(false).when(userRepository).existsById(uuidArgumentCaptor.capture());
                // act
                userService.deleteUser(userId.toString());

                // assert
                var idCaptured = uuidArgumentCaptor.getValue();
                assertEquals(userId, idCaptured);
                verify(userRepository, times(1)).existsById(idCaptured);
                verify(userRepository, never()).deleteById(any());
            }
        }

        @Nested
        class updateUser {
            @Test
            @DisplayName("should update user with success when user exists and username and password are provided")
            void shouldUpdateUserWithSuccessWhenUsernameAndPasswordAreProvided() {
                // arrange
                var user = new User(
                        UUID.randomUUID(),
                        "testuser",
                        "password123",
                        "testuser@email.com",
                        Instant.now(),
                        null,
                        new ArrayList<>());
                doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());
                doReturn(user).when(userRepository).save(userArgumentCaptor.capture());

                var input = new UpdateUserDTO(
                        "testuserupdated",
                        "password1234");
                // act
                userService.updateUser(user.getUserId().toString(), input);

                // assert
                assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
                var userCaptured = userArgumentCaptor.getValue();
                assertEquals(input.username(), userCaptured.getUsername());
                assertEquals(input.password(), userCaptured.getPassword());

                verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
                verify(userRepository, times(1)).save(userCaptured);
            }

            @Test
            @DisplayName("should not update user when user does not exist")
            void shouldNotUpdateUserWhenNotExists() {
                // arrange
                var userId = UUID.randomUUID();
                doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentCaptor.capture());
                var input = new UpdateUserDTO(
                        "testuserupdated",
                        "password1234");
                // act
                userService.updateUser(userId.toString(), input);

                // assert
                assertEquals(userId, uuidArgumentCaptor.getValue());
                verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
                verify(userRepository, never()).save(any());
            }

            @Test
            void updateUserUpdatesUsernameWhenUsernameProvided() {
                var userId = UUID.randomUUID().toString();
                var user = new User();
                user.setUsername("old");
                user.setPassword("oldpass");
                var updateDto = new UpdateUserDTO("new", null);

                when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));

                userService.updateUser(userId, updateDto);

                assertEquals("new", user.getUsername());
                assertEquals("oldpass", user.getPassword());
                verify(userRepository).save(user);
            }

            @Test
            void updateUserUpdatesPasswordWhenPasswordProvided() {
                var userId = UUID.randomUUID().toString();
                var user = new User();
                user.setUsername("old");
                user.setPassword("oldpass");
                var updateDto = new UpdateUserDTO(null, "newpass");

                when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));

                userService.updateUser(userId, updateDto);

                assertEquals("old", user.getUsername());
                assertEquals("newpass", user.getPassword());
                verify(userRepository).save(user);
            }

        }

        @Nested
        class createAccount {

            @Test
            @DisplayName("should create an account and its billing address when user exists")
            void shouldCreateAnAccountWhenUserExists() {
                // arrange
                var user = new User(
                        UUID.randomUUID(),
                        "testuser",
                        "password123",
                        "testuser@email.com",
                        Instant.now(),
                        null,
                        new ArrayList<>());

                var input = new CreateAccountDTO(
                        "description",
                        "Rua 5",
                        77);

                var account = new Account(
                        UUID.randomUUID(),
                        input.description(),
                        user,
                        null,
                        new ArrayList<>());

                var billingAddress = new BillingAddress(
                        account.getAccountId(),
                        input.street(),
                        input.number(),
                        account);
                doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());
                doReturn(account).when(accountRepository).save(accountArgumentCaptor.capture());
                doReturn(billingAddress).when(billingAddressRepository).save(billingAddressArgumentCaptor.capture());

                account.setBillingAddress(billingAddress);
                user.setAccounts(List.of(account));
                // act
                userService.createAccount(user.getUserId().toString(), input);

                // assert
                verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
                verify(accountRepository, times(1)).save(accountArgumentCaptor.getValue());
                verify(billingAddressRepository, times(1)).save(billingAddressArgumentCaptor.getValue());
            }

            @Test
            @DisplayName("should not create an account or a billing address when user not exists")
            void shouldNotCreateAnAccountWhenUserNotExists() {
                // arrange
                var userId = UUID.randomUUID();
                var input = new CreateAccountDTO(
                        "description",
                        "Rua 5",
                        77);
                doThrow(new ResponseStatusException(HttpStatusCode.valueOf(404))).when(userRepository).findById(userId);
                // act & assert
                assertThrows(ResponseStatusException.class, () -> userService.createAccount(userId.toString(), input));

                verify(userRepository, times(1)).findById(userId);
                verify(accountRepository, never()).save(any());
                verify(billingAddressRepository, never()).save(any());
            }
        }

        @Nested
        class getAccounts {

            @Test
            @DisplayName("should return user accounts when user exists")
            void shouldReturnUserAccounts() {
                // arrange
                var user = new User(
                        UUID.randomUUID(),
                        "testuser",
                        "password123",
                        "testuser@email.com",
                        Instant.now(),
                        null,
                        new ArrayList<>());
                doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());

                var account = new Account(
                        UUID.randomUUID(),
                        "description",
                        user,
                        null,
                        new ArrayList<>());

                var accountList = List.of(account);
                user.setAccounts(accountList);

                // act
                var output = userService.getAccounts(user.getUserId().toString());

                // assert
                assertNotNull(output);
                assertEquals(accountList.size(), output.size());
            }
        }

    }
}
