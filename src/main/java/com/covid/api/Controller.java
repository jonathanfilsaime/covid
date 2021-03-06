package com.covid.api;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.covid.api.model.CountyData;
import com.covid.api.model.CovidEntity;
import com.covid.api.model.CovidStateEntity;
import com.covid.api.model.WorldData;
import com.covid.api.repo.CovidRepository;
import org.javatuples.Triplet;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("load/days/{days}")
    public void load(@PathVariable("days") int days) throws IOException {
        int count = days;
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

    @GetMapping("/data/county/{county}/state/{state}/today")
    public ResponseEntity<CovidEntity> getDataCountyToday(@PathVariable("county") String county,
                                                         @PathVariable("state") String state) {
        LocalDate currentDate = LocalDate.now().minusDays(1);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String date = dateFormat.format(currentDate);
        LocalDate previousDate = helper.computePreviousDate(date);
        String prvDate = previousDate.format(dateFormat);
        Optional<CovidEntity> currentEntity = repository.findByCountyStateAndDate(county.toLowerCase(), state.toLowerCase(), date.toLowerCase());
        Optional<CovidEntity> previousEntity = repository.findByCountyStateAndDate(county.toLowerCase(), state.toLowerCase(), prvDate.toLowerCase());
        return ResponseEntity.ok(helper.computeFinalEntity(currentEntity, previousEntity));
    }

    @GetMapping("/data/county/{county}/state/{state}/date/{date}")
    public ResponseEntity<CovidEntity> getDataCountyDate(@PathVariable("county") String county,
                                            @PathVariable("state") String state,
                                            @PathVariable("date") String date) {
        LocalDate previousDate = helper.computePreviousDate(date);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String prvDate = previousDate.format(dateFormat);
        Optional<CovidEntity> currentEntity = repository.findByCountyStateAndDate(county.toLowerCase(), state.toLowerCase(), date.toLowerCase());
        Optional<CovidEntity> previousEntity = repository.findByCountyStateAndDate(county.toLowerCase(), state.toLowerCase(), prvDate.toLowerCase());
        return ResponseEntity.ok(helper.computeFinalEntity(currentEntity, previousEntity));
    }

    @GetMapping("/data/state/{state}/today")
    public ResponseEntity<CovidStateEntity> getDataStateToday(@PathVariable("state") String state) {
        LocalDate currentDate = LocalDate.now().minusDays(1);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String curDate = dateFormat.format(currentDate);
        LocalDate previousDate = helper.computePreviousDate(curDate);
        String prvDate = previousDate.format(dateFormat);
        return ResponseEntity.ok(helper.computeFinalEntity(state, prvDate, curDate));
    }

    @GetMapping("/data/state/{state}/date/{date}")
    public ResponseEntity<CovidStateEntity> getDataStateDate(@PathVariable("state") String state,
                                 @PathVariable("date") String date) {
        LocalDate previousDate = helper.computePreviousDate(date);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String prvDate = previousDate.format(dateFormat);
        return ResponseEntity.ok(helper.computeFinalEntity(state, prvDate, date));
    }

    @GetMapping("/data/county/{county}/state/{state}/days/{days}")
    public ResponseEntity<List<CovidEntity>> getDataCounty(@PathVariable("county") String county,
                                           @PathVariable("state") String state,
                                           @PathVariable("days") int days) {
        int count = 1;
        List<CovidEntity> list = new ArrayList<>();
        while ( count <= days ) {
            LocalDate now = LocalDate.now().minusDays(count);
            LocalDate prv = LocalDate.now().minusDays(count + 1);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            String currentDate = now.format(dateFormat);
            String previousDate = prv.format(dateFormat);
            Optional<CovidEntity> currentEntity = repository.findByCountyStateAndDate(county.toLowerCase(), state.toLowerCase(), currentDate.toLowerCase());
            Optional<CovidEntity> previousEntity = repository.findByCountyStateAndDate(county.toLowerCase(), state.toLowerCase(), previousDate.toLowerCase());
            list.add(helper.computeFinalEntity(currentEntity, previousEntity));
            count++;
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/data/counties/state/{state}/today")
    public ResponseEntity<List<CountyData>> getDataCounties(@PathVariable("state") String state) {
        LocalDate now = LocalDate.now().minusDays(1);
        LocalDate prv = LocalDate.now().minusDays(2);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String currentDate = now.format(dateFormat);
        String previousDate = prv.format(dateFormat);
        List<CovidEntity> currentCountyData = repository.findListOfCountyData(state, currentDate);
        List<CovidEntity> previousCountyData = repository.findListOfCountyData(state, previousDate);
        return helper.computeFinalEntity(currentCountyData, previousCountyData);
    }

    @GetMapping("/data/state/{state}/days/{days}")
    public ResponseEntity<List<CovidStateEntity>> getDataState(@PathVariable("state") String state,
                                              @PathVariable("days") int days) {
        int count = 1;
        List<CovidStateEntity> list = new ArrayList<>();
        while ( count <= days ) {
            LocalDate now = LocalDate.now().minusDays(count);
            LocalDate prv = LocalDate.now().minusDays(count + 1);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            String currentDate = now.format(dateFormat);
            String previousDate = prv.format(dateFormat);
            list.add(helper.computeFinalEntity(state, previousDate, currentDate));
            count++;
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/data/state/{state}")
    public ResponseEntity<List<String>> getCountiesPerState(@PathVariable("state") String state) {
        LocalDate now = LocalDate.now().minusDays(1);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String date = now.format(dateFormat);
        return ResponseEntity.ok(repository.findListOfCountiesPerState(state, date));
    }

    @GetMapping("/data/world")
    public ResponseEntity<List<WorldData>> getWorldData() {
        LocalDate now = LocalDate.now().minusDays(1);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String date = now.format(dateFormat);
        List<WorldData> data = new ArrayList<>(repository.findWorldData(date));
        return ResponseEntity.ok(data);
    }
}
