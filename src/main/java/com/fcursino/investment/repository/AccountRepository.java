package com.fcursino.investment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fcursino.investment.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {    
}
