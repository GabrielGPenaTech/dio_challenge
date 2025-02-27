package br.com.dio.exception;

public class CardCanceledException extends RuntimeException {
    public CardCanceledException(String message) {
        super(message);
    }
}
