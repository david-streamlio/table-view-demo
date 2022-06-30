package com.examples.stocks.data.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class StockPosition {
    private String tickerSymbol;
    private Long quantity;
    private Float purchasePrice;
}
