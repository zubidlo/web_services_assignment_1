package exceptions;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import models.ErrorMessage;

/**
 *
 * @author martin
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class BookNotFoundExceptionMapper implements ExceptionMapper<BookNotFoundException>{

    @Override
    public Response toResponse(BookNotFoundException e) {
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage(), 404);
        return Response.status(Status.NOT_FOUND)
                .entity(errorMessage)
                .build();
    }

}
