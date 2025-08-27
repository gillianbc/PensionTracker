package com.gillianbc.pensionstracker.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Snapshot {
    @Id
    @GeneratedValue
    Long id;
    @ManyToOne(fetch = FetchType.LAZY) Pot pot;
    @Column(nullable=false)
    LocalDate date;
    @Column(nullable=false) Double balance;
    String source; String note;
}
