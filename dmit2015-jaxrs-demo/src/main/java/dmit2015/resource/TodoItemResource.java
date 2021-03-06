package dmit2015.resource;

import common.validator.BeanValidator;
import dmit2015.entity.TodoItem;
import dmit2015.repository.TodoItemRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.net.URI;
import java.util.Optional;


/**
 * * Web API with CRUD methods for managing TodoItem.
 *
 *  URI						    Http Method     Request Body		                        Description
 * 	----------------------      -----------		------------------------------------------- ------------------------------------------
 *	/webapi/TodoItems			POST			{"name":"Demo DMIT2015 assignment 1",       Create a new TodoItem
 *                                         	    "complete":false}
 * 	/webapi/TodoItems/{id}		GET			                                                Find one TodoItem with a id value
 * 	/webapi/TodoItems		    GET			                                                Find all TodoItem
 * 	/webapi/TodoItems/{id}      PUT             {"id":5,                                    Update the TodoItem
 * 	                                            "name":"Submitted DMIT2015 assignment 7",
 *                                              "complete":true}
 * 	/webapi/TodoItems/{id}		DELETE			                                            Remove the TodoItem
 *

 curl -i -X GET http://localhost:8080/dmit2015-1212-jaxrs-demo/webapi/TodoItems

 curl -i -X GET http://localhost:8080/dmit2015-1212-jaxrs-demo/webapi/TodoItems/1

 curl -i -X POST http://localhost:8080//dmit2015-1212-jaxrs-demo/webapi/TodoItems \
 -d '{"name":"Finish DMIT2015 Assignment 1","complete":false}' \
 -H 'Content-Type:application/json'

 curl -i -X GET http://localhost:8080/dmit2015-1212-jaxrs-demo/webapi/TodoItems/4

 curl -i -X PUT http://localhost:8080/dmit2015-1212-jaxrs-demo/webapi/TodoItems/4 \
 -d '{"id":4,"name":"Demo DMIT2015 Assignment 1","complete":true}' \
 -H 'Content-Type:application/json'

 curl -i -X GET http://localhost:8080/dmit2015-1212-jaxrs-demo/webapi/TodoItems/4

 curl -i -X DELETE http://localhost:8080/dmit2015-1212-jaxrs-demo/webapi/TodoItems/4

 curl -i -X GET http://localhost:8080/dmit2015-1212-jaxrs-demo/webapi/TodoItems/4

 *
 */

//@ApplicationScoped
// This is a CDI-managed bean that is created only once during the life cycle of the application
@RequestScoped
@Path("TodoItems")	        // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)	// All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)	// All methods returns data that has been converted to JSON format
public class TodoItemResource {
    @Inject
    private JsonWebToken _callerPrincipal;

    @Context
    private UriInfo uriInfo;

    @Inject
    private TodoItemRepository todoItemRepository;

    @RolesAllowed({"Sales","IT"})
    @POST   // POST: /webapi/TodoItems
    public Response postTodoItem(TodoItem newTodoItem) {
        if (newTodoItem == null) {
            throw new BadRequestException();
        }

        String errorMessage = BeanValidator.validateBean(TodoItem.class, newTodoItem);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        try{
            todoItemRepository.create(newTodoItem);
        } catch (Exception ex){
            ex.printStackTrace();
            return Response.serverError().entity(ex.getMessage()).build();
        }

        URI todoItemsUri = uriInfo.getAbsolutePathBuilder().path(newTodoItem.getId().toString()).build();
        return Response.created(todoItemsUri).build();
    }

    @GET    // GET: /webapi/TodoItems/5
    @Path("{id}")
    public Response getTodoItem(@PathParam("id") Long id) {
        Optional<TodoItem> optionalTodoItem = todoItemRepository.findOptional(id);

        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }
        TodoItem existingTodoItem = optionalTodoItem.get();

        return Response.ok(existingTodoItem).build();
    }

    @GET    // GET: /webapi/TodoItems
    public Response getTodoItems() {
        return Response.ok(todoItemRepository.list()).build();
    }

    @PUT    // PUT: /webapi/TodoItems/5
    @Path("{id}")
    public Response updateTodoItem(@PathParam("id") Long id, TodoItem updatedTodoItem) {
        if (!id.equals(updatedTodoItem.getId())) {
            throw new BadRequestException();
        }

        Optional<TodoItem> optionalTodoItem = todoItemRepository.findOptional(id);

        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }

        String errorMessage = BeanValidator.validateBean(TodoItem.class, updatedTodoItem);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        TodoItem existingTodoItem = optionalTodoItem.get();
        existingTodoItem.setName(updatedTodoItem.getName());
        existingTodoItem.setComplete(updatedTodoItem.isComplete());
        existingTodoItem.setVersion(updatedTodoItem.getVersion());

        try {
            todoItemRepository.update(existingTodoItem);
        } catch (OptimisticLockException ex) {
//            throw new BadRequestException("Data has been updated since last fetch request. Do another fetch request to get the new data.");
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("You are trying to update a record that has changed since you fetch it.")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity(e.getMessage())
                    .build();
        }

        return Response.ok(updatedTodoItem).build();
    }

    @DELETE // DELETE: /webapi/TodoItems/5
    @Path("{id}")
    public Response deleteTodoItem(@PathParam("id") Long id) {
        Optional<TodoItem> optionalTodoItem = todoItemRepository.findOptional(id);

        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }

        todoItemRepository.delete(id);

        return Response.noContent().build();
    }

}