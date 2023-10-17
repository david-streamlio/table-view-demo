package com.examples.stocks.data.readers;

import com.examples.stocks.PulsarClientAware;
import com.examples.stocks.config.PulsarClientConfig;
import com.examples.stocks.data.readers.threads.StockQuoteTopicListener;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockQuoteMonitor extends PulsarClientAware {

    private static final String STOCK_QUOTE_TOPIC = "persistent://public/default/stock-quotes";

    private static ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws PulsarClientException {
        executor.submit(new StockQuoteTopicListener(PulsarClientConfig.of(BROKER_URL), STOCK_QUOTE_TOPIC));
    }
}
