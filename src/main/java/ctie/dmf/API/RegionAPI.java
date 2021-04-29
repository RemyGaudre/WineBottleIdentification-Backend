package ctie.dmf.API;

import java.net.URI;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import ctie.dmf.entity.Producer;
import ctie.dmf.entity.Region;
import ctie.dmf.entity.StorageInstruction;

@Path("/regions/" + API.__VERSION__)
public class RegionAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getRegions", summary = "Get all existing regions", description = "List every regions")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getRegions() {
		return Response.ok(Region.listAll()).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getRegionById", summary = "Get an existing region", description = "Return a region by giving its id")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getRegionById(@PathParam("id") Long id) {
		return Region.findByIdOptional(id).map(region -> Response.ok(region).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@GET
	@Path("{id}/producers")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getByIdProducers", summary = "Get the list of Producer by a given StorageInstruction", description = "Return the list of Producer by giving a StorageInstruction")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getByIdProducers(@PathParam("id") Long id) {
		Region region = Region.findById(id);
		if (region == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		List<Producer> producers = region.Producers();
		return Response.ok(producers).build();
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "createRegion", summary = "Create a region", description = "Create a region")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createRegion(
			@RequestBody(description = "Region to create", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Region.class))) Region region) {
		Region.persist(region);
		if (region.isPersistent()) {
			return Response.status(Status.CREATED).entity(region).build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Operation(operationId = "deleteRegion", summary = "delete a region", description = "delete a region")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteRegion(@PathParam("id") Long id) {
		boolean deleted = Region.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
}
