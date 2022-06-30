package com.examples.stocks.data.generators.threads;

import com.examples.stocks.data.domain.StockQuote;
import org.apache.pulsar.client.api.Schema;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class StockQuoteGeneratorThread extends AbstractDataGenerator {

    private Random rnd = new Random();
    private Float openingPrice;
    private Float lastPrice;
    private String tickerSymbol;

    public StockQuoteGeneratorThread(String topic, String symbol, Float openingPrice) {
        super(topic, StockQuote.class);
        this.tickerSymbol = symbol;
        this.openingPrice = openingPrice;
        this.lastPrice = openingPrice;
    }

    @Override
    public void run() {

        while (running) {
            try {
                Float newPrice = Math.abs(new BigDecimal(this.lastPrice +
                        (rnd.nextFloat() * (rnd.nextBoolean() ? 1 : -1)))
                        .setScale(2, RoundingMode.HALF_UP).floatValue());

                this.createProducer().newMessage(Schema.JSON(StockQuote.class))
                        .value(new StockQuote(tickerSymbol, newPrice))
                        .key(tickerSymbol)
                        .send();

                lastPrice = newPrice;
                Thread.sleep(500);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
