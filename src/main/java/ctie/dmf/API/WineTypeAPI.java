package ctie.dmf.API;

import java.net.URI;
import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import ctie.dmf.entity.Bottle;
import ctie.dmf.entity.WineType;

@Path("/winetypes/" + API.__VERSION__)
public class WineTypeAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getWineTypes", summary = "Get all existing WineTypes", description = "List every WineTypes")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getWineTypes() {

		List<WineType> winetypes = WineType.listAll();

		return Response.ok(winetypes).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getWineTypesById", summary = "Get an existing WineType", description = "Return a WineType given its id")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getWineTypesById(@PathParam("id") Long id) {
		return WineType.findByIdOptional(id).map(winetype -> Response.ok(winetype).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@GET
	@Path("{id}/bottles")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getByIdBottles", summary = "Get the list of bottle by a given WineType", description = "Return the list of bottle by giving a WineType")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getByIdBottles(@PathParam("id") Long id) {
		WineType winetype = WineType.findById(id);
		if (winetype == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		List<Bottle> bottles = winetype.Bottles();
		return Response.ok(bottles).build();
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "createWineType", summary = "Create a WineType", description = "Create a WineType")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createWineType(
			@RequestBody(description = "WineType to create", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = WineType.class))) WineType winetype) {
		WineType.persist(winetype);
		if (winetype.isPersistent()) {
			return Response.status(Status.CREATED).entity(winetype).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "updateWineType", summary = "Update a WineType", description = "Update a WineType")
	@APIResponse(responseCode = "200", description = "The resource was updated and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response updateWineType(@PathParam("id") Long id,
			@RequestBody(description = "WineType updated", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = WineType.class))) WineType winetype) {
		WineType original = WineType.findById(id);
		if (original == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		original.update(winetype);
		WineType.persist(original);
		if (original.isPersistent()) {
			return Response.status(Status.CREATED).entity(original).build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Operation(operationId = "deleteWineType", summary = "Delete a WineType", description = "Delete a WineType")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteWineType(@PathParam("id") Long id) {
		boolean deleted = WineType.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
