package example.in_continue_dev.ex.customException;


public class NotFoundTokenException extends RuntimeException {
    public NotFoundTokenException() {
    }

    public NotFoundTokenException(String message) {
        super(message);
    }

    public NotFoundTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundTokenException(Throwable cause) {
        super(cause);
    }

    public NotFoundTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
