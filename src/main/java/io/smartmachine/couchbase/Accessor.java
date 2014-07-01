package io.smartmachine.couchbase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Accessor annotation is used to mark an injectable GenericAccessor or CouchbaseClientFactory field for injection.
 *
 * Here is what you have to do ...
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Accessor {}
