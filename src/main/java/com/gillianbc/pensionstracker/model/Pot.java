package com.gillianbc.pensionstracker.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Pot {
    @Id
    @GeneratedValue
    Long id;
    @ManyToOne(fetch = FetchType.LAZY) Provider provider;
    @Column(nullable=false) String name;
    String currency = "GBP";
    String status = "active"; // active|dormant|closed
    String notes;
}
