package com.example.customerapi;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


@Path("/customer-resource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    public static BasicDataSource dataSource = null;

    static {
        String driver = "com.mysql.cj.jdbc.Driver";
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/customer");
        dataSource.setUsername("root");
        dataSource.setPassword("admin");

        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxTotal(25);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private static List<Customer> customers = new ArrayList<>();


    // Create a new customer
    @POST
    public Response createCustomer(Customer customer) {
        // Generate a unique ID and add the customer to the list

        try {
            customers.add(customer);
            customer.setId((long) (customers.size() + 1));
            Long id = customer.getId();
            String firstName = customer.getFirstName();
            String lastName = customer.getLastName();
            String username = customer.getUsername();
            String password = customer.getPassword();
            System.out.println("Name" + firstName);
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate("INSERT INTO data (id, firstName, lastName, username, password) VALUES('"+id+"', '"+firstName+"', '"+lastName+"', '"+username+"', '"+password+"')");
            System.out.println("Result" + result);
            if (result == 1) {
                return Response.status(Response.Status.CREATED).entity(customer).build();
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        return null;
    }

    // Read all customers
    @GET
    public List<Customer> getAllCustomers() {

        try {
            List<Customer> allCustomers = new ArrayList<>();
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from data");
            while (resultSet.next()) {

                Customer getCustomer = new Customer();
                getCustomer.setId(resultSet.getLong("id"));
                getCustomer.setFirstName(resultSet.getString("firstName"));
                getCustomer.setLastName(resultSet.getString("lastName"));
                getCustomer.setUsername(resultSet.getString("username"));
                getCustomer.setPassword(resultSet.getString("password"));

                allCustomers.add(getCustomer);

            }
            return allCustomers;
        }
        catch (Exception e){
            System.out.println(e.toString());
        }

       return null;
    }

    // Read a specific customer by ID
    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        try {

            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from data where id = '"+id+"'");
            if(resultSet.next()) {

                Customer customer = new Customer(resultSet.getLong("id"), resultSet.getString("firstName" ),resultSet.getString("lastName" ), resultSet.getString("username"), resultSet.getString("password"));
                return Response.ok(customer).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }

        return null;
    }

    // Update a customer by ID
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, Customer updatedCustomer) {
        try {

            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate("UPDATE data SET id = '"+id+"',firstName = '"+updatedCustomer.getFirstName()+"' ,lastName = '"+updatedCustomer.getLastName()+"', username = '"+updatedCustomer.getUsername()+"' ,password = '"+updatedCustomer.getPassword()+"'  WHERE id='"+id+"'" );
            if(result > 0) {
                Customer customer = new Customer(id, updatedCustomer.getFirstName(), updatedCustomer.getLastName(), updatedCustomer.getUsername(), updatedCustomer.getPassword());
                return Response.ok(customer).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }

        return null;
    }

    // Delete a customer by ID
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        try {

            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate("delete from data where id = '"+id+"'");
            if(result > 0) {
                return Response.noContent().build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }

        return null;
    }

    /*private Customer findCustomerById(Long id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }*/
}