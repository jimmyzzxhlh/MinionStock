package main.job;

import java.time.LocalTime;

import dynamodb.DynamoDBCapacity;

public class JobConfig {
    private DynamoDBCapacity capacity;
    private LocalTime startTime;
    private String tableName;
    
    public DynamoDBCapacity getCapacity() {
        return capacity;
    }
    public JobConfig withCapacity(DynamoDBCapacity capacity) {
        this.capacity = capacity;
        return this;
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public JobConfig withStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }
    public String getTableName() {
        return tableName;
    }
    public JobConfig withTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }
}
