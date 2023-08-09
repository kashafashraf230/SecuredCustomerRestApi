package com.example.customerapi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/customer-resource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class CustomerResource {

    private static List<Customer> customers = new ArrayList<>();

    // Create a new customer
    @POST
    public Response createCustomer(Customer customer) {
        // Generate a unique ID and add the customer to the list
        customer.setId((long) (customers.size() + 1));
        customers.add(customer);

        return Response.status(Response.Status.CREATED).entity(customer).build();
    }

    // Read all customers
    @GET
    public List<Customer> getAllCustomers() {
        return customers;
    }

    // Read a specific customer by ID
    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        Customer customer = findCustomerById(id);
        if (customer != null) {
            return Response.ok(customer).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Update a customer by ID
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, Customer updatedCustomer) {
        Customer customer = findCustomerById(id);
        if (customer != null) {
            customer.setFirstName(updatedCustomer.getFirstName());
            customer.setLastName(updatedCustomer.getLastName());
            return Response.ok(customer).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Delete a customer by ID
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        Customer customer = findCustomerById(id);
        if (customer != null) {
            customers.remove(customer);
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private Customer findCustomerById(Long id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }
}