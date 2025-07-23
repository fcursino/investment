package com.fcursino.investment.controller;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fcursino.investment.controller.dto.CreateAccountDTO;
import com.fcursino.investment.controller.dto.CreateUserDTO;
import com.fcursino.investment.controller.dto.UpdateUserDTO;
import com.fcursino.investment.entity.User;
import com.fcursino.investment.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<CreateUserDTO> createUserCaptor;

    @Captor
    private ArgumentCaptor<UpdateUserDTO> updateUserCaptor;

    @Captor
    private ArgumentCaptor<CreateAccountDTO> createAccountCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Test
    void testCreateUser() throws Exception {
        var user = new User(
                UUID.randomUUID(),
                "testuser",
                "password123",
                "testuser@email.com",
                Instant.now(),
                null,
                new ArrayList<>());
        doReturn(user).when(userService).createUser(createUserCaptor.capture());

        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"testuser@email.com\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetUsers() throws Exception {
        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserById() throws Exception {
        var user = new User(
                UUID.randomUUID(),
                "testuser",
                "password123",
                "testuser@email.com",
                Instant.now(),
                null,
                new ArrayList<>());
        doReturn(Optional.of(user)).when(userService).getUserById(stringCaptor.capture());

        mockMvc.perform(get("/v1/users/user-id"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        doReturn(Optional.empty()).when(userService).getUserById(stringCaptor.capture());

        mockMvc.perform(get("/v1/users/user-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser() throws Exception {
        doNothing().when(userService).updateUser(stringCaptor.capture(), updateUserCaptor.capture());

        mockMvc.perform(put("/v1/users/user-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isNoContent());
    }

     @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(stringCaptor.capture());

        mockMvc.perform(delete("/v1/users/user-id"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreateAccount() throws Exception {
        doNothing().when(userService).createAccount(stringCaptor.capture(), createAccountCaptor.capture());

        mockMvc.perform(post("/v1/users/user-id/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"desc\",\"street\":\"rua 5\",\"number\": 123}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAccounts() throws Exception {
        mockMvc.perform(get("/v1/users/user-id/accounts"))
                .andExpect(status().isOk());
    }


}
