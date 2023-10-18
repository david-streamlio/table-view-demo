package com.examples.stocks;

import com.examples.stocks.config.PulsarClientConfig;
import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public abstract class PulsarClientAware {

    protected static final String BROKER_URL = "pulsar://127.0.0.1:6650";

    private PulsarClient client;
    private PulsarClientConfig config;

    public PulsarClientAware() {
        this(PulsarClientConfig.of(BROKER_URL));
    }

    public PulsarClientAware(PulsarClientConfig config) {
        this.config = config;
    }

    protected PulsarClient getClient() throws PulsarClientException {
        if (client == null) {
            client = PulsarClient.builder()
                    .serviceUrl(config.getBrokerUrl())
                    .authentication(getAuthentication())
                    .build();
        }
        return client;
    }

    private Authentication getAuthentication() {
        return null;
    }

    protected void close() throws PulsarClientException {
        if (client != null) {
            client.close();
        }
    }

    public PulsarClientConfig getConfig() {
        return config;
    }
}
