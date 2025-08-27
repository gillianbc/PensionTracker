package com.gillianbc.pensionstracker.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue
    Long id;
    @ManyToOne(fetch = FetchType.LAZY) Pot pot;
    @Column(nullable=false)
    LocalDate date;
    @Column(nullable=false) String type; // contribution|rebate|employer_match|transfer_in|transfer_out|withdrawal|fee|adjustment
    @Column(nullable=false) Double amount; // inflow +, fees/withdrawals -
    String note;
}
