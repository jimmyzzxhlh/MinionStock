package dynamodb;

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
}
