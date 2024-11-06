package co.vivo.chatservice.controller;

import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.repository.MessageRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/messages")
public class MessageController {

    @Inject
    MessageRepository messageRepository;

    @GET
    @Path("/history")
    public Response getMessageHistory(@QueryParam("limit") int limit) {
        List<MessageEntity> messages = messageRepository.getMessageHistory(limit);
        return Response.ok(messages).build();
    }

    @PUT
    @Path("/edit/{id}")
    public Response editMessage(@PathParam("id") Long id, @FormParam("content") String newContent) {
        messageRepository.updateMessage(id, newContent);
        return Response.ok("Message updated").build();
    }
}