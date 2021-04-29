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
import ctie.dmf.entity.StorageInstruction;

@Path("/storageinstructions/" + API.__VERSION__)
public class StorageInstructionAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getStorageInstruction", summary = "Get all existing StorageInstruction", description = "List every StorageInstruction")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getStorageInstruction() {

		List<StorageInstruction> storageinstruction = StorageInstruction.listAll();

		return Response.ok(storageinstruction).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getStorageInstructionById", summary = "Get an existing StorageInstruction", description = "Return a StorageInstruction given its id")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getStorageInstructionById(@PathParam("id") Long id) {
		return StorageInstruction.findByIdOptional(id)
				.map(storageinstruction -> Response.ok(storageinstruction).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@GET
	@Path("{id}/bottles")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getByIdBottles", summary = "Get the list of bottle by a given StorageInstruction", description = "Return the list of bottle by giving a StorageInstruction")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getByIdBottles(@PathParam("id") Long id) {
		StorageInstruction storageinstruction = StorageInstruction.findById(id);
		if (storageinstruction == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		List<Bottle> bottles = storageinstruction.Bottles();
		return Response.ok(bottles).build();
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "createStorageInstruction", summary = "Create a StorageInstruction", description = "Create a StorageInstruction")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createStorageInstruction(
			@RequestBody(description = "StorageInstruction to create", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = StorageInstruction.class))) StorageInstruction storageinstruction) {
		StorageInstruction.persist(storageinstruction);
		if (storageinstruction.isPersistent()) {
			return Response.status(Status.CREATED).entity(storageinstruction).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "updateStorageInstruction", summary = "Update a StorageInstruction", description = "Update a StorageInstruction")
	@APIResponse(responseCode = "200", description = "The resource was updated and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response updateGrapeVariety(@PathParam("id") Long id,
			@RequestBody(description = "StorageInstruction updated", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = StorageInstruction.class))) StorageInstruction storageinstruction) {
		StorageInstruction original = StorageInstruction.findById(id);
		if (original == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		original.update(storageinstruction);
		StorageInstruction.persist(original);
		if (original.isPersistent()) {
			return Response.status(Status.CREATED).entity(original).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Operation(operationId = "deleteStorageInstruction", summary = "Delete a StorageInstruction", description = "Delete a StorageInstruction")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteStorageInstruction(@PathParam("id") Long id) {
		boolean deleted = StorageInstruction.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
