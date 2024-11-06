package co.vivo.chatservice.controller;

import co.vivo.chatservice.dto.UserPreferencesDto;
import co.vivo.chatservice.model.UserPreferences;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.service.UserPreferencesService;
import co.vivo.chatservice.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@Path("/user-preferences")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserPreferencesController {

    @Inject
    private UserPreferencesService userPreferencesService;

    @Inject
    private AuthService authService;

    /**
     * Get the current preferences for the user.
     */
    @GET
    @Path("/{userId}")
    public Response getUserPreferences(@PathParam("userId") String userId, @HeaderParam("Authorization") String token) {
        UserEntity user = authService.verifyToken(token);
        if (user == null || !user.getUserId().equals(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }

        return userPreferencesService.getUserPreferences(user.getId())
                .map(preferences -> Response.ok(preparePreferenceDto(preferences)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).entity("Preferences not found").build());
    }

    private Optional<UserPreferencesDto> preparePreferenceDto(UserPreferences preferences) {
        UserPreferencesDto dto = new UserPreferencesDto();
        dto.setReadReceipt(preferences.getReadReceipt().name());
        dto.setMuteNotifications(preferences.isMuteNotifications());
        dto.setHideSeenStatus(preferences.isHideSeenStatus());
        return Optional.of(dto);
    }

    /**
     * Update the user preferences.
     */
    @PUT
    @Path("/{userId}")
    public Response updateUserPreferences(@PathParam("userId") String userId, @HeaderParam("Authorization") String token, UserPreferencesDto preferences) {
        UserEntity user = authService.verifyToken(token);
        if (user == null || !user.getUserId().equals(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }

        userPreferencesService.updateUserPreferences(user.getId(), preferences);
        return Response.ok().entity("Preferences updated successfully").build();
    }

    /**
     * Create the user preferences.
     */
    @POST
    @Path("/{userId}")
    public Response createUserPreferences(@PathParam("userId") String userId, @HeaderParam("Authorization") String token, UserPreferencesDto userPreferencesDto) {
        UserEntity user = authService.verifyToken(token);
        if (user == null || !user.getUserId().equals(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }

        userPreferencesService.createUserPreferences(user, userPreferencesDto);
        return Response.ok().entity("Preferences updated successfully").build();
    }
}
