package com.events.statsservice.model;

public class Statistics {

    private double sumX;
    private double avgX;
    private long sumY;
    private double avgY;
    private long total;
    private String totalValueString;

    private Statistics(Builder builder) {
        setSumX(builder.sumX);
        setAvgX(builder.avgX);
        setSumY(builder.sumY);
        setAvgY(builder.avgY);
        setTotal(builder.total);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public double getSumX() {
        return sumX;
    }

    public long getSumY() {
        return sumY;
    }


    public void setSumX(double sumX) {
        this.sumX = sumX;
    }

    public void setSumY(long sumY) {
        this.sumY = sumY;
    }


    public double getAvgX() {
        return avgX;
    }

    public double getAvgY() {
        return avgY;
    }

    public void setAvgX(double avgX) {
        this.avgX = avgX;
    }

    public void setAvgY(double avgY) {
        this.avgY = avgY;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }


    public static final class Builder {
        private double sumX;
        private double avgX;
        private long sumY;
        private double avgY;
        private long total;

        private Builder() {
        }

        public Builder withSumX(double sumX) {
            this.sumX = sumX;
            return this;
        }

        public Builder withSumY(long sumY) {
            this.sumY = sumY;
            return this;
        }

        public Builder withAvgX(double avgX) {
            this.avgX = avgX;
            return this;
        }

        public Builder withAvgY(double avgY) {
            this.avgY = avgY;
            return this;
        }


        public Builder withTotal(long total) {
            this.total = total;
            return this;
        }

        public Statistics build() {
            return new Statistics(this);
        }
    }
}
