package com.covid.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class Controller {

    private final CovidService service;
    private final CovidRepository repository;

    public Controller(CovidService service, CovidRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping("load/today")
    public void today() throws IOException {
        LocalDate now = LocalDate.now().minusDays(1);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String date = now.format(dateFormat);
        log.info(date);
        write(service.getData(date), date);
        
    }

    @GetMapping("load/sevendays")
    public void sevendays() {
        int count = 7;
        while ( count > 0) {
            LocalDate now = LocalDate.now().minusDays(count);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            String date = now.format(dateFormat);
            log.info(date);
            write(service.getData(date), date);
            count--;
        }
        log.info("done");
    }

    @GetMapping("/data/county/{county}/state/{state}/date/{date}")
    public Optional<CovidEntity> getDataCountyDate(@PathVariable("county") String county,
                                  @PathVariable("state") String state,
                                  @PathVariable("date") String date) {
            return repository.findByCountyStateAndDate(county.toLowerCase(), state.toLowerCase(), date.toLowerCase());

    }

    @GetMapping("/data/state/{state}/date/{date}")
    public Optional<CovidEntity> getDataStateDate(@PathVariable("state") String state,
                                 @PathVariable("date") String date) {
            return repository.findByStateAndDate(state.toLowerCase(), date.toLowerCase());

    }

    @GetMapping("/data/sevendays/county/{county}/state/{state}")
    public Optional<CovidEntity> getDataCounty(@PathVariable("county") String county,
                                            @PathVariable("state") String state) {
        return repository.findByCountyAndState(county.toLowerCase(), state.toLowerCase());
    }

    @GetMapping("/data/sevendays/state/{state}")
    public Optional<CovidEntity> getDataState(@PathVariable("state") String state) {
        return repository.findByState(state.toLowerCase());

    }

    private void write(Mono<String> file, String date) {
        StringBuilder st = new StringBuilder();
        st.append("covidData");
        st.append(date);

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(st.toString()));
            out.write(file.block());
            out.close();
            log.info("File created successfully");
            load(date);
        } catch (IOException e) {
        }
    }

    private void load(String date) throws IOException {

        StringBuilder st = new StringBuilder();
        st.append("covidData");
        st.append(date);

        File file = new File(st.toString());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
               String[] token = line.split(",");

               log.info(token[1]);
               log.info(token[2]);
               log.info(token[3]);
               log.info(token[7]);
               log.info(token[8]);
               log.info(token[9]);
               log.info(token[10]);
               log.info(date);


               CovidEntity entity = new CovidEntity();
               entity.setCounty(token[1].toLowerCase());
               entity.setState(token[2].toLowerCase());
               entity.setCountry(token[3].toLowerCase());
               entity.setNewCases(token[7].isEmpty() ? 0 : Double.parseDouble(token[7]));
               entity.setDeaths(token[8].isEmpty() ? 0 : Double.parseDouble(token[8]));
               entity.setRecovered(token[9].isEmpty() ? 0 : Double.parseDouble(token[9]));
               entity.setActive(token[10].isEmpty() ? 0 : Double.parseDouble(token[10]));
               entity.setDate(date);

               repository.save(entity);
            }
        }

    }    
}
