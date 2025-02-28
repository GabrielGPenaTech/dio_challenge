package br.com.dio.exception;

public class CardCancelledException extends RuntimeException {
    public CardCancelledException(String message) {
        super(message);
    }
}
