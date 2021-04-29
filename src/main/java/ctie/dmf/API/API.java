package ctie.dmf.API;

import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(tags = {
		@Tag(name = "Wine bottle base", description = "This API Manage every bottles") }, info = @Info(title = "Bottles API", version = "1.0.1", contact = @Contact(name = "Remy Gaudre", url = "https://www.sword-group.com/", email = "remy.gaudre@sword-group.com"), license = @License(name = "Apache 2.0", url = "http://www.apache.org/licences/LICENSE-2.0.html")))
public class API extends Application {

	public static final String __VERSION__= "v1";
	
}
