package com.examples.stocks.data.generators;

import com.examples.stocks.data.domain.StockQuote;
import com.examples.stocks.PulsarClientAware;
import com.examples.stocks.data.generators.threads.StockQuoteGeneratorThread;
import com.examples.stocks.config.PulsarClientConfig;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockQuoteGenerator extends PulsarClientAware implements Runnable {

    private static final String STOCK_TOPIC = "persistent://public/default/stock-quotes";

    private static ExecutorService executor = Executors.newFixedThreadPool(15);
    static List<StockQuoteGeneratorThread> generators = new ArrayList<StockQuoteGeneratorThread>(10);

    private boolean consume = true;
    private Consumer<StockQuote> consumer;

    static {
        PulsarClientConfig config = PulsarClientConfig.of(BROKER_URL);
        generators.add(new StockQuoteGeneratorThread(STOCK_TOPIC, "APPL", 100.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_TOPIC, "ORCL", 75.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_TOPIC, "GM", 37.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_TOPIC, "XOM", 92.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_TOPIC, "INTL", 18.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_TOPIC, "FDX", 242.0f));
        generators.add(new StockQuoteGeneratorThread(STOCK_TOPIC, "AMD", 86.32f));
        generators.add(new StockQuoteGeneratorThread(STOCK_TOPIC, "BAC", 32.31f));
        generators.add(new StockQuoteGeneratorThread(STOCK_TOPIC, "NFLX", 190.09f));
        generators.add(new StockQuoteGeneratorThread(STOCK_TOPIC, "TSLA", 733.03f));
    }

    public static void main(String[] args) throws PulsarClientException {
        generators.forEach(gen -> executor.submit(gen));
        executor.submit(new StockQuoteGenerator(PulsarClientConfig.of(BROKER_URL)));
    }

    public StockQuoteGenerator(PulsarClientConfig config) {
        super(config);
    }

    @Override
    public void run() {
        Message<StockQuote> msg = null;

        int counter = 0;
        if (consume) {
            do {
                try {
                    msg = getConsumer().receive();
                    System.out.println("[" + counter++ + "]  " + msg.getKey() + " " + msg.getValue().getQuotePrice());
                    getConsumer().acknowledge(msg);
                } catch (PulsarClientException e) {
                    e.printStackTrace();
                }

            } while (msg != null);
        }
    }

    private Consumer<StockQuote> getConsumer() throws PulsarClientException {
        if (consumer == null) {
            consumer = getClient().newConsumer(Schema.JSON(StockQuote.class))
                    .subscriptionName("my-sub")
                    .topic(STOCK_TOPIC)
                    .subscribe();
        }
        return consumer;
    }
}
