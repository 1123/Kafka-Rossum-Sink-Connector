package io.confluent.connectors.rossum.sink;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;

public class RossumClient {

    private static final Logger log = LoggerFactory.getLogger(RossumClient.class);

    private final String userName;
    private final String password;
    private final String company;
    private static final String LOGIN_URL_PATTERN = "https://%s.rossum.app/api/v1/auth/login";
    private static final String QUEUES_URL_PATTERN = "https://%s.rossum.app/api/v1/queues?page_size=1";
    private static final String UPLOAD_URL_PATTERN = "https://%s.rossum.app/api/v1/queues/%s/upload";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    @Getter
    private String key;

    RossumClient(String userName, String password, String company)
            throws IOException, InterruptedException {
        this.userName = userName;
        this.password = password;
        this.company = company;
        initializeKey();
    }

    private void initializeKey() throws IOException, InterruptedException {
        final String url = String.format(LOGIN_URL_PATTERN, company);
        log.debug("Getting key from URL: {}" ,url);
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("{\"username\": \"%s\", \"password\": \"%s\"}", userName, password)
                ))
                .uri(URI.create(url))
                .build();
        final var bodyHandler = HttpResponse.BodyHandlers.ofString();
        final HttpResponse<String> response = httpClient.send(httpRequest, bodyHandler);
        log.debug(response.body());
        final HashMap<String,String> mapping = new ObjectMapper().readValue(response.body(), HashMap.class);
        this.key = mapping.get("key");
    }

    public List<RossumQueue> getRossumQueues() throws IOException, InterruptedException {
        final String url = String.format(QUEUES_URL_PATTERN, company);
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + key)
                .uri(URI.create(url))
                .build();
        final var bodyHandler = HttpResponse.BodyHandlers.ofString();
        final var httpResponse = httpClient.send(httpRequest, bodyHandler);
        log.info(httpResponse.body());
        final var rossumQueuesResponse =  new ObjectMapper().readValue(
                httpResponse.body(), RossumQueuesResponse.class
        );
        return rossumQueuesResponse.getResults();
    }

    public UploadResult uploadByteArray(String queueId, byte[] content) {
        File file = new File("tmp.png");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            fileOutputStream.write(content);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var result = this.uploadDocument(queueId, file.getAbsolutePath());
        var deleteResult = file.delete();
        if (!deleteResult) {
            log.warn("Failed to delete file");
            throw new RuntimeException("Failed to delete file");
        }
        return  result;
    }

    public UploadResult uploadDocument(String queueId, String filePath) {
        try (DefaultHttpClient httpclient = new DefaultHttpClient()) {
            final HttpPost postRequest = new HttpPost(String.format(UPLOAD_URL_PATTERN, company, queueId));
            final File file = new File(filePath);
            FileEntity params = new FileEntity(file);
            postRequest.setHeader(HttpHeaders.AUTHORIZATION, "token " + key);
            postRequest.setHeader("Content-Disposition", String.format("attachment; filename=%s", file.getName()));
            postRequest.setHeader(HttpHeaders.ACCEPT, "application/json");
            postRequest.setEntity(params);
            org.apache.http.HttpResponse httpResponse = httpclient.execute(postRequest);
            HttpEntity entity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            log.info(responseString);
            return new ObjectMapper().readValue(responseString, UploadResult.class);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}

