package com.examples.stocks.joins;

import com.examples.stocks.data.domain.StockPosition;
import com.examples.stocks.data.domain.StockQuote;
import com.examples.stocks.PulsarClientAware;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TableView;

import java.util.concurrent.TimeUnit;

/**
 * Join a normal stream of data from a standard Pulsar consumer
 * to a TableView.
 */
public class StockQuoteStreamJoinStockPositionTable extends PulsarClientAware {

    private static final String STOCK_QUOTE_TOPIC = "persistent://public/default/stock-quotes";
    private static final String STOCK_POSITIONS_TOPIC = "persistent://public/default/stock-positions-12345";

    private final String streamTopic;
    private final String tableTopic;
    private Consumer<StockQuote> stockQuoteStream;
    private TableView<StockPosition> table;

    public StockQuoteStreamJoinStockPositionTable(String streamTopic, String tableTopic) {
        this.streamTopic = streamTopic;
        this.tableTopic = tableTopic;
    }

    public void join() throws PulsarClientException {
        table = getClient().newTableViewBuilder(Schema.JSON(StockPosition.class))
                .autoUpdatePartitionsInterval(2, TimeUnit.SECONDS)
                .topic(tableTopic)
                .create();

        stockQuoteStream = getClient().newConsumer(Schema.JSON(StockQuote.class))
                .subscriptionName("portfolio")
                .topic(streamTopic)
                .messageListener((con, msg) -> {
                    StockPosition position = table.get(msg.getValue().getTickerSymbol());
                    if (position != null) {
                        System.out.println(String.format
                                ("%s [ Last Trade: $%.2f  Purchase Price: $%.2f  Shares: %,d  Net Gain: $%,.2f ]",
                                        msg.getValue().getTickerSymbol(), msg.getValue().getQuotePrice(),
                                        position.getPurchasePrice(), position.getQuantity(),
                                        ((msg.getValue().getQuotePrice()) - position.getPurchasePrice()) *
                                                position.getQuantity()));
                    }
                    try {
                        con.acknowledge(msg);
                    } catch (PulsarClientException e) {
                        e.printStackTrace();
                    }
                })
                .subscribe();
    }

    public static void main(String[] args) throws PulsarClientException {
        StockQuoteStreamJoinStockPositionTable demo = new StockQuoteStreamJoinStockPositionTable(
                STOCK_QUOTE_TOPIC, STOCK_POSITIONS_TOPIC);

        demo.join();
    }
}
