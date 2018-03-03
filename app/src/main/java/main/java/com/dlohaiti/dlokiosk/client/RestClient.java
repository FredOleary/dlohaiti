package main.java.com.dlohaiti.dlokiosk.client;

import main.java.com.dlohaiti.dlokiosk.KioskDate;
import main.java.com.dlohaiti.dlokiosk.db.ConfigurationKey;
import main.java.com.dlohaiti.dlokiosk.db.ConfigurationRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;

import static java.util.Arrays.asList;

public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private final RestTemplate restTemplate;
    private final ConfigurationRepository config;
    private final String baseUrl;

    @Inject
    public RestClient(ConfigurationRepository config, KioskDate kioskDate) {
        this.restTemplate = getJsonRestTemplate(kioskDate.getFormat());
        this.config = config;
        this.baseUrl = config.get(ConfigurationKey.SERVER_URL);
    }

    private RestTemplate getJsonRestTemplate(DateFormat format) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.getObjectMapper().setDateFormat(format);
        converter.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        restTemplate.getMessageConverters().add(converter);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        return restTemplate;
    }

    private MultiValueMap<String, String> requestHeaders() {
        HttpAuthentication authHeader = new HttpBasicAuthentication(
                config.get(ConfigurationKey.KIOSK_ID),
                config.get(ConfigurationKey.KIOSK_PASSWORD)
        );
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return requestHeaders;
    }

    public <T> T get(String uri, Class<T> responseType) {
        return restTemplate.exchange(this.baseUrl + uri, HttpMethod.GET, new HttpEntity<Object>(requestHeaders()), responseType, new Object()).getBody();
    }

    public PostResponse post(String uri, Object data) {
        String url = this.baseUrl + uri;
        try {
            ResponseEntity<PostResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(data, requestHeaders()), PostResponse.class, new Object());
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("Error " + e.getStatusCode() + " while posting data to url " + url, e);
            return new PostResponse(asList("HTTP " + e.getStatusCode() + " ERROR"));
        }
    }
}
