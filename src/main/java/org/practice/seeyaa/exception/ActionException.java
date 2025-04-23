package org.practice.seeyaa.exception;

public class ActionException extends RuntimeException {
    public ActionException(String message) {
        super(message);
    }

    public ActionException(Throwable cause) {
        super(cause);
    }

}
