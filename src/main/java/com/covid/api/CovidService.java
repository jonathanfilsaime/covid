package com.covid.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CovidService {

    private final WebClient.Builder webClient;

    public CovidService (WebClient.Builder webClient) {
        this.webClient = webClient;
   
    }

    public Mono<String> getData(String date) {
        return webClient
            .baseUrl("https://raw.githubusercontent.com")
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build())
            .build()
            .get()
            .uri("/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/{date}.csv", date)
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(e -> {throw new RuntimeException(e.getLocalizedMessage());})
            .doOnSuccess(r -> log.info("call successfully retrieve data" + r));
    }
}