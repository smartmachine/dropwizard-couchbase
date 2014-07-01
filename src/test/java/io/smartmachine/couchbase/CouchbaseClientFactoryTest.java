package io.smartmachine.couchbase;


import com.couchbase.client.protocol.views.DesignDocument;
import com.couchbase.client.protocol.views.ViewDesign;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class CouchbaseClientFactoryTest {

    private static Logger log = LoggerFactory.getLogger(FooTest.class);

    @Test
    public void clientTest() throws Exception {
        CouchbaseConfiguration conf = new CouchbaseConfiguration();
        List<URI> pool = new ArrayList<>();
        pool.add(new URI("http://localhost:8091/pools"));
        conf.setBucket("config_server");
        conf.setHosts(pool);
        conf.setPassword("goliath");
        CouchbaseClientFactory factory = new CouchbaseClientFactory(conf);
        factory.start();
        DesignDocument doc = new DesignDocument("DEVICE");
        ViewDesign design = new ViewDesign("findAll", "function (doc, meta) {\n" +
                "  if ((/^DEVICE/).test(meta.id)) {\n" +
                "    emit(meta.id, null);\n" +
                "  }\n" +
                "}");
        doc.setView(design);
        factory.client().createDesignDoc(doc);
        factory.client().getView("DEVICE", "123");
    }


}
