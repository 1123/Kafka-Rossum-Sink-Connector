package io.confluent.connectors.rossum.sink;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

@Slf4j
public final class RossumSinkConnector extends SinkConnector {

    private Map<String, String> props;

    public RossumSinkConnector() { }

    @Override
    public ConfigDef config() {
        ConfigDef config = new ConfigDef();
        config.define("rossum.username", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Rossum username");
        config.define("rossum.password", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Rossum password");
        config.define("rossum.company", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Rossum company");
        config.define("rossum.queue.id", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Rossum queue id");
        return config;
    }

    @Override
    public void start(final Map<String, String> props) {
        if (props == null) { throw new IllegalArgumentException("Properties cannot be null"); }
        this.props = Collections.unmodifiableMap(props);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return RossumSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(final int maxTasks) {
        return Collections.nCopies(maxTasks, Map.copyOf(props));
    }

    @Override
    public void stop() {
        log.info("Stopping the connector {}", props.get("name"));
    }

    @Override
    public String version() {
        return "0.0.1";
    }
}
