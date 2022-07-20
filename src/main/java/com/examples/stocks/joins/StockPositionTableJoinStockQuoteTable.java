package com.examples.stocks.joins;

import com.examples.stocks.PulsarClientAware;
import com.examples.stocks.data.domain.StockPosition;
import com.examples.stocks.data.domain.StockQuote;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TableView;

import java.util.concurrent.TimeUnit;

public class StockPositionTableJoinStockQuoteTable extends PulsarClientAware {

    private static final String STOCK_QUOTE_TOPIC = "persistent://public/default/stock-quotes";
    private static final String STOCK_POSITIONS_TOPIC = "persistent://public/default/stock-positions-12345";

    private final String stockQuoteTopic;
    private final String stockPositionsTopic;
    private TableView<StockQuote> stockQuoteTable;
    private TableView<StockPosition> stockPositionTable;

    public StockPositionTableJoinStockQuoteTable(String stockQuoteTopic, String stockPositionsTopic) {
        this.stockQuoteTopic = stockQuoteTopic;
        this.stockPositionsTopic = stockPositionsTopic;
    }

    protected TableView<StockQuote> getStockQuoteTable() throws PulsarClientException {
        if (stockQuoteTable == null) {
            stockQuoteTable = getClient().newTableViewBuilder(Schema.JSON(StockQuote.class))
                    .autoUpdatePartitionsInterval(2, TimeUnit.SECONDS)
                    .topic(stockQuoteTopic)
                    .create();
        }
        return stockQuoteTable;
    }

    protected TableView<StockPosition> getStockPositionTable() throws PulsarClientException {
        if (stockPositionTable == null) {
            stockPositionTable = getClient().newTableViewBuilder(Schema.JSON(StockPosition.class))
                    .autoUpdatePartitionsInterval(2, TimeUnit.SECONDS)
                    .topic(stockPositionsTopic)
                    .create();

        }
        return stockPositionTable;
    }

    public void join() throws PulsarClientException {
        getStockPositionTable().forEachAndListen((symbol, position) -> {
            StockQuote quote = null;
            try {
                quote = getStockQuoteTable().get(symbol);
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }

            if (quote != null) {
                System.out.println(String.format
                        ("%s [ Last Trade: $%.2f  Purchase Price: $%.2f  Shares: %,d  Net Gain: $%,.2f ]",
                                symbol, quote.getQuotePrice(), position.getPurchasePrice(),
                                position.getQuantity(),
                                ((quote.getQuotePrice()) - position.getPurchasePrice()) * position.getQuantity()));
            }
        });
    }

    public static void main(String[] args) throws PulsarClientException {
        StockPositionTableJoinStockQuoteTable demo = new StockPositionTableJoinStockQuoteTable(
                STOCK_QUOTE_TOPIC, STOCK_POSITIONS_TOPIC);

        demo.join();
    }

}
