package ctie.dmf.API;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import ctie.dmf.entity.Image;

@Path("/images/" + API.__VERSION__)
public class ImageAPI {

	private final static String IMAGES_DIR = "C:\\Users\\rgaudre\\Downloads\\wine-identifier\\Images_saved\\";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getImage", summary = "Get all existing Image", description = "List every Image")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getImage() {

		List<Image> images = Image.listAll();

		return Response.ok(images).build();
	}
	
	@GET
	@Path("/{id}/bottle")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getBottleOfAnImage", summary = "Get the linked bottle of an Image", description = "Get the bottle of an Image")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getBottleOfAnImage(@PathParam("id") Long id) {

		Image image = Image.findById(id);

		return Response.ok(image.getBottle()).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getAnImage", summary = "Get an Image", description = "Get an Image")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getAnImage(@PathParam("id") Long id) {

		Image image = Image.findById(id);

		return Response.ok(image).build();
	}


	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(operationId = "createImage", summary = "Create an Image", description = "Create an Image")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response createImage(
			@RequestBody(description = "Image file", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(implementation = MultipartFormDataInput.class))) MultipartFormDataInput data)
			throws IOException {

		Map<String, List<InputPart>> uploadForm = data.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("file");

		for (InputPart inputPart : inputParts) {

			try {
				String fileName = getFileName(IMAGES_DIR);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);

				// constructs upload file path
				fileName = IMAGES_DIR + fileName;

				writeFile(bytes, fileName);

				System.out.println("New file: " + fileName);

				Image img = new Image();
				img.setPath(fileName);
				Image.persist(img);
				if (img.isPersistent()) {
					return Response.status(Status.CREATED).entity(img).build();
				}
				return Response.status(Response.Status.CONFLICT).build();

			} catch (IOException e) {
				e.printStackTrace();
			}
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
		System.out.println(filename);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();
	}

	@PUT
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
		Image.persist(original);
		if (original.isPersistent()) {
			return Response.status(Status.CREATED).entity(original).build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Operation(operationId = "deleteImage", summary = "Delete an Image", description = "Delete an Image")
	@APIResponse(responseCode = "200", description = "The resource was deleted and some content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response deleteImage(@PathParam("id") Long id) {
		boolean deleted = Image.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
