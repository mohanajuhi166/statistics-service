package com.events.statsservice.service.impl.impl;

import com.events.statsservice.exception.BaseException;
import com.events.statsservice.model.Event;
import com.events.statsservice.model.Statistics;
import com.events.statsservice.service.impl.StatisticsService;
import com.events.statsservice.service.impl.StatisticsFunc;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private Object lock = new Object();


    private static Statistics latest = Statistics.newBuilder().build();
    private ReentrantLock reentrantLock = new ReentrantLock();
    private static final Map<Long, Statistics> statisticsMap = new ConcurrentHashMap<>();
    private static final Map<Long, Statistics> outOfRangeStatisticsMap = new ConcurrentHashMap<>();

    @Override
    public String getStatistics() throws BaseException {
        if (latest == null)
            throw new BaseException("No statistics captured yet", HttpStatus.NO_CONTENT);
        //return statistics;
        return getStatisticsValues();
    }

    @Override
    public boolean validateData(final String value) throws BaseException {
        //split strings
        String[] splitValues = value.split(",");
        if (splitValues.length != 3)
            throw new BaseException("Invalid Input", HttpStatus.BAD_REQUEST);

        else {

            if (!isTimeStampValid(splitValues[0])
                    || !validateXValue(splitValues[1])
                    || (!validateYValue(splitValues[2])
                    || !isStringInteger(splitValues[2]))) {

                return false;
            }


        }

        return true;
    }

    @Override
    public boolean addEvent(String value) throws BaseException {


        try {
            //split strings
            boolean isValidData = validateData(value);

            if (!isValidData)
                return false;

            String[] splitValues = value.split(",");
            long epochTime = Instant.now(Clock.systemUTC()).getEpochSecond();
            long timestampValInSec = Instant.ofEpochMilli(Long.parseLong(splitValues[0])).atOffset(ZoneOffset.UTC).toEpochSecond();
            long differenceInSec = epochTime - timestampValInSec;

            if (differenceInSec > 60) {
                return false;
            }

            Event eventVal =
                    new Event(timestampValInSec,
                            valueOfx(splitValues[1]),
                            Integer.parseInt(splitValues[2]));


            // timestamp is greater than 1 min,
            if (epochTime < timestampValInSec) {
                addOutOfRangeStatistics(eventVal.getX(), eventVal.getY(), timestampValInSec);
            } else {
                addStatistics(eventVal.getX(), eventVal.getY(), timestampValInSec);
            }
        } catch (BaseException e) {

            throw new BaseException(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            throw new BaseException("Data Validation Failed", HttpStatus.EXPECTATION_FAILED);
        }


        return true;
    }

    private boolean validateYValue(String value) {

        String max = String.valueOf(Integer.MAX_VALUE);
        String min = "1073741823";
        BigInteger b1 = new BigInteger(value);
        BigInteger b_max = new BigInteger(max);
        BigInteger b_min = new BigInteger(min);
        return b1.compareTo(b_max) <= 0 && b1.compareTo(b_min) >= 0;
    }

    private boolean isStringInteger(String YValue) {
        try {
            Integer.parseInt(YValue);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean validateXValue(String x) {

        try {
            long iPart;
            Double num = Double.parseDouble(x);
            iPart = (long) num.doubleValue();

            if (iPart != 0)
                return false;

        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private BigDecimal valueOfx(String x) {

        Double num = Double.parseDouble(x);
        BigDecimal modifiedXValue = BigDecimal.valueOf(num);
        int lengthOfFractionalPart = getNumberOfFractionParts(num);

        if (lengthOfFractionalPart > 10) {

            modifiedXValue = BigDecimal.valueOf(num).setScale(10, BigDecimal.ROUND_HALF_UP);
        }
        return modifiedXValue;
    }


    private int getNumberOfFractionParts(Double value) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(100);
        String s = numberFormat.format(value);
        return s.length();
    }

    private boolean isTimeStampValid(String inputString) throws BaseException {

        try {
            long timeStamp = Long.parseLong(inputString.trim());
            return timeStamp <= System.currentTimeMillis();
        } catch (Exception e) {
            throw new BaseException("Format issue in timestamp", HttpStatus.BAD_REQUEST);
        }
    }

    public void addStatistics(BigDecimal x, long y, long timestamp) {
        reentrantLock.lock();
        try {
            StatisticsFunc.addStatistics(latest, x, y);
        } finally {
            reentrantLock.unlock();
        }

        Statistics statistics = statisticsMap.computeIfAbsent(timestamp, key -> Statistics.newBuilder().build());
        StatisticsFunc.addStatistics(statistics, x, y);
    }


    public void addOutOfRangeStatistics(BigDecimal x, long y, long timestamp) {
        Statistics statistics = outOfRangeStatisticsMap.computeIfAbsent(timestamp,
                key -> Statistics.newBuilder().build());
        StatisticsFunc.addStatistics(statistics, x, y);
    }


    public String getStatisticsValues() {
        reentrantLock.lock();
        try {
            return StatisticsFunc.cloneStatistics(latest);
        } finally {
            reentrantLock.unlock();
        }
    }


    /**
     * Scheduled to remove old events
     */
    @Scheduled(cron = "* * * * * ?")
    private void removeOldEvents() {
        long currentEpoch = Instant.now(Clock.systemUTC()).getEpochSecond();

        // add current time statistics If exist in out of range map
        Statistics notCalculatedYet = outOfRangeStatisticsMap.remove(currentEpoch);
        if (notCalculatedYet != null) {
            reentrantLock.lock();
            try {
                StatisticsFunc.addStatistics(latest, notCalculatedYet);
            } finally {
                reentrantLock.unlock();
            }
            statisticsMap.put(currentEpoch, notCalculatedYet);
        }

        long before60seconds = currentEpoch - 60;
        Statistics statisticsTobeRemoved = statisticsMap.remove(before60seconds);
        if (statisticsTobeRemoved == null) {
            return;
        }
        reentrantLock.lock();
        try {
            StatisticsFunc.remove(latest, statisticsTobeRemoved);
        } finally {
            reentrantLock.unlock();
        }
    }

}
