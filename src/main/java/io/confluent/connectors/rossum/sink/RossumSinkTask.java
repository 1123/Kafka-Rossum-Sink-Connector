package io.confluent.connectors.rossum.sink;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public final class RossumSinkTask extends SinkTask {

    private RossumClient rossumClient;

    private String queueId;

    public RossumSinkTask() {
    }

    @Override
    public void start(final Map<String, String> props) {
        Objects.requireNonNull(props);
        try {
            this.rossumClient = new RossumClient(props.get("rossum.username"), props.get("rossum.password"), props.get("rossum.company"));
            this.queueId = props.get("rossum.queue.id");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void put(final Collection<SinkRecord> records) {
        log.debug("Received {} records", records.size());
        records.forEach(record -> {
            try {
                rossumClient.uploadByteArray(queueId, (byte []) record.value());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    public void stop() {
    }

    @Override
    public String version() {
        return "0.0.1";
    }

}
