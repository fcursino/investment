package com.fcursino.investment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fcursino.investment.controller.dto.CreateStockDTO;

import com.fcursino.investment.entity.Stock;
import com.fcursino.investment.entity.User;
import com.fcursino.investment.repository.StockRepository;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {
  
  @Mock
  private StockRepository stockRepository;

  @InjectMocks
  private StockService stockService;

  @Captor
  private ArgumentCaptor<Stock> stockArgumentCaptor;

  @Nested
  class createStock {

    @Test
    @DisplayName("should create a new stock with success")
    void shouldCreateAStock() {
      //arrange
      var stock = new Stock(
        "STCK",
        "description of stock"
      );
      doReturn(stock).when(stockRepository).save(stockArgumentCaptor.capture());
      var input = new CreateStockDTO(
        "STCK",
        "description of stock"
      );
      //act
      var output = stockService.createStock(input);

      //assert
      assertEquals(input.stockId(), output.getStockId());
      assertEquals(input.description(), output.getDescription());
    }
  }
}
