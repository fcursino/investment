package com.fcursino.investiment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fcursino.investiment.entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {    
}
