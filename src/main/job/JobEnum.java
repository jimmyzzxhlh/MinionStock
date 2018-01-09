package main.job;

import java.util.HashMap;
import java.util.Map;

public enum JobEnum {
    UPDATE_COMPANY("UpdateCompany"),
    UPDATE_DAILY_CHART("UpdateDailyChart"),
    BACKFILL_DAILY_CHART("BackfillDailyChart");
    
    private final String enumStr;
    private static final Map<String, JobEnum> map = new HashMap<>();
    
    static {
        for (JobEnum e : JobEnum.values()) {
            map.put(e.enumStr, e);
        }
    }
    
    private JobEnum(String enumStr) {
        this.enumStr = enumStr;
    }
    
    public String toString() {
        return enumStr;
    }
    
    public static JobEnum get(String enumStr) {
        if (map.containsKey(enumStr)) {
            return map.get(enumStr);
        }
        throw new IllegalArgumentException(enumStr + " is not a valid JobEnum.");
    }
}
