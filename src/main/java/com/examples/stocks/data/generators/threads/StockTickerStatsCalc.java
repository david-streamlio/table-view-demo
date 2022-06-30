package com.examples.stocks.data.generators.threads;

import com.examples.stocks.data.domain.StockStatistics;
import com.examples.stocks.data.domain.StockTrade;
import org.apache.pulsar.client.api.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Calculates the following statistics for a stock
 *     - Daily trading volume
 *     - Day High/Low
 */
public class StockTickerStatsCalc extends AbstractDataGenerator {

    private String inputTopic;

    public StockTickerStatsCalc(String inputTopic, String outputTopic) {
        super(outputTopic, StockStatistics.class);
        this.inputTopic = inputTopic;
    }

    @Override
    public void run() {
        try {
            Consumer<StockTrade> stockTradeConsumer =
                    getClient().newConsumer(Schema.JSON(StockTrade.class))
                            .topic(inputTopic)
                            .messageListener(new StockTradeMessageListener(this.createProducer()))
                            .subscriptionName("stock-stats-calculator")
                            .subscribe();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }

    }

    private class StockTradeMessageListener implements MessageListener<StockTrade> {
        private Producer producer;
        private Map<String, StockStatistics> statsTable;

        public StockTradeMessageListener(Producer<StockStatistics> producer) throws PulsarClientException {
            this.producer = producer;
            TableView<StockStatistics> tv = getClient().newTableViewBuilder(Schema.JSON(StockStatistics.class))
                    .autoUpdatePartitionsInterval(10, TimeUnit.SECONDS)
                    .topic(outputTopic)
                    .create();

            // Pre-load the data
            statsTable = new HashMap<String, StockStatistics>();
            tv.keySet().forEach(key -> statsTable.put(key, tv.get(key)));
        }

        @Override
        public void received(Consumer<StockTrade> consumer, Message<StockTrade> msg) {
            try {
                StockStatistics stats;
                if (!statsTable.containsKey(msg.getKey())) {
                    stats = new StockStatistics(msg.getKey(),
                            msg.getValue().getExecutedPrice(),
                            msg.getValue().getExecutedPrice(),
                            0L, System.currentTimeMillis());
                } else {
                    stats = statsTable.get(msg.getKey());
                }

                updateStats(stats, msg.getValue());
                statsTable.put(msg.getKey(), stats);

                producer.newMessage(Schema.JSON(StockStatistics.class))
                        .value(stats)
                        .key(msg.getKey())
                        .send();

                consumer.acknowledge(msg);
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }

        private void updateStats(StockStatistics stats, StockTrade trade) {
            if (stats.getTradeVolume() == null) {
                stats.setTradeVolume(trade.getLotSize().longValue());
            } else {
                stats.setTradeVolume(stats.getTradeVolume() + trade.getLotSize().longValue());
            }

            if (stats.getHighValue() == null || trade.getExecutedPrice() > stats.getHighValue()) {
                stats.setHighValue(trade.getExecutedPrice());
            }

            if (stats.getLowValue() == null || trade.getExecutedPrice() < stats.getLowValue()) {
                stats.setLowValue(trade.getExecutedPrice());
            }

            stats.setTimestamp(System.currentTimeMillis());
        }
    }
}
