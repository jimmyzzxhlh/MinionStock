package main.job;

import java.time.LocalTime;

import dynamodb.DynamoDBCapacity;

public class JobConfig {
  private DynamoDBCapacity workCapacity;
  private DynamoDBCapacity idleCapacity;
  private LocalTime startTime;
  private String tableName;
  private String jobTarget;  // This is for logging purpose only.
  
  public DynamoDBCapacity getWorkCapacity() {
    return workCapacity;
  }
  public JobConfig withWorkCapacity(DynamoDBCapacity workCapacity) {
    this.workCapacity = workCapacity;
    return this;
  }  
  public DynamoDBCapacity getIdleCapacity() {
    return idleCapacity;
  }
  public JobConfig withIdleCapacity(DynamoDBCapacity idleCapacity) {
    this.idleCapacity = idleCapacity;
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
  public String getJobTarget() {
    return jobTarget;
  }
  public JobConfig withJobTarget(String jobTarget) {
    this.jobTarget = jobTarget;
    return this;
  }
  
}
