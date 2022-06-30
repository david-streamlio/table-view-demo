package com.examples.stocks.data.generators;

import com.examples.stocks.data.domain.StockPosition;
import com.examples.stocks.config.PulsarClientConfig;
import com.examples.stocks.data.generators.threads.StockQuoteGeneratorThread;
import com.examples.stocks.data.generators.threads.StockTradeGeneratorThread;
import com.examples.stocks.data.generators.threads.StockPositionGeneratorThread;
import com.examples.stocks.data.generators.threads.StockTickerStatsCalc;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockTradeGenerator {

    private static final String BROKER_URL = "pulsar://127.0.0.1:6650";
    private static final String STOCK_QUOTE_TOPIC = "persistent://public/default/stock-quotes";
    private static final String STOCK_TRADE_TOPIC = "persistent://public/default/stock-trades";
    private static final String STOCK_STATS_TOPIC = "persistent://public/default/stock-stats";
    private static final String STOCK_POSITIONS_TOPIC = "persistent://public/default/stock-positions-12345";

    private static ExecutorService executor = Executors.newFixedThreadPool(15);
    private static List<StockQuoteGeneratorThread> generators = new ArrayList<StockQuoteGeneratorThread>(10);
    private static PulsarClientConfig config;

    static {
        generators.add(new StockQuoteGeneratorThread(STOCK_QUOTE_TOPIC, "NFLX", 190.09f));
        generators.add(new StockQuoteGeneratorThread(STOCK_QUOTE_TOPIC, "APPL", 100.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_QUOTE_TOPIC, "ORCL", 75.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_QUOTE_TOPIC, "INTL", 18.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_QUOTE_TOPIC, "GM", 37.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_QUOTE_TOPIC, "XOM", 92.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_QUOTE_TOPIC, "FDX", 242.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_QUOTE_TOPIC, "AMD", 86.32f));
        generators.add(new StockQuoteGeneratorThread(STOCK_QUOTE_TOPIC, "BAC", 32.31f));
        generators.add(new StockQuoteGeneratorThread(STOCK_QUOTE_TOPIC, "TSLA", 733.03f));
    }

    public static void main(String[] args) throws PulsarClientException {
        executor.submit(new StockPositionGeneratorThread(STOCK_POSITIONS_TOPIC, StockPosition.class));
        generators.forEach(gen -> executor.submit(gen));
        executor.submit(new StockTradeGeneratorThread(STOCK_QUOTE_TOPIC, STOCK_TRADE_TOPIC));
        executor.submit(new StockTickerStatsCalc(STOCK_TRADE_TOPIC, STOCK_STATS_TOPIC));
    }

}
