package com.gillianbc.pensionstracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Provider {
    @Id
    @GeneratedValue
    Long id;
    @Column(nullable=false) String name;
    String notes;
}
