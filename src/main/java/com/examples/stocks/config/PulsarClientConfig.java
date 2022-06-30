package com.examples.stocks.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class PulsarClientConfig {

    private String brokerUrl;

}
