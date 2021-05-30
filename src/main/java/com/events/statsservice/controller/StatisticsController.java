package com.events.statsservice.controller;

import com.events.statsservice.exception.BaseException;
import com.events.statsservice.model.Response;
import com.events.statsservice.model.Statistics;
import com.events.statsservice.service.impl.StatisticsService;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService service;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity get() throws BaseException {
        // O(1)
        try {
            String statistics = service.getStatistics();
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity("Error in Getting Statistics", HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/event")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity addTransaction(@RequestBody String event) throws BaseException {
        // O(1)

        if(event==null)
            return new ResponseEntity("empty request", HttpStatus.BAD_REQUEST);
        try {
            boolean isAdded = service.addEvent(event);
            if (!isAdded)
                return new ResponseEntity("Event Timestamp is > 60s", HttpStatus.NO_CONTENT);
            else
                return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (BaseException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NO_CONTENT);
        }


    }

}
