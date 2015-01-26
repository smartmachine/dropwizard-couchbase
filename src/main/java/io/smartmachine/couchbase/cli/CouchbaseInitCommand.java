package io.smartmachine.couchbase.cli;

import eu.infomas.annotation.AnnotationDetector;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import io.smartmachine.couchbase.Accessor;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.CouchbaseConfiguration;
import io.smartmachine.couchbase.spi.AccessorFactory;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class CouchbaseInitCommand<T extends Configuration> extends ConfiguredCommand<T> {

    private CouchbaseConfiguration<T> strategy = null;
    private Class<T> klass = null;

    private static Logger log = LoggerFactory.getLogger(CouchbaseInitCommand.class);

    public CouchbaseInitCommand(String name, String description, CouchbaseConfiguration<T> strategy, Class<T> klass) {
        super(name, description);
        this.strategy = strategy;
        this.klass = klass;
    }

    @Override
    protected Class<T> getConfigurationClass() {
        return klass;
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);
    }

    @Override
    protected void run(Bootstrap<T> bootstrap, Namespace namespace, T configuration) throws Exception {

        final CouchbaseClientFactory factory = strategy.getCouchbaseClientFactory(configuration);
        factory.start();

        log.info("Let's see if we can find any Resource classes ...");
        final AnnotationDetector.FieldReporter reporter = new AnnotationDetector.FieldReporter() {

            @Override
            public void reportFieldAnnotation(Class<? extends Annotation> annotation, String className, String fieldName) {
                log.info("Annotation: " + annotation.getSimpleName() + " ClassName: " + className + " FieldName: " + fieldName);
                Field field = null;
                try {
                    field = Class.forName(className).getDeclaredField(fieldName);
                    log.info("Field type is: " + field.getType().getSimpleName());
                    AccessorFactory.getFactory().getAccessor(field.getType(), factory);
                } catch (Exception e) {
                    log.error("Unable to retrieve annotated field.", e);
                }
            }

            @Override
            public Class<? extends Annotation>[] annotations() {
                return new Class[] {Accessor.class};
            }
        };

        final AnnotationDetector cf = new AnnotationDetector(reporter);
        cf.detect();

        factory.stop();

    }
}
