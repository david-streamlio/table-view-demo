package com.examples.stocks.data.readers.threads;

import com.examples.stocks.config.PulsarClientConfig;
import com.examples.stocks.PulsarClientAware;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

public abstract class TopicListener<T> extends PulsarClientAware implements Runnable {

    private String inputTopic;
    private Consumer<Float> consumer;

    public TopicListener(PulsarClientConfig config, String inputTopic) {
        super(config);
        this.inputTopic = inputTopic;
    }

    @Override
    public void run() {
        Message<T> msg = null;

        do {
            try {
                msg = getConsumer().receive();
                System.out.println(msg.getKey() + " " + msg.getValue());
                getConsumer().acknowledge(msg);
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }

        } while (msg != null);

    }

    protected Consumer<T> getConsumer() throws PulsarClientException {
        if (consumer == null) {
            consumer = getClient().newConsumer(getSchema())
                    .subscriptionName("my-sub")
                    .topic(inputTopic)
                    .subscribe();
        }

        return (Consumer<T>) consumer;
    }

    protected abstract Schema getSchema();

}
