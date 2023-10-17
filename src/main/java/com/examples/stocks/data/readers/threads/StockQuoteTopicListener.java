package com.examples.stocks.data.readers.threads;

import com.examples.stocks.config.PulsarClientConfig;
import com.examples.stocks.data.domain.StockQuote;
import org.apache.pulsar.client.api.Schema;

public class StockQuoteTopicListener extends TopicListener<StockQuote> {

    public StockQuoteTopicListener(PulsarClientConfig config, String inputTopic) {
        super(config, inputTopic);
    }

    @Override
    protected Schema getSchema() {
        return Schema.JSON(StockQuote.class);
    }

}
