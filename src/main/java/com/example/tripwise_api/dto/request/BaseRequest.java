package com.example.tripwise_api.dto.request;

public abstract class BaseRequest {
    private String provider;
    private String rawPayload;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }
}

