package com.covid.api.model;

import lombok.Data;

@Data
public class CountyData {
    public String countyName;
    public String newCases;
    public String deaths;
}
