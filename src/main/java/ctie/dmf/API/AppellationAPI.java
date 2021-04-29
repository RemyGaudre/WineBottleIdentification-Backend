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

import ctie.dmf.entity.Appellation;
import ctie.dmf.entity.Bottle;

@Path("/appellations/" + API.__VERSION__)
public class AppellationAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getAppellations", summary = "Get all existing appellations", description = "List every appellations")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getAppellations() {
		return Response.ok(Appellation.listAll()).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getAppellationsById", summary = "Get an existing appellation", description = "Return an appellation by giving its id")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getAppellationsById(@PathParam("id") Long id) {
		return Appellation.findByIdOptional(id).map(appellation -> Response.ok(appellation).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@GET
	@Path("{id}/bottles")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getByIdBottles", summary = "Get the list of bottle by a given Appellation", description = "Return the list of bottle by giving an Appellation")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getByIdBottles(@PathParam("id") Long id) {
		Appellation appellation = Appellation.findById(id);
		if (appellation == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		List<Bottle> bottles = appellation.Bottles();
		return Response.ok(bottles).build();
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "createAppellation", summary = "Create a appellation", description = "Create a appellation")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createAppellation(
			@RequestBody(description = "Appellation to create", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Appellation.class))) Appellation appelation) {
		Appellation.persist(appelation);
		if (appelation.isPersistent()) {
			return Response.status(Status.CREATED).entity(appelation).build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "updateAppellation", summary = "Update an appellation", description = "Update an appellation")
	@APIResponse(responseCode = "200", description = "The resource was updated and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response updateAppellation(@PathParam("id") Long id,
			@RequestBody(description = "Appellation updated", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Appellation.class))) Appellation appelation) {
		Appellation original = Appellation.findById(id);
		if (original == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		original.update(appelation);
		Appellation.persist(original);
		if (original.isPersistent()) {
			return Response.status(Status.CREATED).entity(original).build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Operation(operationId = "deleteAppellation", summary = "delete a appelation", description = "delete a appelation")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteAppellation(@PathParam("id") Long id) {
		boolean deleted = Appellation.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
}
