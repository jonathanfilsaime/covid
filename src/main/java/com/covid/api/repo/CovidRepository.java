package com.covid.api.repo;

import java.util.Optional;

import com.covid.api.model.CovidEntity;

import com.covid.api.model.CovidStateEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CovidRepository extends CrudRepository<CovidEntity, Long> {

    @Query("SELECT t FROM CovidEntity t where t.county = ?1 AND t.state= ?2 AND t.date = ?3")
    public Optional<CovidEntity> findByCountyStateAndDate(String county, String state, String date);

    @Query("SELECT t FROM CovidEntity t where t.country = ?1 AND t.county = ?2 AND t.state= ?3 AND t.date = ?4")
    public Optional<CovidEntity> findByCountryCountyStateAndDate(String country, String county, String state, String date);

    @Query("SELECT t.state as state, SUM(t.newCases) as newCases, SUM(t.deaths) as deaths, SUM(t.recovered) as recovered, SUM(t.active) as active FROM CovidEntity t where t.state = ?1 AND t.date = ?2")
    public Optional<CovidEntity> findByStateAndDate(String state, String date);

    @Query("SELECT SUM(t.newCases) as newCases FROM CovidEntity t where t.state = ?1 AND t.date = ?2")
    public Optional<Double> findSumNewCasesByStateAndDate(String state, String date);

    @Query("SELECT SUM(t.deaths) as deaths FROM CovidEntity t where t.state = ?1 AND t.date = ?2")
    public Optional<Double> findSumDeathsByStateAndDate(String state, String date);

    @Query("SELECT COUNT(*) FROM CovidEntity t WHERE t.country= ?1 AND t.county = ?2 AND t.state = ?3 AND t.date = ?4")
    public int checkIfExist(String country, String county, String state, String date);

}
