package ctie.dmf.API;

import java.util.List;

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


import ctie.dmf.entity.Country;
import ctie.dmf.entity.Region;

@Path("/countries/" + API.__VERSION__)
public class CountryAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getCountries", summary = "Get all existing countries", description = "List every countries")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getCountries() {
		return Response.ok(Country.listAll()).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getCountryById", summary = "Get an existing country", description = "Return a country by giving its id")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getCountryById(@PathParam("id") Long id) {
		return Country.findByIdOptional(id).map(country -> Response.ok(country).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "createCountry", summary = "Create a country", description = "Create a country")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createCountry(
			@RequestBody(description = "Country to create", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Country.class))) Country country) {
		Country.persist(country);
		if (country.isPersistent()) {
			return Response.status(Status.CREATED).entity(country).build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Operation(operationId = "deleteCountry", summary = "delete a country", description = "delete a country")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteCountry(@PathParam("id") Long id) {
		boolean deleted = Country.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@GET
	@Path("{id}/regions")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getRegionsByIdCountry", summary = "Get the list of regions in a given country", description = "Return the list of regions in a Country")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getRegionsByIdCountry(@PathParam("id") Long id) {
		Country country = Country.findById(id);
		if (country == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		List<Region> regions = country.Regions();
		return Response.ok(regions).build();
	}
}
