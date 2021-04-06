package com.covid.api;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.covid.api.model.CovidEntity;
import com.covid.api.repo.CovidRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class Controller {

    private final CovidRepository repository;
    private final HelperService helper;

    public Controller(CovidRepository repository, HelperService helper) {
        this.repository = repository;
        this.helper = helper;
    }

    @GetMapping("load/today")
    public void today() throws IOException {
        LocalDate now = LocalDate.now().minusDays(1);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String date = now.format(dateFormat);
        log.info(date);
        helper.writeAndLoad(date);
        log.info("done");
    }

    @GetMapping("load/month")
    public void month() throws IOException {
        int count = 31;
        while ( count > 0) {
            LocalDate now = LocalDate.now().minusDays(count);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            String date = now.format(dateFormat);
            log.info(date);
            helper.writeAndLoad(date);
            count--;
        }
        log.info("done");
    }

    @GetMapping("/data/county/{county}/state/{state}/date/{date}")
    public CovidEntity getDataCountyDate(@PathVariable("county") String county,
                                                         @PathVariable("state") String state,
                                                         @PathVariable("date") String date) throws Exception {
        LocalDate previousDate = helper.computePreviousDate(date);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String prvDate = previousDate.format(dateFormat);
        Optional<CovidEntity> currentEntity = repository.findByCountyStateAndDate(county.toLowerCase(), state.toLowerCase(), date.toLowerCase());
        Optional<CovidEntity> previousEntity = repository.findByCountyStateAndDate(county.toLowerCase(), state.toLowerCase(), prvDate.toLowerCase());
        return helper.computeFinalEntity(currentEntity, previousEntity);
    }

    @GetMapping("/data/state/{state}/date/{date}")
    public CovidEntity getDataStateDate(@PathVariable("state") String state,
                                 @PathVariable("date") String date) throws Exception {
        LocalDate previousDate = helper.computePreviousDate(date);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String prvDate = previousDate.format(dateFormat);
        Optional<CovidEntity> currentEntity = repository.findByStateAndDate(state.toLowerCase(), date.toLowerCase());
        Optional<CovidEntity> previousEntity = repository.findByStateAndDate(state.toLowerCase(), prvDate.toString().toLowerCase());
        return helper.computeFinalEntity(currentEntity, previousEntity);
    }

    @GetMapping("/data/month/county/{county}/state/{state}")
    public Optional<CovidEntity> getDataCounty(@PathVariable("county") String county,
                                            @PathVariable("state") String state) {
        return repository.findByCountyAndState(county.toLowerCase(), state.toLowerCase());
    }

    @GetMapping("/data/month/state/{state}")
    public Optional<CovidEntity> getDataState(@PathVariable("state") String state) {
        return repository.findByState(state.toLowerCase());

    }
}
