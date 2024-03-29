package com.examples.stocks.data.readers;

import com.examples.stocks.PulsarClientAware;
import com.examples.stocks.data.readers.threads.StockTradeListener;
import com.examples.stocks.config.PulsarClientConfig;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockTradeMonitor extends PulsarClientAware {

    private static final String STOCK_TRADE_TOPIC = "persistent://public/default/stock-trades";

    private static ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws PulsarClientException {
        executor.submit(new StockTradeListener(PulsarClientConfig.of(BROKER_URL), STOCK_TRADE_TOPIC));
    }

}
