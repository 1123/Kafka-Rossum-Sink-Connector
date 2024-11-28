package io.confluent.connectors.rossum.sink;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RossumQueuesResponse {
    private RossumPagination pagination;
    private List<RossumQueue> results;

    public RossumPagination getPagination() {
        return pagination;
    }

    public void setPagination(RossumPagination pagination) {
        this.pagination = pagination;
    }

    public List<RossumQueue> getResults() {
        return results;
    }

    public void setResults(List<RossumQueue> results) {
        this.results = results;
    }
}
