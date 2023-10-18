package com.examples.stocks.data.generators.threads;

import com.examples.stocks.data.domain.StockPosition;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StockPositionGeneratorThread extends AbstractDataGenerator {

    private Random rnd = new Random();
    private Map<String, StockPosition> portfolio;
    private List<String> availableSymbols = Stream.of("INTL", "GM", "XOM", "AMD", "TSLA").collect(Collectors.toList());

    public StockPositionGeneratorThread( String outputTopic, Class clazz) {
        super(outputTopic, clazz);
    }

    protected Map<String, StockPosition> getPortfolio() {
        if (portfolio == null) {
            portfolio = new HashMap<String, StockPosition>();
            List<String> stocks = Stream.of("NFLX", "APPL", "ORCL").collect(Collectors.toList());
            for (String symbol : stocks) {
                portfolio.put(symbol, StockPosition.of(symbol,
                        rnd.nextInt(5000) + 0L,
                        rnd.nextInt(200)/1.0f));
            }
        }
        return portfolio;
    }

    private String getRandomSymbol() {
        if (!availableSymbols.isEmpty()) {
            int idx = rnd.nextInt(availableSymbols.size());
            String symbol = availableSymbols.get(idx);
            availableSymbols.remove(idx);
            return symbol;
        }
        return null;
    }

    private StockPosition getRandomStock() {
        int index = rnd.nextInt(getPortfolio().entrySet().size() + availableSymbols.size());

        if (index >= getPortfolio().size()) {
            String symbol = getRandomSymbol();
            if (symbol != null) {
                return StockPosition.of(symbol, rnd.nextInt(5000) + 0L,
                        rnd.nextInt(200) / 1.0f);
            } else {
                return getRandomStock();
            }
        } else {
            Iterator<Map.Entry<String, StockPosition>> iter = getPortfolio().entrySet().iterator();
            for (int i = 0; i < index; i++) {
                iter.next();
            }
            return iter.next().getValue();
        }
    }

    private void updatePortfolio() throws PulsarClientException {
        // Pick one stock to modify
        StockPosition position = getRandomStock();

        // Simulate either a purchase of additional shares or the sale of existing shares.
        if (rnd.nextBoolean()) {
            // Buy
            position.setQuantity(position.getQuantity() + (rnd.nextInt(100) + 0L));
        } else {
            position.setQuantity(position.getQuantity() - (rnd.nextInt(position.getQuantity().intValue()) + 0L));
        }

        this.getPortfolio().put(position.getTickerSymbol(), position);

        createProducer().newMessage(Schema.JSON(StockPosition.class))
                .value(position)
                .key(position.getTickerSymbol())
                .send();
    }

    @Override
    public void run() {

        getPortfolio().entrySet().forEach((stockPosition -> {
            try {
                createProducer().newMessage(Schema.JSON(StockPosition.class))
                        .value(stockPosition.getValue())
                        .key(stockPosition.getValue().getTickerSymbol())
                        .send();
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }));

        while (running) {
            try {
                Thread.sleep(10 * 1000L);
                if (rnd.nextInt(100) < 50) {
                    updatePortfolio();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
