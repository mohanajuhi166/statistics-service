package com.events.statsservice.service.impl;

import com.events.statsservice.model.Statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StatisticsFunc {

    public static String cloneStatistics(Statistics statistics) {
        StringBuffer latestString = new StringBuffer();

        Statistics value = Statistics.newBuilder()
                .withAvgX(statistics.getAvgX())
                .withAvgY(statistics.getAvgY())
                .withTotal(statistics.getTotal())
                .withSumX(statistics.getSumX())
                .withSumY(statistics.getSumY()).build();


        //using string buffer as its synchronized and append is O(1) amortized
        latestString.append(value.getTotal());
        latestString.append(",");
        latestString.append(value.getSumX());
        latestString.append(",");
        latestString.append(value.getAvgX());
        latestString.append(",");
        latestString.append(value.getSumY());
        latestString.append(",");
        latestString.append(value.getAvgY());

        return latestString.toString();
    }

    public static void addStatistics(Statistics to, BigDecimal x, long y) {

        //add event
        to.setTotal(to.getTotal() + 1);
        double xVal = x.doubleValue();

        if (to.getTotal() == 1) {
            to.setSumX(xVal);
            to.setAvgX(xVal);
            to.setSumY(y);
            to.setAvgY(y);
        } else {
            BigDecimal sumX = BigDecimal.valueOf(to.getSumX())
                    .add(x).setScale(10, RoundingMode.HALF_UP);

            long sumY = to.getSumY() + y;

            //x
            to.setSumX(sumX.doubleValue());
            to.setAvgX(sumX.divide(BigDecimal.valueOf(to.getTotal()), RoundingMode.HALF_UP).doubleValue());

            //y
            to.setSumY(sumY);
            to.setAvgY(BigDecimal.valueOf(sumY).divide(BigDecimal.valueOf(to.getTotal()), RoundingMode.HALF_UP).doubleValue());
        }

    }


    public static void addStatistics(Statistics to, Statistics from) {
        to.setTotal(to.getTotal() + from.getTotal());
        if (to.getTotal() == from.getTotal()) {
            to.setSumX(from.getSumX());
            to.setAvgX(from.getAvgX());
            to.setSumY(from.getSumY());
            to.setAvgY(from.getAvgY());
        } else {
            BigDecimal sumX = BigDecimal.valueOf(to.getSumX()).add(BigDecimal.valueOf(from.getSumX()))
                    .setScale(10, RoundingMode.HALF_UP);
            long sumY = to.getSumY() + from.getSumY();
            to.setSumX(sumX.doubleValue());
            to.setAvgX(sumX.divide(BigDecimal.valueOf(to.getTotal()), RoundingMode.HALF_UP).doubleValue());
            to.setSumY(sumY);
            to.setAvgX(BigDecimal.valueOf(sumY).divide(BigDecimal.valueOf(to.getTotal()), RoundingMode.HALF_UP).doubleValue());
        }
    }


    public static void remove(Statistics from, Statistics to) {
        BigDecimal sumX = BigDecimal.valueOf(from.getSumX()).subtract(BigDecimal.valueOf(to.getSumX()))
                .setScale(10, RoundingMode.HALF_UP);
        from.setTotal(from.getTotal() - to.getTotal());
        from.setSumX(sumX.doubleValue());

        long sumY = from.getSumY() - to.getSumY();
        from.setSumY(sumY);

        if (from.getTotal() == 1) {
            from.setAvgX(from.getSumX());
            from.setAvgY(from.getSumY());
        } else if (from.getTotal() > 0) {
            from.setAvgX(
                    sumX.divide(BigDecimal.valueOf(from.getTotal()), 10, RoundingMode.HALF_UP)
                            .doubleValue());
            from.setAvgY(BigDecimal.valueOf(sumY).divide(BigDecimal.valueOf(from.getTotal()), RoundingMode.HALF_UP).doubleValue());
        } else {
            from.setAvgX(0.0);
            from.setAvgY(0.0);
        }
    }
}
