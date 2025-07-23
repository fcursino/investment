package com.fcursino.investment.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fcursino.investment.controller.dto.CreateStockDTO;
import com.fcursino.investment.service.StockService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
public class StockControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Test
    void testCreateStock() throws Exception {
        doNothing().when(stockService).createStock(new CreateStockDTO("STCK", "description of stock"));

        mockMvc.perform(post("/v1/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stockId\":\"STCK\",\"description\":\"description of stock\"}"))
                .andExpect(status().isOk());
    }
}
