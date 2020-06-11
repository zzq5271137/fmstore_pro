package com.mycomp.core.exceptions;

import java.io.Serializable;

public class DeleteGoodsFromDBException extends Exception implements Serializable {

    public DeleteGoodsFromDBException() {
        super();
    }

    public DeleteGoodsFromDBException(String message) {
        super(message);
    }

    public DeleteGoodsFromDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteGoodsFromDBException(Throwable cause) {
        super(cause);
    }

}
