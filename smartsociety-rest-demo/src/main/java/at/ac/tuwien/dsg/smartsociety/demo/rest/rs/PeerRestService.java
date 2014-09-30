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

import at.ac.tuwien.dsg.smartsociety.demo.rest.resource.Peer;
import at.ac.tuwien.dsg.smartsociety.demo.rest.services.PeerService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path( "/peer" ) 
@Api( value = "/peer", description = "Manage peer" )
public class PeerRestService {
	@Inject private PeerService peerService;
	
	@Produces( { MediaType.APPLICATION_JSON } )
	@GET
	@ApiOperation( value = "List all peers", notes = "List all peers using paging", response = Peer.class, responseContainer = "List")
	public Collection< Peer > getPeer(  @ApiParam( value = "Page to fetch", required = true ) @QueryParam( "page") @DefaultValue( "1" ) final int page ) {
		return peerService.getPeers( page, 5 );
	}

	@Produces( { MediaType.APPLICATION_JSON } )
	@Path( "/{email}" )
	@GET
	@ApiOperation( value = "Find peer by e-mail", notes = "Find peer by e-mail", response = Peer.class )
	@ApiResponses( {
	    @ApiResponse( code = 404, message = "Peer with such e-mail doesn't exists" )			 
	} )
	public Peer getPeer( @ApiParam( value = "E-Mail address to lookup for", required = true ) @PathParam( "email" ) final String email ) {
		return peerService.getByEmail( email );
	}

	@Produces( { MediaType.APPLICATION_JSON  } )
	@POST
	@ApiOperation( value = "Create new peer", notes = "Create new peer" )
	@ApiResponses( {
	    @ApiResponse( code = 201, message = "Peer created successfully" ),
	    @ApiResponse( code = 409, message = "Peer with such e-mail already exists" )
	} )
	public Response addPerson( @Context final UriInfo uriInfo,
			@ApiParam( value = "E-Mail", required = true ) @FormParam( "email" ) final String email, 
			@ApiParam( value = "Name", required = true ) @FormParam( "name" ) final String name ) {
		
		peerService.addPeer(email, name);
		return Response.created( uriInfo.getRequestUriBuilder().path( email ).build() ).build();
	}
	
	@Produces( { MediaType.APPLICATION_JSON  } )
	@Path( "/{email}" )
	@PUT
	@ApiOperation( value = "Update existing peer", notes = "Update existing peer", response = Peer.class )
	@ApiResponses( {
	    @ApiResponse( code = 404, message = "Peer with such e-mail doesn't exists" )			 
	} )
	public Peer updatePerson(			
			@ApiParam( value = "E-Mail", required = true ) @PathParam( "email" ) final String email, 
			@ApiParam( value = "Name", required = false ) @FormParam( "name" ) final String name ) {
		
		final Peer peer = peerService.getByEmail( email );
		
		if( name != null ) {
		    peer.setName( name );
		}
		
		return peer; 				
	}
	
	@Path( "/{email}" )
	@DELETE
	@ApiOperation( value = "Delete existing peer", notes = "Delete existing peer", response = Peer.class )
	@ApiResponses( {
	    @ApiResponse( code = 404, message = "Peer with such e-mail doesn't exists" )			 
	} )
	public Response deletePeer( @ApiParam( value = "E-Mail", required = true ) @PathParam( "email" ) final String email ) {
		peerService.removePeer( email );
		return Response.ok().build();
	}

}
