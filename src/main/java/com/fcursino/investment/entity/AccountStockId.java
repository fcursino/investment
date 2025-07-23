package com.fcursino.investment.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountStockId {
    
    @Column(name = "account_id")
    private UUID accountId;
    @Column(name = "stock_id")
    private String stockId;
}
