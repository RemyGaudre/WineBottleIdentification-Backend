package ctie.dmf.API;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import ctie.dmf.entity.Bottle;
import ctie.dmf.entity.Image;

@Path("")
public class ImageAPI {

	@Inject
	@ConfigProperty(name = "images_dir")
	protected String IMAGES_DIR;

	@Inject
	@ConfigProperty(name = "images_saved_folder")
	protected String IMAGES_SAVED_FOLDER;

	@GET
	@Path("/admin/" + API.__VERSION__ + "/images/")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getImage", summary = "Get all existing Image", description = "List every Image")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getImage() {

		List<Image> images = Image.listAll();

		return Response.ok(images).build();
	}

	@GET
	@Path("/admin/" + API.__VERSION__ + "/images/" + "{id}/bottle")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getBottleOfAnImage", summary = "Get the linked bottle of an Image", description = "Get the bottle of an Image")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getBottleOfAnImage(@PathParam("id") Long id) {

		Image image = Image.findById(id);
		if (image != null && image.getBottle() != null) {
			System.out.println("Bottle linked to image : " + image.getBottle().getId().toString());
			return Response.ok(image.getBottle()).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("/bottleidentification/image/" + API.__VERSION__ + "/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getAnImage", summary = "Get an Image", description = "Get an Image")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM))
	public Response getAnImage(@PathParam("id") Long id) {

		Image image = Image.findById(id);
		if (image != null) {
			File file = new File(IMAGES_DIR + image.getPath());
			if (file.exists()) {
				ResponseBuilder response = Response.ok((Object) file);
				response.header("Content-Disposition", "attachment; filename=image.jpg");
				return response.build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("/admin/" + API.__VERSION__ + "/images/" + "{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getAnImage", summary = "Get an Image", description = "Get an Image")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM))
	public Response getAnImage1(@PathParam("id") Long id) {

		Image image = Image.findById(id);
		if (image != null) {
			return Response.ok(image).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@POST
	@Path("/admin/" + API.__VERSION__ + "/images/")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(operationId = "createImage", summary = "Create an Image", description = "Create an Image")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createImage(
			@RequestBody(description = "Image file and bottle pkey", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(implementation = MultipartFormDataInput.class))) MultipartFormDataInput data)
			throws IOException {

		Map<String, List<InputPart>> uploadForm = data.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("file");
		try {
			String fileName = getFileName(IMAGES_DIR + IMAGES_SAVED_FOLDER);

			// convert the uploaded file to inputstream
			InputStream inputStream = inputParts.get(0).getBody(InputStream.class, null);

			byte[] bytes = IOUtils.toByteArray(inputStream);

			// constructs upload file path
			writeFile(bytes, IMAGES_DIR + IMAGES_SAVED_FOLDER + fileName);

			System.out.println("New image saved at: " + IMAGES_DIR + IMAGES_SAVED_FOLDER + fileName);

			Long bottleid = uploadForm.get("bottleId").get(0).getBody(Long.class,null);
			Image img;
			if(bottleid != null) {
				Bottle bottle = Bottle.findById(bottleid);
				if(bottle != null) {
					img = new Image(IMAGES_SAVED_FOLDER + fileName, bottle);
				}else {
					img = new Image(IMAGES_SAVED_FOLDER + fileName, null);
				}
			}else {
				img = new Image(IMAGES_SAVED_FOLDER + fileName, null);
			}
			Image.persist(img);
			if (img.isPersistent()) {
				return Response.status(Status.CREATED).entity(img).build();
			}
			return Response.status(Response.Status.CONFLICT).build();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	private String getFileName(String Image_dir) {

		try {
			File f = new File(Image_dir);

			// Populates the array with names of files and directories
			String[] pathnames = f.list();
			return "Image_" + pathnames.length + ".jpg";
		} catch (Exception e) {
			return "Image_0.jpg";
		}
	}

	private void writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();
	}

	@PUT
	@Path("/admin/" + API.__VERSION__ + "/images/")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(operationId = "updateImage", summary = "Update an image", description = "Update an image")
	@APIResponse(responseCode = "200", description = "The resource was updated and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response updateImage(@PathParam("id") Long id,
			@RequestBody(description = "Image updated", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Image.class))) Image image) {
		Image original = Image.findById(image.getId());
		if (original == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		original.update(image);
		original.persistAndFlush();
		if (original.isPersistent()) {
			return Response.status(Status.CREATED).entity(original).build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}

	@DELETE
	@Path("/admin/" + API.__VERSION__ + "/images/" + "{id}")
	@Transactional
	@Operation(operationId = "deleteImage", summary = "Delete an Image", description = "Delete an Image")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteImage(@PathParam("id") Long id) {
		Image img = Image.findById(id);
		if (img != null) {
			if (img.getBottle() != null)
				img.getBottle().removeImage(img);
			img.delete();
			if (!(img.isPersistent())) {
				return Response.noContent().build();
			} else {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}
