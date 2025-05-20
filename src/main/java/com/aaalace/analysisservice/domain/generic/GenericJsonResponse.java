package com.aaalace.analysisservice.domain.generic;

public class GenericJsonResponse<T> {
    public State state;
    public T data;
    public String error;

    GenericJsonResponse(State state, T data, String error) {
        this.state = state;
        this.data = data;
        this.error = error;
    }

    public static <T> GenericJsonResponse<T> success(T data) {
        return new GenericJsonResponse<>(State.SUCCESS, data, null);
    }

    public static <T> GenericJsonResponse<T> failure(String error) {
        return new GenericJsonResponse<>(State.FAILURE, null, error);
    }

    public enum State {
        SUCCESS, FAILURE
    }
}
