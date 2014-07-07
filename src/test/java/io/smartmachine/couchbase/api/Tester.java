package io.smartmachine.couchbase.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class Tester {

    @NotEmpty
    private final String name;

    @NotEmpty
    private final String someOtherProperty;

    @JsonCreator
    public Tester(@JsonProperty("name") String name, @JsonProperty("other") String someOtherProperty) {
        this.name = name;
        this.someOtherProperty = someOtherProperty;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("other")
    public String getSomeOtherProperty() {
        return someOtherProperty;
    }
}
