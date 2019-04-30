package story;

public class EmptyJailException extends RuntimeException{
    public EmptyJailException() {
        super();
    }
    public EmptyJailException(String s) {
        super(s);
    }
}
