package com.examples.stocks.data.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockTrade {
    private String tickerSymbol;
    private Integer lotSize;
    private Float executedPrice;
}
