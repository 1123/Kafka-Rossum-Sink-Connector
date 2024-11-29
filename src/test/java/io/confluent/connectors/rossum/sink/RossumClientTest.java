package io.confluent.connectors.rossum.sink;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class RossumClientTest {

    private RossumClient rossumClient;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        assert System.getenv("ROSSUM_USERNAME") != null;
        assert System.getenv("ROSSUM_PASSWORD") != null;
        assert System.getenv("ROSSUM_COMPANY") != null;
        rossumClient = new RossumClient(
                System.getenv("ROSSUM_USERNAME"),
                System.getenv("ROSSUM_PASSWORD"),
                System.getenv("ROSSUM_COMPANY")
        );
    }

    @Test
    public void getKey() throws IOException, InterruptedException {
        log.debug("Retrieved key: {} ", rossumClient.getKey());
        assertNotNull(rossumClient.getKey());
    }

    @Test
    public void testGetQueues() throws IOException, InterruptedException {
        var queues = rossumClient.getRossumQueues();
        log.info(queues.toString());
    }

    /*
     * Note that the file must be an image or similar. The API will not accept a text file.
     */
    @Test
    public void testUploadDocument() throws IOException, InterruptedException {
        final var queues = rossumClient.getRossumQueues();
        final var queueId = queues.get(0).getId();
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        final URL resource = classloader.getResource("confluent-logo.png");
        final String path = resource.getPath();
        UploadResult result = rossumClient.uploadDocument(queueId, path);
        log.info(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    public void testUploadByteArray() throws IOException, InterruptedException {
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource("confluent-logo.png").getFile());
        FileInputStream fileInputStream = new FileInputStream(file);
        final var queues = rossumClient.getRossumQueues();
        final var queueId = queues.get(0).getId();
        UploadResult result = rossumClient.uploadByteArray(queueId, fileInputStream.readAllBytes());
        log.info(new ObjectMapper().writeValueAsString(result));
    }

}

