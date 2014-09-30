package at.ac.tuwien.dsg.smartsociety.demo.rest;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import at.ac.tuwien.dsg.smartsociety.demo.rest.common.Util;
import at.ac.tuwien.dsg.smartsociety.demo.rest.config.AppConfig;

public class Starter {

    private static final String PROP_FILE = "smartsoc.properties";

    public static void main( final String[] args ) throws Exception {
        Resource.setDefaultUseCaches( false );

        int serverPort = Integer.parseInt(Util.getProperty(PROP_FILE, "SERVER_PORT"));
        String restContextPath = Util.getProperty(PROP_FILE, "REST_CONTEXT_PATH");
        String swaggerContextPath = Util.getProperty(PROP_FILE, "SWAGGER_CONTEXT_PATH");
        
        final Server server = new Server( serverPort );
        System.setProperty( AppConfig.SERVER_PORT, Integer.toString(serverPort) );
        System.setProperty( AppConfig.SERVER_HOST, Util.getProperty(PROP_FILE, "SERVER_HOST") );
        System.setProperty( AppConfig.CONTEXT_PATH, restContextPath );				

        // Configuring Apache CXF servlet and Spring listener  
        final ServletHolder servletHolder = new ServletHolder( new CXFServlet() ); 		 		
        final ServletContextHandler context = new ServletContextHandler(); 		
        context.setContextPath( "/" );
        context.addServlet( servletHolder, "/" + restContextPath + "/*" ); 	 		
        context.addEventListener( new ContextLoaderListener() ); 		 		
        context.setInitParameter( "contextClass", AnnotationConfigWebApplicationContext.class.getName() );
        context.setInitParameter( "contextConfigLocation", AppConfig.class.getName() );

        // Configuring Swagger as static web resource
        final ServletHolder swaggerHolder = new ServletHolder( new DefaultServlet() );
        final ServletContextHandler swagger = new ServletContextHandler();
        swagger.setContextPath( "/" + swaggerContextPath );
        swagger.addServlet( swaggerHolder, "/*" );
        swagger.setResourceBase( new ClassPathResource( "/webapp" ).getURI().toString() );

        final HandlerList handlers = new HandlerList();
        handlers.addHandler( context );
        handlers.addHandler( swagger );

        server.setHandler( handlers );
        server.start();
        server.join();	
    }
}

