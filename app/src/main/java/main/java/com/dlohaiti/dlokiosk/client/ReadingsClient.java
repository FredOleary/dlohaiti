package main.java.com.dlohaiti.dlokiosk.client;

import main.java.com.dlohaiti.dlokiosk.domain.Reading;
import com.google.inject.Inject;

public class ReadingsClient {
    private final RestClient restClient;

    @Inject
    public ReadingsClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PostResponse send(Reading reading) {
        return restClient.post("/readings", reading);
    }
}
