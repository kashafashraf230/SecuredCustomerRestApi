package com.example.customerapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import static com.example.customerapi.ApacheDBCP.dataSource;


@Provider
public class SecurityFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic";
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        String authHeader = containerRequestContext.getHeaderString(AUTHORIZATION_HEADER);
        if(authHeader != null && authHeader.startsWith(AUTHORIZATION_HEADER_PREFIX)){
            String credentials = authHeader.substring(AUTHORIZATION_HEADER_PREFIX.length()).trim();
            byte[] decodedCredentials = Base64.getDecoder().decode(credentials);

            String decodedString = new String (decodedCredentials, StandardCharsets.UTF_8);

            String[] parts = decodedString.split(":");
            String username = parts[0];
            String password = parts[1];

            if(isUser(username, password)){
                logger.info(getLogMessage(containerRequestContext, "Authentication successful"));
                return;
            }

        }
        logger.warn(getLogMessage(containerRequestContext, "Authentication failed"));
        containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("Unauthorized User!")
                .build());
    }

    private boolean isUser(String username, String password){

        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from userroles where username = '"+username+"' and password = '"+password+"'");
            if(resultSet.next()) {

                return true;
            }
            return false;
        }catch(Exception e ){
            System.out.println(e.toString());
        }
        return false;
    }

    private String getLogMessage(ContainerRequestContext requestContext, String message) {
        return String.format("%s_%s_%s_%s",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                requestContext.getMethod(),
                "Authentication",
                message);
    }

}
