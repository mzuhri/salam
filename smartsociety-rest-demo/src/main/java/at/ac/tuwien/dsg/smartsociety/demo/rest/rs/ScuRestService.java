package at.ac.tuwien.dsg.smartsociety.demo.rest.rs;

import java.util.ArrayList;
import java.util.Collection;

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

import at.ac.tuwien.dsg.smartsociety.demo.rest.resource.Peer;
import at.ac.tuwien.dsg.smartsociety.demo.rest.resource.Scu;
import at.ac.tuwien.dsg.smartsociety.demo.rest.resource.Task;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path( "/scu" ) 
@Api( value = "/scu", description = "Manage SCU" )
public class ScuRestService {
	
	@Produces( { MediaType.APPLICATION_JSON } )
	@GET
	@ApiOperation( value = "List all SCUs", notes = "List all SCUs using paging", response = Scu.class, responseContainer = "List")
	public Collection<Scu> getScus(  @ApiParam( value = "Page to fetch", required = true ) @QueryParam( "page") @DefaultValue( "1" ) final int page ) {
		return new ArrayList<Scu>();
	}

	@Produces( { MediaType.APPLICATION_JSON  } )
	@POST
	@ApiOperation( value = "Create new SCU", notes = "Create new SCU" )
	@ApiResponses( {
	    @ApiResponse( code = 201, message = "SCU created successfully" )
	} )
	public Response createSCU( @Context final UriInfo uriInfo,
            @ApiParam( value = "Id", required = true ) @FormParam( "id" ) final Integer id, 
			@ApiParam( value = "Members' email addresses", required = true ) @FormParam( "members" ) final String emails) {
		return Response.created( uriInfo.getRequestUriBuilder().path(id.toString()).build() ).build();
	}
	
    @Path( "/{id}" )
    @DELETE
    @ApiOperation( value = "Dissolve SCU", notes = "Delete existing SCU", response = Task.class )
    @ApiResponses( {
        @ApiResponse( code = 404, message = "SCU with such id doesn't exists" )             
    } )
    public Response deleteScu( @ApiParam( value = "Id", required = true ) @PathParam( "id" ) final Integer id ) {
        return Response.ok().build();
    }

    @Path( "/{id}/run" )
    @POST
    @ApiOperation( value = "Invoke SCU", notes = "Initiate execution of an SCU", response = Task.class )
    @ApiResponses( {
        @ApiResponse( code = 404, message = "SCU with such id doesn't exists" )             
    } )
    public Response runScu( @ApiParam( value = "Id", required = true ) @PathParam( "id" ) final Integer id ) {
        return Response.ok().build();
    }

    @Produces( { MediaType.APPLICATION_JSON  } )
	@Path( "/{id}/members" )
	@GET
	@ApiOperation( value = "List members of SCU", notes = "List members of SCU", response = Peer.class, responseContainer = "List")
	@ApiResponses( {
	    @ApiResponse( code = 404, message = "SCU with such id doesn't exists" )			 
	} )
	public Collection<Peer> listMembers(			
            @ApiParam( value = "Id", required = true ) @FormParam( "id" ) final Integer id) {
        return new ArrayList<Peer>();
	}
	
    @Produces( { MediaType.APPLICATION_JSON  } )
    @Path( "/{id}/members" )
    @PUT
    @ApiOperation( value = "Extend SCU", notes = "Add members of SCU")
    @ApiResponses( {
        @ApiResponse( code = 404, message = "SCU with such id doesn't exists" )          
    } )
    public Response addMembers(            
            @ApiParam( value = "Id", required = true ) @FormParam( "id" ) final Integer id,
            @ApiParam( value = "Members' email addresses", required = true ) @FormParam( "members" ) final String emails) {
        return Response.ok().build();
    }

    @Produces( { MediaType.APPLICATION_JSON  } )
    @Path( "/{id}/members" )
    @DELETE
    @ApiOperation( value = "Reduce SCU", notes = "Remove members of SCU")
    @ApiResponses( {
        @ApiResponse( code = 404, message = "SCU with such id doesn't exists" )          
    } )
    public Response deleteMembers(            
            @ApiParam( value = "Id", required = true ) @FormParam( "id" ) final Integer id,
            @ApiParam( value = "Members' email addresses", required = true ) @FormParam( "members" ) final String emails) {
        return Response.ok().build();
    }

}
