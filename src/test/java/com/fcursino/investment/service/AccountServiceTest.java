package com.fcursino.investment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import com.fcursino.investment.client.BrapiClient;
import com.fcursino.investment.controller.dto.AssociateAccountStockDTO;
import com.fcursino.investment.entity.Account;
import com.fcursino.investment.entity.AccountStock;
import com.fcursino.investment.entity.AccountStockId;
import com.fcursino.investment.entity.Stock;
import com.fcursino.investment.repository.AccountRepository;
import com.fcursino.investment.repository.AccountStockRepository;
import com.fcursino.investment.repository.StockRepository;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "TOKEN=test-token")
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

      accountService.associateStock(accountId.toString(), input);

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
      var account = new Account(
        UUID.randomUUID(),
        "description",
        null,
        null,
        new ArrayList<>()
      );

      var stock = new Stock(
        "STCK",
        "description of stock"
      );

      var id = new AccountStockId(
        account.getAccountId(),
        "STCK"
      );

      var accountStock = new AccountStock(
        id,
        account,
        stock,
        10
      );
      account.getAccountStocks().add(accountStock);
      var stockList = List.of(accountStock);
      doReturn(Optional.of(account)).when(accountRepository).findById(uuidArgumentCaptor.capture());
      doReturn().when(brapiClient).getQuote(, null)(uuidArgumentCaptor.capture());
      // act & assert
      var output = accountService.getStocks(account.getAccountId().toString());

      verify(accountRepository, times(1)).findById(uuidArgumentCaptor.getValue());
      assertEquals(account.getAccountId(), uuidArgumentCaptor.getValue());
      assertEquals(stockList.size(), account.getAccountStocks().size());
      
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
