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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import ctie.dmf.entity.Bottle;
import ctie.dmf.entity.Image;
import ctie.dmf.entity.Producer;

@Path("/admin/" + API.__VERSION__ + "/bottles/")
public class BottlesAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getBottles", summary = "Get all existing bottles", description = "List every bottle")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getBottles() {
		return Response.ok(Bottle.listAll()).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getById", summary = "Get an existing bottle", description = "Return a bottle given its id")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getById(@PathParam("id") Long id) {
		return Bottle.findByIdOptional(id).map(bottle -> Response.ok(bottle).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@GET
	@Path("vintage/{vintage}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getByVintage", summary = "Get all bottles by vintage", description = "Return a list of bottle of a particular vintage")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getByVintage(@PathParam("vintage") int vintage) {
		List<Bottle> bottles = Bottle.list("SELECT b from Bottle b WHERE b.vintage=?1 ORDER BY id DESC", vintage);
		return Response.ok(bottles).build();
	}

	@GET
	@Path("alcohol/{alcohol}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getByAlcohol", summary = "Get all bottles by percentage of alcohol", description = "Return a list of bottle of a particular percentage of alcohol")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getByAlcohol(@PathParam("alcohol") double alcohol) {
		List<Bottle> bottles = Bottle.list("alcohol", alcohol);
		return Response.ok(bottles).build();
	}

	@GET
	@Path("producer/{producer_id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getByProducer", summary = "Get all bottles by producer", description = "Return a list of bottle of a particular producer")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getByProducer(@PathParam("producer_id") Long producer) {
		List<Bottle> bottles = Bottle.list(
				"SELECT b from Bottle b WHERE b.producer=(Select p From Producer p where id=?1) ORDER BY id DESC",
				producer);
		return Response.ok(bottles).build();
	}

	@GET
	@Path("{id}/images")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getListOfImagesOfABottle", summary = "Get all images of a bottle", description = "Return a list of images of a particular bottle")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getListOfImagesOfABottle(@PathParam("id") Long id) {
		List<Image> images = Image
				.list("SELECT i from Image i WHERE i.bottle=(Select b From Bottle b where id=?1) ORDER BY id DESC", id);
		return Response.ok(images).build();
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "createBottle", summary = "Create a bottle", description = "Create a bottle")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createBottle(
			@RequestBody(description = "Bottle to create", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Bottle.class))) Bottle bottle) {
		Bottle.persist(bottle);
		if (bottle.isPersistent()) {
			return Response.status(Status.CREATED).entity(bottle).build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}

	@PUT
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "updateBottle", summary = "Update a bottle", description = "Update a bottle")
	@APIResponse(responseCode = "200", description = "The resource was updated and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response updateBottle(
			@RequestBody(description = "Bottle updated", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Bottle.class))) Bottle bottle) {
		Bottle original = Bottle.findById(bottle.getId());
		System.out.println(original.toString());
		if (original == null) {return Response.status(Response.Status.NOT_FOUND).build();}
		original.update(bottle);
		Bottle.persist(original);
		if (original.isPersistent()) {
			return Response.status(Status.CREATED).entity(original).build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Operation(operationId = "deleteBottle", summary = "delete a bottle", description = "delete a bottle")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteBottle(@PathParam("id") Long id) {
		boolean deleted = Bottle.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
}