package com.example.customerapi;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Provider
public class SecurityFilter implements ContainerRequestFilter {

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

            if(username.equals("user") && password.equals("pwd")){
                return;
            }

        }
        containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("Unauthorized User!")
                .build());
    }

}
