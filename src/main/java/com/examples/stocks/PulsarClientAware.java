package com.examples.stocks;

import com.examples.stocks.config.PulsarClientConfig;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public abstract class PulsarClientAware {

    //    private static final String BROKER_URL = "pulsar://192.168.1.121:6650";
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
                    .build();
        }
        return client;
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
