package resources;

import exceptions.BookNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import models.Book;
import models.BookService;

/**
 *
 * @author martin
 */
@Path("books")
public class BookResource {
    
    private final BookService bookService = new BookService();
    
    /**
     * Resets the books to initial state.
     * @return initial list of books
     */
    @GET
    @Path("/reset")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> reset() {
        BookService.resetBooks();
        return bookService.getBooks();
    }
    
    /**
     * Gets all the books.
     * all = /booksREST/webapi/books
     * by genre = /booksREST/webapi/books?genre=space opera
     * by author = /booksREST/webapi/books?author=Clark
     * page = /booksREST/webapi/books?start=3&size=5
     * @param genre
     * @param search
     * @param start
     * @param size
     * @return 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> getAllBooks(
            @QueryParam("genre") String genre,
            @QueryParam("search") String search,
            @QueryParam("start") int start,
            @QueryParam("size") int size) {
        if(genre != null) return bookService.booksByGenre(genre.trim());
        if(search != null) return bookService.booksBySearch(search.trim());
        if(start >= 0 && size > 0) return bookService.booksPaginated(start, size);
        return bookService.getBooks();
    }
    
    /**
     * Get book by id
     * path = /booksREST/webapi/books/1
     * @param id
     * @return 
     * @throws exceptions.BookNotFoundException 
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book getBook(@PathParam("id") long id) throws BookNotFoundException {
        return bookService.getBook(id);
    }
    
    /**
     * Adds given book without
     * path = /booksREST/webapi/books/
     * format = (id is optional)
     * {
     *  "author": "William Gibson 1",
     *  "description": "bla",
     *  "genre": "cyberpunk",
     *  "price": 11.8,
     *  "title": "Neuromancer",
     *  "year": 1984
     * }
     * @param book
     * @param uriInfo
     * @return 
     * @throws java.net.URISyntaxException 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBook(Book book, @Context UriInfo uriInfo) throws URISyntaxException {
        Book newBook = bookService.addBook(book);
        
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newBook.getId()))
                .build();
        
        return Response.created(uri)
                .entity(newBook)
                .build();
    }
    
    /**
     * Edit the book with given id
     * path = /booksREST/webapi/books/1
     * format = (id is optional)
     * {
     *  "author": "William Gibson 1",
     *  "description": "bla",
     *  "genre": "cyberpunk",
     *  "price": 11.8,
     *  "title": "Neuromancer",
     *  "year": 1984
     * }
     * @param id
     * @param book
     * @return 
     * @throws exceptions.BookNotFoundException 
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Book updateBook(@PathParam("id") long id, Book book) throws BookNotFoundException {
        book.setId(id);
        return bookService.updateBook(book);
    }
    
    /**
     * Deletes the book with given id
     * path = /booksREST/webapi/books/1
     * @param id
     * @return
     * @throws BookNotFoundException 
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book deleteBook(@PathParam("id") long id) throws BookNotFoundException {
        return bookService.removeBook(id);
    }
    
}
