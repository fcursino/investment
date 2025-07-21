package com.fcursino.investiment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fcursino.investiment.controller.dto.CreateStockDTO;
import com.fcursino.investiment.entity.Stock;
import com.fcursino.investiment.repository.StockRepository;

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
