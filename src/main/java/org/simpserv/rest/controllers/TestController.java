package org.simpserv.rest.controllers;

import org.simpserv.rest.bean.AnyBean;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/mt")
public class TestController implements Controller {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test(){
        return "Hello Any Test";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/json")
    public AnyBean json(){
        return new AnyBean();
    }

}
