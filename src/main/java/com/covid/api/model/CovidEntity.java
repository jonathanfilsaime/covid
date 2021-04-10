package com.covid.api.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "COVID_ENTITY")
public class CovidEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String county;
    private String state;
    private String country;
    private double newCases;
    private double deaths;
    private double recovered;
    private double active;
    private String date;
}

