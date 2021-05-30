package com.events.statsservice.service.impl.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collector;

public class BigDecimalSummaryStatistics implements Consumer<BigDecimal> {

    public static Collector<BigDecimal, ?, BigDecimalSummaryStatistics> statistics() {
        return Collector.of(BigDecimalSummaryStatistics::new,
                BigDecimalSummaryStatistics::accept, BigDecimalSummaryStatistics::merge);
    }

    private BigDecimal sum = BigDecimal.ZERO;
    private long count;

    public void accept(BigDecimal t) {
        if (count == 0) {
            Objects.requireNonNull(t);
            count = 1;
            sum = t;
        } else {
            sum = sum.add(t);
            count++;
        }
    }

    public BigDecimalSummaryStatistics merge(BigDecimalSummaryStatistics s) {
        if (s.count > 0) {
            if (count == 0) {
                count = s.count;
                sum = s.sum;
            } else {
                sum = sum.add(s.sum);
                count += s.count;
            }
        }
        return this;
    }

    public long getCount() {
        return count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAverage(MathContext mc) {
        return count < 2 ? sum : sum.divide(BigDecimal.valueOf(count), mc);
    }
}