package com.fcursino.investment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fcursino.investment.controller.dto.CreateStockDTO;
import com.fcursino.investment.entity.Stock;
import com.fcursino.investment.repository.StockRepository;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public void createStock(CreateStockDTO createStockDTO) {
        var stock = new Stock(
            createStockDTO.stockId(),
            createStockDTO.description()
        );

        stockRepository.save(stock);
    }

    
}
