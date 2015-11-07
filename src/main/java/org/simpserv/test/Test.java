package org.simpserv.test;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.Headers;
import org.simpserv.servlets.EchoServlet;
import org.simpserv.servlets.HelloServlet;

import static io.undertow.Handlers.path;

public class Test {

    public static void main(String[] args) throws Exception {
        Undertow server = Undertow.builder()
            .addHttpListener(6060, "localhost")
            .setHandler(path()
                .addPrefixPath( "/static", createStaticResourceHandler() )
                .addPrefixPath( "/api",    new InternalRestHandler() )
                .addPrefixPath( "/serv",   createServletHandler() )
            ).build();
        server.start();
    }

    private static HttpHandler createStaticResourceHandler() {
        final ResourceManager staticResources = new ClassPathResourceManager(
                Test.class.getClassLoader(),"static");
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

    private static HttpHandler createServletHandler() throws Exception {
        DeploymentInfo di = Servlets.deployment()
                .setClassLoader(Test.class.getClassLoader())
                .setContextPath("/serv")
                .setDeploymentName("My Servlets")
                .addServlets(
                    Servlets.servlet("helloServlet", HelloServlet.class).addMapping("/hello"),
                    Servlets.servlet("echoServlet", EchoServlet.class).addMapping("/echo")
                );
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(di);
        manager.deploy();
        HttpHandler servletHandler = manager.start();
        return servletHandler;
    }
}
