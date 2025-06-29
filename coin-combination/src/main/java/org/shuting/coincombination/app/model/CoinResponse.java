package org.shuting.coincombination.app.model;

public class CoinResponse {
    private String message;

    public CoinResponse() {}

    public CoinResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
