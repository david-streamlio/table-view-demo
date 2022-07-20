package com.examples.stocks.joins;

import com.examples.stocks.data.domain.StockQuote;
import com.examples.stocks.data.domain.StockStatistics;
import com.examples.stocks.PulsarClientAware;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TableView;

import java.util.concurrent.TimeUnit;

public class StockQuoteTableJoinStockStatsTable extends PulsarClientAware {

    private static final String STOCK_QUOTE_TOPIC = "persistent://public/default/stock-quotes";
    private static final String STOCK_STATS_TOPIC = "persistent://public/default/stock-stats";

    private final String stockQuoteTopic;
    private final String stockStatsTopic;
    private TableView<StockQuote> stockQuoteTable;
    private TableView<StockStatistics> stockStatisticsTable;

    public StockQuoteTableJoinStockStatsTable(String stockQuoteTopic, String stockStatsTopic) {
        this.stockQuoteTopic = stockQuoteTopic;
        this.stockStatsTopic = stockStatsTopic;
    }

    public void join() throws PulsarClientException {
        stockQuoteTable = getClient().newTableViewBuilder(Schema.JSON(StockQuote.class))
                .autoUpdatePartitionsInterval(2, TimeUnit.SECONDS)
                .topic(stockQuoteTopic)
                .create();

        stockStatisticsTable = getClient().newTableViewBuilder(Schema.JSON(StockStatistics.class))
                .autoUpdatePartitionsInterval(2, TimeUnit.SECONDS)
                .topic(stockStatsTopic)
                .create();

        stockQuoteTable.forEach((symbol, quote) -> {
            StockStatistics stats = stockStatisticsTable.get(symbol);

            if (stats != null) {
                System.out.println(String.format("%s [Quote: %.2f Low: %.2f  High: %.2f  Volume: %,d  Time: %tT]",
                        symbol, quote.getQuotePrice(), stats.getLowValue(),
                        stats.getHighValue(), stats.getTradeVolume(), stats.getTimestamp()));
            }
        });
    }

    public static void main(String[] args) throws PulsarClientException {
        StockQuoteTableJoinStockStatsTable demo =
                new StockQuoteTableJoinStockStatsTable(STOCK_QUOTE_TOPIC, STOCK_STATS_TOPIC);

        demo.join();
    }

}
