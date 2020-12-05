package com.covid.api.repo;

import java.util.Optional;

import com.covid.api.model.CovidEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CovidRepository extends CrudRepository<CovidEntity, Long> {

    @Query("SELECT t FROM CovidEntity t where t.county = ?1 AND t.state= ?2 AND t.date = ?3")
    public Optional<CovidEntity> findByCountyStateAndDate(String county, String state, String date);

    @Query("SELECT t.state as state, SUM(t.newCases) as newCases, SUM(t.deaths) as deaths, SUM(t.recovered) as recovered, SUM(t.active) as active FROM CovidEntity t where t.state = ?1 AND t.date = ?2")
    public Optional<CovidEntity> findByStateAndDate(String state, String date);

    @Query("SELECT t.state as state, SUM(t.newCases) as newCases, SUM(t.deaths) as deaths, SUM(t.recovered) as recovered, SUM(t.active) as active FROM CovidEntity t where t.state = ?1")
    public Optional<CovidEntity> findByState(String state);

    @Query("SELECT t.state as state, SUM(t.newCases) as newCases, SUM(t.deaths) as deaths, SUM(t.recovered) as recovered, SUM(t.active) as active FROM CovidEntity t where t.county = ?1 AND t.state = ?2")
    public Optional<CovidEntity> findByCountyAndState(String county, String state);
    
}
