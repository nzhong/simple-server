package org.simpserv.test;

import javax.ws.rs.core.Application;
import java.util.LinkedHashSet;
import java.util.Set;

public class MyApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new LinkedHashSet<Class<?>>();
        resources.add(HelloResource.class);
        return resources;
    }

}