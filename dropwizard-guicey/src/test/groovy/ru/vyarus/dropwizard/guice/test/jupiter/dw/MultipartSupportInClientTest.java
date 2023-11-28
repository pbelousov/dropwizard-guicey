package ru.vyarus.dropwizard.guice.test.jupiter.dw;

import com.google.common.base.Preconditions;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spockframework.util.IoUtil;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.test.ClientSupport;
import ru.vyarus.dropwizard.guice.test.jupiter.TestDropwizardApp;

import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author Vyacheslav Rusakov
 * @since 18.11.2023
 */
@TestDropwizardApp(MultipartSupportInClientTest.App.class)
public class MultipartSupportInClientTest {

    @Test
    void testMultipartClientSupport(@TempDir java.nio.file.Path temp, ClientSupport client) throws Exception {
        java.nio.file.Path file = temp.resolve("sample.txt");
        Files.createFile(file);
        Files.writeString(file, "sample content");

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
                file.toFile(),
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(fileDataBodyPart);


        Response response = client.targetRest("form/handle").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        Assertions.assertEquals(200, response.getStatus());
    }

    public static class App extends Application<Configuration> {

        @Override
        public void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(new MultiPartBundle());
            bootstrap.addBundle(GuiceBundle.builder()
                    .extensions(MultipartRest.class)
                    .build());
        }

        @Override
        public void run(Configuration configuration, Environment environment) throws Exception {

        }
    }

    @Path("form/")
    @Produces(MediaType.APPLICATION_JSON)
    public static class MultipartRest {


        @POST
        @Path("/handle")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        public Response convert(@NotNull @FormDataParam("file") final InputStream uploadedInputStream,
                                @NotNull @FormDataParam("file") final FormDataContentDisposition fileDetail)
                throws Exception {
            final String text = IoUtil.getText(uploadedInputStream);
            System.out.println("TEXT: " + text);
            Preconditions.checkState(!text.isEmpty());

            System.out.println("NAME: " + fileDetail.getFileName());
            Preconditions.checkNotNull(fileDetail.getFileName());
            return Response.status(200).build();
        }
    }
}
