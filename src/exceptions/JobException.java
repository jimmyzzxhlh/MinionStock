package exceptions;

public class JobException extends Exception {
    private static final long serialVersionUID = 4863375458744782042L;
    
    public JobException(String message) {
        super(message);
    }
    
    public JobException(Exception e) {
        super(e);
    }
}
