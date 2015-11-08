package org.simpserv.app;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.Headers;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.weld.environment.servlet.Listener;
import org.simpserv.rest.controllers.ControllerFactory;
import org.simpserv.servlets.EchoServlet;
import org.simpserv.servlets.HelloServlet;

import javax.servlet.ServletException;

import java.util.ArrayList;
import java.util.List;


public class AppServer {

    public static void main(String[] args) throws ServletException {
        new AppServer("0.0.0.0", 6060)
                .addHandler( "/static",  createStaticResourceHandler() )
                .addHandler( "/api",     new InternalApiHandler() )
                .addHandler( "/serv",    createServletHandler() )
                .addHandler( "/rest",    createRestApiHandler( "/rest", "/api" ) )
                .start();
    }

    private final String host;
    private final int port;
    private final List<Pair<String,HttpHandler>> handlers;
    public AppServer( final String host, final Integer port ) {
        this.host = host;
        this.port = port;
        this.handlers = new ArrayList<>();
    }

    protected AppServer addHandler( final String path, final HttpHandler handler ) {
        this.handlers.add(Pair.of(path, handler));
        return this;
    }

    protected void start() {
        PathHandler pathHandlers = Handlers.path();
        for ( Pair<String,HttpHandler> handler : this.handlers ) {
            pathHandlers.addPrefixPath( handler.getLeft(), handler.getRight() );
        }

        Undertow server = Undertow.builder()
            .addHttpListener(port, host)
            .setHandler(pathHandlers)
            .build();
        server.start();
    }

    private static HttpHandler createStaticResourceHandler() {
        final ResourceManager staticResources = new ClassPathResourceManager(
                AppServer.class.getClassLoader(),"static");
        final ResourceHandler resourceHandler = new ResourceHandler(staticResources);
        resourceHandler.setWelcomeFiles("index.html");
        return resourceHandler;
    }

    private static class InternalApiHandler implements HttpHandler {
        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Hello World From the InternalApiHandler");
        }
    }

    private static HttpHandler createServletHandler() throws ServletException {
        DeploymentInfo di = Servlets.deployment()
                .setClassLoader(AppServer.class.getClassLoader())
                .setContextPath("/serv")
                .setDeploymentName("Servlet APIs")
                .addServlets(
                        Servlets.servlet("helloServlet", HelloServlet.class).addMapping("/hello"),
                        Servlets.servlet("echoServlet", EchoServlet.class).addMapping("/echo")
                );
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(di);
        manager.deploy();
        HttpHandler servletHandler = manager.start();
        return servletHandler;
    }

    private static HttpHandler createRestApiHandler( final String contextPath, final String appPath ) throws ServletException {
        final UndertowJaxrsServer server = new UndertowJaxrsServer();

        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
        deployment.setApplicationClass( ControllerFactory.class.getName() );
        DeploymentInfo di = server.undertowDeployment(deployment, appPath )
                .setClassLoader(AppServer.class.getClassLoader())
                .setContextPath( contextPath )
                .setDeploymentName("Rest APIs");
        di.addListeners(Servlets.listener(Listener.class));
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(di);
        manager.deploy();
        HttpHandler servletHandler = manager.start();
        return servletHandler;
    }
}
