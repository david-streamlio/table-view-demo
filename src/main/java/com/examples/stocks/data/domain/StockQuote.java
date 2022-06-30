package com.examples.stocks.data.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockQuote {
    private String tickerSymbol;
    private Float quotePrice;
}
