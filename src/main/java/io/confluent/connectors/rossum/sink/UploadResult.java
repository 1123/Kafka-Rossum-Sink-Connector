package io.confluent.connectors.rossum.sink;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResult {

    private String document = null;
    private String annotation = null;

    public UploadResult() { }

    public UploadResult(String document, String annotation) {
        this.document = document;
        this.annotation = annotation;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}
