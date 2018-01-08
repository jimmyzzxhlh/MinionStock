package enums;

import java.util.HashMap;
import java.util.Map;

public enum JobStatusEnum {
    UPDATING("Updating"),    
    DONE("Done"),
    FAILED("Failed");
    
    private final String enumStr;
    private static final Map<String, JobStatusEnum> map = new HashMap<>();
    
    static {
        for (JobStatusEnum e : JobStatusEnum.values()) {
            map.put(e.enumStr, e);
        }
    }
    
    private JobStatusEnum(String enumStr) {
        this.enumStr = enumStr;
    }
    
    public String toString() {
        return enumStr;
    }
    
    public static JobStatusEnum get(String enumStr) {
        if (map.containsKey(enumStr)) {
            return map.get(enumStr);
        }
        throw new IllegalArgumentException(enumStr + " is not a valid JobStatusEnum.");
    }
}
