package com.fcursino.investment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fcursino.investment.entity.AccountStock;
import com.fcursino.investment.entity.AccountStockId;

@Repository
public interface AccountStockRepository extends JpaRepository<AccountStock, AccountStockId> {    
}
