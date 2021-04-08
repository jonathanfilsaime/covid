package com.covid.api.model;

import lombok.Data;

@Data
public class CovidStateEntity {

    String state;
    double newCases;
    double deaths;
    String date;

}
