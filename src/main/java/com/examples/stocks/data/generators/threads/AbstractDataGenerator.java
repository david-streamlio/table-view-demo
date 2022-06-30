package com.examples.stocks.data.generators.threads;

import com.examples.stocks.PulsarClientAware;
import io.streamnative.util.schema.PulsarSchemaUtils;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;

public abstract class AbstractDataGenerator extends PulsarClientAware implements Runnable {

    protected Producer producer;
    protected String outputTopic;
    protected boolean running = true;
    protected final Class clazz;

    public AbstractDataGenerator(String outputTopic, Class clazz) {
        this.outputTopic = outputTopic;
        this.clazz = clazz;
    }

    public void stopRunning() {
        this.running = false;
        try {
            this.close();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    protected Producer createProducer() throws PulsarClientException {
        if (producer == null) {
            producer = this.getClient()
                    .newProducer(PulsarSchemaUtils.getSchemaByClass(clazz))
                    .topic(this.outputTopic)
                    .create();
        }
        return producer;
    }

    @Override
    public abstract void run();
}
