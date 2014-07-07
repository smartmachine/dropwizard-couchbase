package io.smartmachine.couchbase.api;

import org.hibernate.validator.constraints.NotEmpty;

public class BrokenTester {

    @NotEmpty
    private final String name;

    @NotEmpty
    private final String someOtherProperty;

    public BrokenTester(String name, String someOtherProperty) {
        this.name = name;
        this.someOtherProperty = someOtherProperty;
    }

}
