package com.yl.Exception;

/**
 * @author yl
 * @date 2025-05-08 11:32
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }
    public SerializeException(String message, Throwable cause){
        super(message, cause);
    }
}
