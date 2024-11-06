package co.vivo.chatservice.controller;

import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.multipart.MultipartFile;
import co.vivo.chatservice.service.AuthService;
import co.vivo.chatservice.service.MediaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Path("/media")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class MediaController {

    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Inject
    private AuthService authService;

    @Inject
    private MediaService mediaService;

    @POST
    @Path("/upload")
    public Response uploadMedia(@HeaderParam("Authorization") String token, @MultipartForm MultipartFile file) {
        try {
            UserEntity userEntity = authService.verifyToken(token);
            if (userEntity == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
            }

            // Upload media and generate pre-signed URL
            String presignedUrl = mediaService.uploadMedia(file.getBytes(), file.getFileName());

            return Response.ok(presignedUrl).build();
        } catch (IOException e) {
            logger.error("Error while uploading media", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error uploading media").build();
        }
    }
}
