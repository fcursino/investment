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
import com.fcursino.investment.controller.dto.CreateUserDTO;
import com.fcursino.investment.controller.dto.UpdateUserDTO;
import com.fcursino.investment.entity.User;
import com.fcursino.investment.repository.UserRepository;
import com.fcursino.investment.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

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
                        new ArrayList<>()
                        );
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
        }

    }
}
