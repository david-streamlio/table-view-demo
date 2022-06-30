package com.examples.stocks.data.readers.threads;

import com.examples.stocks.config.PulsarClientConfig;
import org.apache.pulsar.client.api.Schema;

public class StockQuoteTopicListener extends TopicListener<Float> {

    public StockQuoteTopicListener(PulsarClientConfig config, String inputTopic) {
        super(config, inputTopic);
    }

    @Override
    protected Schema getSchema() {
        return Schema.FLOAT;
    }

}
