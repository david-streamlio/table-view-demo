package com.examples.stocks.data.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockStatistics {
    private String tickerSymbol;
    private Float lowValue;
    private Float highValue;
    private Long tradeVolume;
    private Long timestamp;
}
