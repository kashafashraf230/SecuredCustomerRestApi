package com.example.customerapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.example.customerapi.ApacheDBCP.dataSource;

@Path("/customer-resource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private static final Logger logger = LoggerFactory.getLogger(CustomerResource.class);

   static int count = 0;

    // Create a new customer
    @POST
    public Response createCustomer(Customer customer) {

        try {
            logger.info(getLogMessage("createCustomer", "Request received", "POST"));
            customer.setId((long) (count + 1));   // Generate a unique ID and add the customer to the list
            Long id = customer.getId();
            String firstName = customer.getFirstName();
            String lastName = customer.getLastName();
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate("INSERT INTO data (id, firstName, lastName) VALUES('"+id+"', '"+firstName+"', '"+lastName+"')");

            if (result == 1) {
                logger.info(getLogMessage("createCustomer", "Response Sent", "POST"));
                return Response.status(Response.Status.CREATED).entity(customer).build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        logger.error(getLogMessage("createCustomer", "Can't Create Customer", "POST"));
        return Response.status(Response.Status.BAD_REQUEST)
                .build();
    }

    // Read all customers
    @GET
    public List<Customer> getAllCustomers() {

        try {

            logger.info(getLogMessage("getAllCustomers", "Request received", "GET"));
            List<Customer> allCustomers = new ArrayList<>();
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from data");
            while (resultSet.next()) {

                Customer getCustomer = new Customer();
                getCustomer.setId(resultSet.getLong("id"));
                getCustomer.setFirstName(resultSet.getString("firstName"));
                getCustomer.setLastName(resultSet.getString("lastName"));

                allCustomers.add(getCustomer);

            }

            count = allCustomers.size();
            logger.info(getLogMessage("getAllCustomers", "Response sent", "GET"));
            return allCustomers;
        }
        catch (Exception e){
           e.printStackTrace();
        }
        logger.error(getLogMessage("getAllCustomers", "Can't get all Customers", "GET"));
       return null;
    }

    // Read a specific customer by ID
    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        try {

            logger.info(getLogMessage("getCustomerById", "Request received", "GET"));
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from data where id = '"+id+"'");
            if(resultSet.next()) {

                Customer customer = new Customer(resultSet.getLong("id"), resultSet.getString("firstName" ),resultSet.getString("lastName" ));
                logger.info(getLogMessage("getCustomerById", "Response Sent", "GET"));
                return Response.ok(customer).build();
            }
            else {
                logger.error(getLogMessage("getCustomerById", "No Customer exists with this ID", "GET"));
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        logger.info(getLogMessage("getCustomerById", "Bad Request", "GET"));
        return Response.status(Response.Status.BAD_REQUEST)
                .build();
    }

    // Update a customer by ID
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, Customer updatedCustomer) {
        try {

            logger.info(getLogMessage("updateCustomer", "Request received", "PUT"));
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate("UPDATE data SET id = '"+id+"',firstName = '"+updatedCustomer.getFirstName()+"' ,lastName = '"+updatedCustomer.getLastName()+"'  WHERE id='"+id+"'" );
            if(result > 0) {
                Customer customer = new Customer(id, updatedCustomer.getFirstName(), updatedCustomer.getLastName());
                logger.info(getLogMessage("updateCustomer", "Response Sent", "PUT"));
                return Response.ok(customer).build();
            }
            else {
                logger.error(getLogMessage("updateCustomer", "No Customer exists with this ID", "PUT"));
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        logger.error(getLogMessage("updateCustomer", "Bad Request", "PUT"));
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // Delete a customer by ID
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        try {

            logger.info(getLogMessage("deleteCustomer", "Request received", "DELETE"));
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate("delete from data where id = '"+id+"'");
            if(result > 0) {
                logger.info(getLogMessage("deleteCustomer", "Customer Deleted", "DELETE"));
                return Response.noContent().build();
            }
            else {
                logger.error(getLogMessage("deleteCustomer", "No Customer exists with this ID", "DELETE"));
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        logger.error(getLogMessage("deleteCustomer", "Bad Request", "DELETE"));
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private String getLogMessage(String methodName, String message, String reqMethod) {
        return String.format("%s_%s_%s_%s",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                reqMethod,
                methodName,
                message);
    }
}