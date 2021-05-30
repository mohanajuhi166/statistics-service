package com.events.statsservice.service.impl;

import com.events.statsservice.exception.BaseException;

public interface StatisticsService {

    public boolean validateData(final String value) throws BaseException;
    public boolean addEvent(final String value) throws BaseException;
    public String getStatistics() throws BaseException;
}
