package com.covid.api;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

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
            .bodyToMono(String.class);
    }
}