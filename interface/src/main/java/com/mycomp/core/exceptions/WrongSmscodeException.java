package com.mycomp.core.exceptions;

import java.io.Serializable;

public class WrongSmscodeException extends Exception implements Serializable {

    public WrongSmscodeException() {
        super();
    }

    public WrongSmscodeException(String message) {
        super(message);
    }

    public WrongSmscodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongSmscodeException(Throwable cause) {
        super(cause);
    }

}
