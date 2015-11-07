package org.simpserv.test;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.util.Headers;

import static io.undertow.Handlers.path;

public class Test {

    public static void main(String[] args) {
        Undertow server = Undertow.builder()
            .addHttpListener(6060, "localhost")
            .setHandler(path()
                .addPrefixPath( "/static", createStaticResourceHandler() )
                .addPrefixPath( "/api",    new InternalRestHandler() )
            ).build();
        server.start();
    }

    private static HttpHandler createStaticResourceHandler() {
        final ResourceManager staticResources = new ClassPathResourceManager(
                (new Test()).getClass().getClassLoader(),"static");
        final ResourceHandler resourceHandler = new ResourceHandler(staticResources);
        resourceHandler.setWelcomeFiles("index.html");
        return resourceHandler;
    }

    private static class InternalRestHandler implements HttpHandler {
        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Hello World U");
        }
    }
}
