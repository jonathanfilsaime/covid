//package com.covid.api;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Slf4j
//@Configuration
//@EnableScheduling
//public class Scheduler {
//
//    private final HelperService helper;
//
//    public Scheduler(HelperService helper) {
//        this.helper = helper;
//    }
//
//    @Scheduled(cron = "0 */1 * * * ?")
//    public void scheduleFixedDelayTask() throws IOException {
//        LocalDateTime now = LocalDateTime.now().minusDays(1);
//        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
//        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String date = now.format(dateFormat);
//        String dateTime = now.format(dateTimeFormat);
//        log.info("cron job date time now: " + dateTime);
//        helper.writeAndLoad(date);
//    }
//}
