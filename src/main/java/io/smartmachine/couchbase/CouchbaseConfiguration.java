package io.smartmachine.couchbase;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CouchbaseConfiguration {

    @Valid
    @NotNull
    private List<URI> hosts = new ArrayList<>();

    {
        try {
            hosts.add(new URI("http://localhost:8091/pools"));
        } catch (URISyntaxException e) {
            hosts = null;
        }
    }

    @Valid
    @NotEmpty
    private String bucket = "default";

    @Valid
    private String password = null;

    @JsonProperty
    public List<URI> getHosts() {
        return hosts;
    }

    @JsonProperty
    public void setHosts(List<URI> hosts) {
        this.hosts = hosts;
    }

    @JsonProperty
    public String getBucket() {
        return bucket;
    }

    @JsonProperty
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

}
