package com.fcursino.investment.controller;

import com.fcursino.investment.controller.dto.AssociateAccountStockDTO;
import com.fcursino.investment.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void testAssociateStock() throws Exception {
        doNothing().when(accountService).associateStock("account-id", new AssociateAccountStockDTO("STCK", 10));

        mockMvc.perform(post("/v1/accounts/account-id/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stockId\":\"STCK\",\"quantity\":10}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStocks() throws Exception {
        mockMvc.perform(get("/v1/accounts/account-id/stocks"))
                .andExpect(status().isOk());
    }
}