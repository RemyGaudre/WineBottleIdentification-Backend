package ctie.dmf.API;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

import ctie.dmf.entity.Bottle;
import ctie.dmf.entity.Image;
import ctie.dmf.finder.BottleFinder;

@Path("/bottleidentification/" + API.__VERSION__)
public class BottleFinderAPI {

	private BottleFinder finder = new BottleFinder();
	private final static String IMAGES_RECEIVED_DIR = "C:\\Users\\rgaudre\\Downloads\\wine-identifier\\Images_received\\";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getAppellations", summary = "Get all existing appellations", description = "List every appellations")
	@APIResponse(responseCode = "200", description = "The resource was found and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response getAppellations() {
		return Response.ok(Image.findById(new Long(38))).build();
	}
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(operationId = "identify", summary = "Identify a bottle", description = "Identify a bottle")
	@APIResponse(responseCode = "201", description = "The resource was created and content is returned", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	public Response identify(
			@RequestBody(description = "Image file", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(implementation = MultipartFormDataInput.class))) MultipartFormDataInput data)
			throws IOException {

		Map<String, List<InputPart>> uploadForm = data.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("file");

		for (InputPart inputPart : inputParts) {

			try {
				long startTime = System.nanoTime();
				String fileName = getFileName(IMAGES_RECEIVED_DIR);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);

				// constructs upload file path
				fileName = IMAGES_RECEIVED_DIR + fileName;

				writeFile(bytes, fileName);

				System.out.println("New image in Identification : " + fileName);

				Image img = new Image();
				img.setPath(fileName);
				
				Bottle correspondingBottle = finder.identify(img);
				long duration = (System.nanoTime() - startTime);
				System.out.println("[INFO] Object detection took : " + duration/1000000 + "ms");
				
				return Response.ok(correspondingBottle).build();

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
}
