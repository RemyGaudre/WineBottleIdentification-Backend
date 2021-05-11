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
import ctie.dmf.entity.WineStyle;

@Path("/admin/" + API.__VERSION__ + "/winestyles/")
public class WineStyleAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getWineStyle", summary = "Get all existing WineStyle", description = "List every WineStyle")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getWineStyle() {

		List<WineStyle> winestyle = WineStyle.listAll();

		return Response.ok(winestyle).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getWineStyleById", summary = "Get an existing WineStyle", description = "Return a WineStyle given its id")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getWineStyleById(@PathParam("id") Long id) {
		return WineStyle.findByIdOptional(id).map(winestyle -> Response.ok(winestyle).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "createWineStyle", summary = "Create a WineStyle", description = "Create a WineStyle")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createWineStyle(
			@RequestBody(description = "WineStyle to create", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = WineStyle.class))) WineStyle winestyle) {
		WineStyle.persist(winestyle);
		if (winestyle.isPersistent()) {
			return Response.status(Status.CREATED).entity(winestyle).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "updateWineStyle", summary = "Update a WineStyle", description = "Update a WineStyle")
	@APIResponse(responseCode = "200", description = "The resource was updated and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response updateGrapeVariety(@PathParam("id") Long id,
			@RequestBody(description = "WineStyle updated", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = WineStyle.class))) WineStyle winestyle) {
		WineStyle original = WineStyle.findById(id);
		if (original == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		original.update(winestyle);
		WineStyle.persist(original);
		if (original.isPersistent()) {
			return Response.status(Status.CREATED).entity(original).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Operation(operationId = "deleteWineStyle", summary = "Delete a WineStyle", description = "Delete a WineStyle")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteWineStyle(@PathParam("id") Long id) {
		boolean deleted = WineStyle.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
