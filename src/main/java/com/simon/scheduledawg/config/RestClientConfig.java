package com.simon.scheduledawg.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Every outbound call (Anthropic, Resend, Overpass, UGA's bulletin) goes
    // through this one client. Without a timeout, a hung upstream blocks the
    // request thread forever — under load that exhausts Tomcat's thread pool
    // and takes the whole app down for every user, not just the one whose
    // request triggered it.
    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(45_000);
        return RestClient.builder().requestFactory(factory).build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}