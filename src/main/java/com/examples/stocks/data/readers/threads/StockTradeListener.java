package com.examples.stocks.data.readers.threads;

import com.examples.stocks.config.PulsarClientConfig;
import com.examples.stocks.data.domain.StockTrade;

import org.apache.pulsar.client.api.Schema;

public class StockTradeListener extends TopicListener<StockTrade> {

    public StockTradeListener(PulsarClientConfig config, String inputTopic) {
        super(config, inputTopic);
    }

    @Override
    protected Schema getSchema() {
        return Schema.JSON(StockTrade.class);
    }

}
