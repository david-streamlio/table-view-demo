package com.examples.stocks.data.generators.threads;

import com.examples.stocks.data.domain.StockQuote;
import com.examples.stocks.data.domain.StockTrade;
import org.apache.pulsar.client.api.*;

import java.util.Random;

public class StockTradeGeneratorThread extends AbstractDataGenerator {

    private String stockQuoteTopic;
    private Consumer<StockQuote> consumer;

    public StockTradeGeneratorThread(String inputTopic, String outputTopic) {
        super(outputTopic, StockTrade.class);
        this.stockQuoteTopic = inputTopic;
    }

    @Override
    public void run() {

        try {
            consumer = getClient().newConsumer(Schema.JSON(StockQuote.class))
                    .topic(stockQuoteTopic)
                    .messageListener(new StockQuoteMessageListener(this.createProducer()))
                    .subscriptionName("stock-trade-generator")
                    .subscribe();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }

        while (running) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class StockQuoteMessageListener implements MessageListener<StockQuote> {

        private Random rnd = new Random();
        private final Producer<StockTrade> producer;

        public StockQuoteMessageListener(Producer<StockTrade> producer) {
            this.producer = producer;
        }

        @Override
        public void received(Consumer<StockQuote> consumer, Message<StockQuote> msg) {
            if (rnd.nextBoolean()) { // Execute a trade?
                Integer lotSize = (rnd.nextInt(100)+1) * 10;
                StockTrade trade = new StockTrade(msg.getKey(), lotSize, msg.getValue().getQuotePrice());
                try {
                    producer.newMessage(Schema.JSON(StockTrade.class))
                            .value(trade)
                            .key(msg.getKey())
                            .send();

                    consumer.acknowledge(msg);
                } catch (PulsarClientException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
