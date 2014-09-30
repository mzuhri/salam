package at.ac.tuwien.dsg.smartsociety.demo.rest.rs;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import at.ac.tuwien.dsg.smartsociety.demo.rest.resource.Task;
import at.ac.tuwien.dsg.smartsociety.demo.rest.services.TaskService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path( "/task" ) 
@Api( value = "/task", description = "Manage task" )
public class TaskRestService {
	@Inject private TaskService taskService;
	
	@Produces( { MediaType.APPLICATION_JSON } )
	@GET
	@ApiOperation( value = "List all tasks", notes = "List all tasks using paging", response = Task.class, responseContainer = "List")
	public Collection< Task > getTask(  @ApiParam( value = "Page to fetch", required = true ) @QueryParam( "page") @DefaultValue( "1" ) final int page ) {
		return taskService.getTasks( page, 5 );
	}

	@Produces( { MediaType.APPLICATION_JSON } )
	@Path( "/{id}" )
	@GET
	@ApiOperation( value = "Find task by id", notes = "Find task by id", response = Task.class )
	@ApiResponses( {
	    @ApiResponse( code = 404, message = "Task with such id doesn't exists" )			 
	} )
	public Task getTask( @ApiParam( value = "Id to lookup for", required = true ) @PathParam( "id" ) final Integer id ) {
		return taskService.getById(id);
	}

	@Produces( { MediaType.APPLICATION_JSON  } )
	@POST
	@ApiOperation( value = "Create new task", notes = "Create new task" )
	@ApiResponses( {
	    @ApiResponse( code = 201, message = "Task created successfully" ),
	    @ApiResponse( code = 409, message = "Task with such id already exists" )
	} )
	public Response addPerson( @Context final UriInfo uriInfo,
            @ApiParam( value = "Id", required = true ) @FormParam( "id" ) final Integer id, 
			@ApiParam( value = "Title", required = true ) @FormParam( "title" ) final String title, 
			@ApiParam( value = "Description", required = true ) @FormParam( "description" ) final String description ) {
		
		taskService.addTask(id, title, description);
		return Response.created( uriInfo.getRequestUriBuilder().path(id.toString()).build() ).build();
	}
	
	@Produces( { MediaType.APPLICATION_JSON  } )
	@Path( "/{id}" )
	@PUT
	@ApiOperation( value = "Update existing task", notes = "Update existing task", response = Task.class )
	@ApiResponses( {
	    @ApiResponse( code = 404, message = "Task with such id doesn't exists" )			 
	} )
	public Task updatePerson(			
            @ApiParam( value = "Id", required = true ) @FormParam( "id" ) final Integer id, 
            @ApiParam( value = "Title", required = false ) @FormParam( "title" ) final String title, 
            @ApiParam( value = "Description", required = false ) @FormParam( "description" ) final String description ) {
		
		final Task task = taskService.getById(id);
		
		if( title != null ) {
		    task.setTitle(title);
		}
		
        if( description != null ) {
            task.setDescription(description);
        }

        return task; 				
	}
	
	@Path( "/{id}" )
	@DELETE
	@ApiOperation( value = "Delete existing task", notes = "Delete existing task", response = Task.class )
	@ApiResponses( {
	    @ApiResponse( code = 404, message = "Task with such id doesn't exists" )			 
	} )
	public Response deleteTask( @ApiParam( value = "Id", required = true ) @PathParam( "id" ) final Integer id ) {
		taskService.removeTask(id);
		return Response.ok().build();
	}

    @Path( "/{id}/assign" )
    @POST
    @ApiOperation( value = "Delete existing task", notes = "Delete existing task", response = Task.class )
    @ApiResponses( {
        @ApiResponse( code = 404, message = "Task with such id doesn't exists" )             
    } )
    public Response assignTask( 
            @ApiParam( value = "Id", required = true ) @PathParam( "id" ) final Integer id,
            @ApiParam( value = "E-Mail of the assigned peer", required = true ) @FormParam( "email" ) final String email
            ) {
        return Response.ok().build();
    }
}
