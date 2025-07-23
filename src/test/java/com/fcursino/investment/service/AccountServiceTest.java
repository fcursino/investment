package com.fcursino.investment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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

import com.fcursino.investment.client.BrapiClient;
import com.fcursino.investment.controller.dto.AssociateAccountStockDTO;
import com.fcursino.investment.client.dto.BrapiResponseDTO;
import com.fcursino.investment.client.dto.StockDTO;
import com.fcursino.investment.entity.Account;
import com.fcursino.investment.entity.AccountStock;
import com.fcursino.investment.entity.AccountStockId;
import com.fcursino.investment.entity.Stock;
import com.fcursino.investment.repository.AccountRepository;
import com.fcursino.investment.repository.AccountStockRepository;
import com.fcursino.investment.repository.StockRepository;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

  @Mock
  private StockRepository stockRepository;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private AccountStockRepository accountStockRepository;

  @Captor
  private ArgumentCaptor<UUID> uuidArgumentCaptor;

  @Captor
  private ArgumentCaptor<String> stringArgumentCaptor;

  @Captor
  ArgumentCaptor<AccountStock> accountStockCaptor;

  @InjectMocks
  private AccountService accountService;

  @Mock
  private BrapiClient brapiClient;

  @BeforeEach
  void setUp() throws Exception {
      var field = AccountService.class.getDeclaredField("token");
      field.setAccessible(true);
      field.set(accountService, "test-token");
  }
  
  @Nested
  class associateStock {

    @Test
    @DisplayName("should associate a stock with an account when account exists")
    void shouldAssociateAStockWithAnAccountWhenAccountExists() {
      //arrange
      var accountId = UUID.randomUUID();
      var input = new AssociateAccountStockDTO(
        "STCK",
        5
      );
      var account = new Account(
        accountId,
        "description",
        null,
        null,
        new ArrayList<>()
      );

      var stock = new Stock(
        "STCK",
        "description of stock"
      );

      doReturn(Optional.of(account)).when(accountRepository).findById(uuidArgumentCaptor.capture());
      doReturn(Optional.of(stock)).when(stockRepository).findById(stringArgumentCaptor.capture());
      //act
      accountService.associateStock(accountId.toString(), input);
      //assert
      verify(accountRepository, times(1)).findById(uuidArgumentCaptor.getValue());
      verify(stockRepository, times(1)).findById(stringArgumentCaptor.getValue());
      verify(accountStockRepository, times(1)).save(accountStockCaptor.capture());
      
    }

    @Test
    @DisplayName("should not associate a stock with an account when account not exists")
    void shouldNotAssociateAStockWithAnAccountWhenAccountNotExists() {
      //arrange
      var accountId = UUID.randomUUID();
      var input = new AssociateAccountStockDTO(
        "STCK",
        5
      );
      doThrow(new ResponseStatusException(HttpStatusCode.valueOf(404))).when(accountRepository).findById(accountId);
      // act & assert
      assertThrows(ResponseStatusException.class, () -> accountService.associateStock(accountId.toString(), input));
    }

    @Test
    @DisplayName("should not associate a stock with an account when account exists and stock not exists")
    void shouldNotAssociateAStockWithAnAccountWhenAccountExistsAndStockNotExists() {
      //arrange
      var accountId = UUID.randomUUID();
      var input = new AssociateAccountStockDTO(
        "STCK",
        5
      );
      var account = new Account(
        UUID.randomUUID(),
        "description",
        null,
        null,
        new ArrayList<>()
      );
      doReturn(Optional.of(account)).when(accountRepository).findById(uuidArgumentCaptor.capture());
      doThrow(new ResponseStatusException(HttpStatusCode.valueOf(404))).when(stockRepository).findById(input.stockId());
      // act & assert
      assertThrows(ResponseStatusException.class, () -> accountService.associateStock(accountId.toString(), input));
    }
  }

  @Nested
  class getStocks {

    @Test
    @DisplayName("should get all stocks associated with an account when account exists")
    void shouldGetAllStocksAssociatedWithAnAccountWhenAccountExists() {
      //arrange
      var accountId = UUID.randomUUID();
      var stock1 = new Stock("STCK1", "description of stock 1");
      var stock2 = new Stock("STCK2", "description of stock 2");
      var accountStock1 = new AccountStock(new AccountStockId(accountId, stock1.getStockId()), null, stock1, 10);
      var accountStock2 = new AccountStock(new AccountStockId(accountId, stock2.getStockId()), null, stock2, 20);
      var account = new Account(
        accountId,
        "description",
        null,
        null,
        List.of(accountStock1, accountStock2)
      );

      var brapiResponse1 = new BrapiResponseDTO(List.of(new StockDTO(100.0)));
      var brapiResponse2 = new BrapiResponseDTO(List.of(new StockDTO(200.0)));

      doReturn(Optional.of(account)).when(accountRepository).findById(uuidArgumentCaptor.capture());
      when(brapiClient.getQuote(eq("test-token"), eq(stock1.getStockId()))).thenReturn(brapiResponse1);
      when(brapiClient.getQuote(eq("test-token"), eq(stock2.getStockId()))).thenReturn(brapiResponse2);


      //act
      var response = accountService.getStocks(accountId.toString());


      //assert
      assertEquals(2, response.size());
      assertEquals("STCK1", response.get(0).stockId());
      assertEquals(10, response.get(0).quantity());
      assertEquals(1000.0, response.get(0).total());
      
      assertEquals("STCK2", response.get(1).stockId());
      assertEquals(20, response.get(1).quantity());
      assertEquals(4000.0, response.get(1).total());

      verify(accountRepository, times(1)).findById(uuidArgumentCaptor.getValue());
      
    }

    @Test
    @DisplayName("should not get all stocks associated with an account when account not exists")
    void shouldNotGetAllStocksAssociatedWithAnAccountWhenAccountNotExists() {
      //arrange
      var accountId = UUID.randomUUID();
      doThrow(new ResponseStatusException(HttpStatusCode.valueOf(404))).when(accountRepository).findById(accountId);
      // act & assert
      assertThrows(ResponseStatusException.class, () -> accountService.getStocks(accountId.toString()));
    }
  }
}
