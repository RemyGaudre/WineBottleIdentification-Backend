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
import ctie.dmf.entity.GrapeVariety;

@Path("/grapevarieties/" + API.__VERSION__)
public class GrapeVarietyAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getGrapeVariety", summary = "Get all existing GrapeVariety", description = "List every GrapeVariety")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getGrapeVariety() {

		List<GrapeVariety> grapevariety = GrapeVariety.listAll();

		return Response.ok(grapevariety).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getGrapeVarietyById", summary = "Get an existing GrapeVariety", description = "Return a GrapeVariety given its id")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getGrapeVarietyById(@PathParam("id") Long id) {
		return GrapeVariety.findByIdOptional(id).map(grapevariety -> Response.ok(grapevariety).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@GET
	@Path("{id}/bottles")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getByIdBottles", summary = "Get the list of bottle by a given GrapeVariety", description = "Return the list of bottle by giving a GrapeVariety")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getByIdBottles(@PathParam("id") Long id) {
		GrapeVariety grapevariety = GrapeVariety.findById(id);
		if (grapevariety == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		List<Bottle> bottles = grapevariety.Bottles();
		return Response.ok(bottles).build();
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "createGrapeVariety", summary = "Create a GrapeVariety", description = "Create a GrapeVariety")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createGrapeVariety(
			@RequestBody(description = "GrapeVariety to create", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GrapeVariety.class))) GrapeVariety grapevariety) {
		GrapeVariety.persist(grapevariety);
		if (grapevariety.isPersistent()) {
			return Response.status(Status.CREATED).entity(grapevariety).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "updateGrapeVariety", summary = "Update a GrapeVariety", description = "Update a GrapeVariety")
	@APIResponse(responseCode = "200", description = "The resource was updated and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response updateGrapeVariety(@PathParam("id") Long id,
			@RequestBody(description = "GrapeVariety updated", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GrapeVariety.class))) GrapeVariety grapevariety) {
		GrapeVariety original = GrapeVariety.findById(id);
		if (original == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		original.update(grapevariety);
		GrapeVariety.persist(original);
		if (original.isPersistent()) {
			return Response.status(Status.CREATED).entity(original).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Operation(operationId = "deleteGrapeVariety", summary = "Delete a GrapeVariety", description = "Delete a GrapeVariety")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteGrapeVariety(@PathParam("id") Long id) {
		boolean deleted = GrapeVariety.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
