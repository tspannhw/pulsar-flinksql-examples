package io.ipolyzos;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EventsProducer {
    private static final Logger logger
            = LoggerFactory.getLogger(EventsProducer.class);

    public static void main(String[] args) throws IOException {
        List<Event> events = DataSourceUtils.loadDataFile("/data/events.csv")
                .map(DataSourceUtils::lineAsEvent)
                .collect(Collectors.toList());
        logger.info("Creating Pulsar Client ...");
        PulsarClient pulsarClient = PulsarClient
                .builder()
                .serviceUrl("pulsar://localhost:6650")
                .build();

        logger.info("Creating Orders Producer ...");
        Producer<Event> eventsProducer = pulsarClient
                .newProducer(JSONSchema.of(Event.class))
                .producerName("event-producer")
                .topic("events")
                .create();

        AtomicInteger counter = new AtomicInteger();
        for (Iterator<Event> it = events.iterator(); it.hasNext(); ) {
            Event event = it.next();

            eventsProducer
                    .newMessage()
                    .value(event)
                    .eventTime(System.currentTimeMillis())
                    .send();

            logger.info("✅ Total {} - Sent: {}", counter.getAndIncrement(), event);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Sent '{}' orders.", counter.get());
            logger.info("Closing Resources...");
            try {
                eventsProducer.close();
                pulsarClient.close();
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }));
    }
}
