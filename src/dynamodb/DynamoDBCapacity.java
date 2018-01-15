package dynamodb;

import java.util.Objects;

public class DynamoDBCapacity {
    private long read;
    private long write;
 
    public DynamoDBCapacity(long read, long write) {
        this.read = read;
        this.write = write;
    }

    public long getRead() {
        return read;
    }

    public void setRead(long read) {
        this.read = read;
    }

    public long getWrite() {
        return write;
    }

    public void setWrite(long write) {
        this.write = write;
    }
    
    @Override
    public String toString() {
        return String.format("read = %s, write = %s", read, write);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof DynamoDBCapacity) {
            DynamoDBCapacity capacity = (DynamoDBCapacity)o;
            if (capacity.getRead() == read && capacity.getWrite() == write) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(read, write);
    }
}
