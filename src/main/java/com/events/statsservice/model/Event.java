package com.events.statsservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class Event {
    long timestamp;
    BigDecimal x;
    long y;
}
