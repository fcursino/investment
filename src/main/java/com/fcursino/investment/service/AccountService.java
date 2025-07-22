package com.fcursino.investment.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fcursino.investment.client.BrapiClient;
import com.fcursino.investment.controller.dto.AccountStockResponseDTO;
import com.fcursino.investment.controller.dto.AssociateAccountStockDTO;
import com.fcursino.investment.entity.AccountStock;
import com.fcursino.investment.entity.AccountStockId;
import com.fcursino.investment.repository.AccountRepository;
import com.fcursino.investment.repository.AccountStockRepository;
import com.fcursino.investment.repository.StockRepository;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AccountStockRepository accountStockRepository;

    @Autowired
    private BrapiClient brapiClient;

    @Value("#{environment.TOKEN}")
    private String token;

    public void associateStock(String accountId, AssociateAccountStockDTO dto) {
        var account = accountRepository.findById(UUID.fromString(accountId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        
        var stock = stockRepository.findById(dto.stockId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));

        var id = new AccountStockId(
            account.getAccountId(),
            stock.getStockId()
        );
        var entity = new AccountStock(
            id,
            account,
            stock,
            dto.quantity()
        );

        accountStockRepository.save(entity);
    }

    public List<AccountStockResponseDTO> getStocks(String accountId) {
        var account = accountRepository.findById(UUID.fromString(accountId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        return account.getAccountStocks()
            .stream()
            .map(as -> new AccountStockResponseDTO(
                as.getStock().getStockId(),
                as.getQuantity(),
                getTotal(as.getStock().getStockId(),
                as.getQuantity())
            )).toList();
    }

    private Double getTotal(String stockId, Integer quantity) {
        
        var response = brapiClient.getQuote(token, stockId);
        var price = response.results().getFirst().regularMarketPrice();

        return quantity * price;
    }

    
}
