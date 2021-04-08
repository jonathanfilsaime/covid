package com.covid.api;

import com.covid.api.model.CovidEntity;
import com.covid.api.model.CovidStateEntity;
import com.covid.api.repo.CovidRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.*;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
public class HelperService {

    private final CovidRepository repository;
    private final CovidService service;

    public HelperService(CovidRepository repository, CovidService service) {
        this.repository = repository;
        this.service = service;
    }

    public CovidEntity computeFinalEntity(Optional<CovidEntity> current, Optional<CovidEntity> previous) {

        CovidEntity finalEntity = new CovidEntity();

        if(current.isPresent() && previous.isPresent()) {
            finalEntity.setId(current.get().getId());
            finalEntity.setCounty(current.get().getCounty());
            finalEntity.setState(current.get().getState());
            finalEntity.setCountry(current.get().getCountry());
            finalEntity.setNewCases(current.get().getNewCases() - previous.get().getNewCases());
            finalEntity.setDeaths(current.get().getDeaths() - previous.get().getDeaths());
            finalEntity.setRecovered(current.get().getRecovered() - previous.get().getRecovered());
            finalEntity.setActive(current.get().getActive() - previous.get().getActive());
            finalEntity.setDate(current.get().getDate());
        } else if (current.isPresent()) {
            finalEntity.setId(current.get().getId());
            finalEntity.setCounty(current.get().getCounty());
            finalEntity.setState(current.get().getState());
            finalEntity.setCountry(current.get().getCountry());
            finalEntity.setNewCases(0);
            finalEntity.setDeaths(0);
            finalEntity.setRecovered(0);
            finalEntity.setActive(0);
            finalEntity.setDate(current.get().getDate());
        } else if (previous.isPresent()) {
            throw new RuntimeException("data not present");
        } else {
            throw new RuntimeException("data not present");
        }

        return finalEntity;
    }

    public CovidStateEntity computeFinalEntity(String state, String pvrDate, String curDate) {

        Optional<Double> newCasesPrev = repository.findSumNewCasesByStateAndDate(state, pvrDate);
        Optional<Double> deathPrev = repository.findSumDeathsByStateAndDate(state, pvrDate);

        Optional<Double> newCasesCur = repository.findSumNewCasesByStateAndDate(state, curDate);
        Optional<Double> deathCur = repository.findSumDeathsByStateAndDate(state, curDate);

        CovidStateEntity finalEntity = new CovidStateEntity();

        finalEntity.setState(state);
        finalEntity.setDate(curDate);
        if(newCasesPrev.isPresent() && newCasesCur.isPresent() && deathPrev.isPresent() && deathCur.isPresent()) {
            finalEntity.setNewCases(newCasesCur.get() - newCasesPrev.get());
            finalEntity.setDeaths(deathCur.get() - deathPrev.get());
        } else {
            finalEntity.setNewCases(0);
            finalEntity.setDeaths(0);
        }

        return finalEntity;
    }

    public void write(Mono<String> file, String date) {
        StringBuilder st = new StringBuilder();
        st.append("covidData");
        st.append(date);

        try {
            String text = file.block();

            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(st.toString()));
                out.write(text);
                out.close();
                log.info("File created successfully");
            } catch (IOException e) {
                log.error("issue creating the file " + e.getLocalizedMessage());
            }
        } catch (RuntimeException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void load(String date) {

        StringBuilder st = new StringBuilder();
        st.append("covidData");
        st.append(date);

        File file = new File(st.toString());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] token = line.split(",");

                if (repository.checkIfExist(token[3].toLowerCase(), token[1].toLowerCase(), token[2].toLowerCase(), date) == 0) {
                    CovidEntity entity = new CovidEntity();
                    entity.setCounty(token[1].toLowerCase());
                    entity.setState(token[2].toLowerCase());
                    entity.setCountry(token[3].toLowerCase());
                    entity.setNewCases(token[7].isEmpty() ? 0 : Double.parseDouble(token[7]));
                    entity.setDeaths(token[8].isEmpty() ? 0 : Double.parseDouble(token[8]));
                    entity.setRecovered(token[9].isEmpty() ? 0 : Double.parseDouble(token[9]));
                    entity.setActive(token[10].isEmpty() ? 0 : Double.parseDouble(token[10]));
                    entity.setDate(date);
                    log.info("entity inserted: " + entity);
                    repository.save(entity);
                } else {
                    log.info("No need to load the data for this date.The data is already in the database, county {}, state {}, country {}, new cases {}", token[1], token[2], token[3], token[7] );
                }
            }
        } catch (IOException e) {
            log.error("unable to read or open file");
        }
    }

    public void writeAndLoad(String date) throws IOException {
        File test = new File("covidData"+date);
        if (!test.exists()) {
            log.info("file does not exists");
            write(service.getData(date), date);
                load(date);
        } else {
            log.info("file exists already");
            load(date);
        }
    }

    public LocalDate computePreviousDate(String date) {
        int year = Integer.parseInt(date.substring(6, 10));
        int month = Integer.parseInt(date.substring(0, 2));
        int day = Integer.parseInt(date.substring(3, 5));

        LocalDate current = LocalDate.of(year, month, day);
        return current.minusDays(1);
    }
}
