package org.orymar.exception.domin;


public class PaymentServiceException extends RuntimeException {
    public PaymentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}