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
import ctie.dmf.entity.Producer;

@Path("/producers/" + API.__VERSION__)
public class ProducerAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getProducer", summary = "Get all existing Producer", description = "List every Producer")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getProducer() {

		List<Producer> producer = Producer.listAll();

		return Response.ok(producer).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getProducerById", summary = "Get an existing Producer", description = "Return a Producer given its id")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getProducerById(@PathParam("id") Long id) {
		return Producer.findByIdOptional(id).map(producer -> Response.ok(producer).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@GET
	@Path("{id}/bottles")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getByIdBottles", summary = "Get the list of bottle by a given Producer", description = "Return the list of bottle by giving a Producer")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getByIdBottles(@PathParam("id") Long id) {
		Producer producer = Producer.findById(id);
		if (producer == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		List<Bottle> bottles = producer.Bottles();
		return Response.ok(bottles).build();
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "createProducer", summary = "Create a Producer", description = "Create a Producer")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createProducer(
			@RequestBody(description = "Producer to create", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Producer.class))) Producer producer) {
		Producer.persist(producer);
		if (producer.isPersistent()) {
			return Response.status(Status.CREATED).entity(producer).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "updateProducer", summary = "Update a Producer", description = "Update a Producer")
	@APIResponse(responseCode = "200", description = "The resource was updated and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response updateGrapeVariety(@PathParam("id") Long id,
			@RequestBody(description = "Producer updated", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Producer.class))) Producer producer) {
		Producer original = Producer.findById(id);
		if (original == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		original.update(producer);
		Producer.persist(original);
		if (original.isPersistent()) {
			return Response.status(Status.CREATED).entity(original).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Operation(operationId = "deleteProducer", summary = "Delete a Producer", description = "Delete a Producer")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteProducer(@PathParam("id") Long id) {
		boolean deleted = Producer.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
