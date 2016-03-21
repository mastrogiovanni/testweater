package com.crossover.trial.weather.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A collected point, including some information about the range of collected values
 *
 * @author code test administrator
 */
public class DataPoint {

    private double mean = 0.0;

    private int first = 0;

    private int second = 0;

    private int third = 0;

    private int count = 0;
    
    /** private constructor, use the builder to create this object */
    private DataPoint() {
    }

    /** the mean of the observations */
    public double getMean() {
        return mean;
    }

    private void setMean(double mean) { 
    	this.mean = mean; 
    }

    /** 1st quartile -- useful as a lower bound */
    public int getFirst() {
        return first;
    }

    private void setFirst(int first) {
        this.first = first;
    }

    /** 2nd quartile -- median value */
    public int getSecond() {
        return second;
    }

    private void setSecond(int second) {
        this.second = second;
    }

    /** 3rd quartile value -- less noisy upper value */
    public int getThird() {
        return third;
    }

    private void setThird(int third) {
        this.third = third;
    }

    /** the total number of measurements */
    public int getCount() {
        return count;
    }

    private void setCount(int count) {
        this.count = count;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public boolean equals(Object that) {
        return this.toString().equals(that.toString());
    }

    public static class Builder {
    	
        int first;
        double mean;
        int median;
        int last;
        int count;

        public Builder() {}
        
        public Builder from(DataPoint dp) {
        	this.first = dp.getFirst();
        	this.mean = dp.getMean();
        	this.median = dp.getSecond();
        	this.last = dp.getThird();
        	this.count = dp.getCount();
        	return this;
        }

        public Builder withFirst(int first) {
            this.first = first;
            return this;
        }

        public Builder withMean(double mean) {
        	this.mean = mean;
            return this;
        }

        public Builder withMedian(int median) {
        	this.median = median;
            return this;
        }

        public Builder withCount(int count) {
        	this.count = count;
            return this;
        }

        public Builder withLast(int last) {
        	this.last = last;
            return this;
        }

        public DataPoint build() {
        	DataPoint dp = new DataPoint();
        	dp.setFirst(this.first);
        	dp.setMean(this.mean);
        	dp.setSecond(this.median);
        	dp.setThird(this.last);
        	dp.setCount(this.count);
        	return dp;
        }
        
    }
}
