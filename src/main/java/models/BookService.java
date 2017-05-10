package models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import exceptions.BookNotFoundException;

/**
 * Mock book database.
 * @author martin
 */
public class BookService {
    
    private static final List<Book> books = new ArrayList<>();
    
    static { resetBooks(); }
    
    /**
     * Initializes the list of books
     */
    public static void resetBooks() {
        if(books.size() > 0) books.clear();
        books.add(new Book(1, "Frank Herbert", "Dune", "space opera", 1965, 11.40, "The first and best of Herbert's Dune series - to survive on sandworld Arrakis water resources must be carefully preserved. A political power struggle over an immortality drug sees the hero lead desert dwellers and sandworms into battle and begin his rise to messiah status. A sprawling saga that remains immensely popular. Dune Messiah (1969) and Children of Dune (1976) complete the trilogy."));
        books.add(new Book(2, "Orson Scott Card" , "Ender's Game", "military sci-fi", 1985, 9.70, "The first book in Card's superb Ender sequence, although it easily holds its own as a stand-alone. Humanity is attacked by aliens and almost wiped out. Child genius Ender Wiggin is top of the heap in the government's military genius breeding program. A race against time springs some surprises for both Ender and the invading forces. A feature film adaptation was released in 2013."));
        books.add(new Book(3, "Isaac Asimov", "Foundation Trilogy", "hard sci-fi", 1953, 10.00, "Much-loved series tracing the decline and fall of a galactic empire. Psychohistorian Hari Seldon, however, makes contingency plans - with art, science and technology eventually saving the day. Asimov's scientific background shows in Foundation's frequent intellectual discourse. Won a retrospective Hugo as all-time best series"));
        books.add(new Book(4, "Douglas Adams", "Hitch-Hiker's Guide to the Galaxy", "space opera", 1979, 5.50, "It's the end of the world as we know it - and when a book is this funny - I feel fine. The guide, of course, is what we segue to as we follow Arthur Dent, the last human left alive, on his unlikely travels around the galaxy. By book four in the \"trilogy\" the jokes were wearing a little thin. This one, however, is not to be missed for the world."));
        books.add(new Book(5, "Arthur C Clarke", "A Space Odyssey", "hard sci-fi", 1968, 6.40, "The now-familiar story of human evolution that culminates in a mission to Saturn to track down the origins of a monolith found on the moon. The super-intelligent HAL 9000 computer starts getting some ideas of his own along the way. Based on the co-written screenplay for the famous Stanley Kubrick feature feature film. A good novel, but rides the film's coattails to a certain degree."));
        books.add(new Book(6, "Robert A Heinlein", "Stranger in a Strange Land", "social sci-fi", 1961, 9.20, "No respectable hippie-era pad was complete without a copy of Stranger lying on a beanbag somewhere. Human raised by Martians arrives back on Earth, inherits a fortune and is indoctrinated by a Heinlein-like author. He becomes a messiah and free-love abounds. A controversial bestseller in its day and still manages to raise some eyebrows."));
        books.add(new Book(7, "George Orwell", "1984", "dystopia", 1949, 8.30, "Orwell got the title by reversing the last two digits in 1948 - the year he wrote this greatest of all anti-utopian satires. A minor bureaucrat in a totalitarian state rebels against the ruling Party and its almost mythical leader Big Brother. Terms like Newspeak, Doublethink and Thought Police became part of the language. Immensely influential."));
        books.add(new Book(8, "Ray Bradbury", "Fahrenheit 451", "dystopia", 1954, 10.30, "Near-future 'firemen' are charged with the responsibility of burning all books in order to wipe out dangerous and subversive ideas. Wall-to-wall TV satiates the masses, while fireman hero Montag secretly reads books. He finally flees the city and takes refuge with a group of 'memorisers' - quite literally people who memorise books. Timeless classic."));
        books.add(new Book(9, "Philip K Dick", "Do Androids Dream of Electric Sheep?", "cyberpunk", 1968, 13.30, "Filmed as Blade Runner, this PKD classic has lost none of its appeal over the years. Bounty hunter Rick Deckard tracks down renegade 'replicants' - almost-faultlessly lifelike androids created to fill the void left by the devastating World War. As usual, Dick keeps us guessing - and the hunter becomes the hunted. Must read novel."));
        books.add(new Book(10, "William Gibson", "Neuromancer", "cyberpunk", 1984, 11.80, "Groundbreaking cyber-granddaddy of them all, this book won the 'Holy Trinity' of sci-fi awards (Hugo, Nebula, Dick). A computer cowboy jacks his mind into cyberspace and swipes information for sale to the highest bidder. Caught in a double-cross, his brain pays the price. The only cure is a conspiratorial cyber-satanic deal. Remains popular."));
    }
       
    /**
     * Returns all the books.
     * @return books
     */
    public List<Book> getBooks() {
        return books;
    }
    
    /**
     * Returns book by id.
     * @param id
     * @return 
     * @throws exceptions.BookNotFoundException 
     */
    public Book getBook(final long id) throws BookNotFoundException {
        assert id > 0;
        Book book = books.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
        if(book != null) return book;
        throw new BookNotFoundException(String.format("Book with id %d not found", id));
        
    }
    
    /**
     * Adds given book.
     * @param book
     * @return 
     */
    public synchronized Book addBook(final Book book) {
        assert book != null;
        Book bookWithMaxid = books.stream().max((a, b) -> (int) a.getId() - (int) b.getId()).get();
        
        long newId = bookWithMaxid.getId() + 1;
        book.setId(newId);
        books.add(book);
        return book;
    }
    
    /**
     * Updates given book.
     * @param book
     * @return 
     * @throws exceptions.BookNotFoundException 
     */
    public synchronized Book updateBook(final Book book) throws BookNotFoundException {
        assert book.getId() > 0;
        Book bookIn = getBook(book.getId());
        assert bookIn != null;
        bookIn.setAuthor(book.getAuthor());
        bookIn.setTitle(book.getTitle());
        bookIn.setPrice(book.getPrice());
        bookIn.setDescription(book.getDescription());
        bookIn.setYear(book.getYear());
        bookIn.setGenre(book.getGenre());
        return bookIn;
    }
    
    /**
     * Removes given book by id.
     * @param id
     * @return 
     * @throws exceptions.BookNotFoundException 
     */
    public synchronized Book removeBook(final long id) throws BookNotFoundException {
        assert id > 0;
        Book book = getBook(id);
        assert book != null;
        books.remove(book);
        return book;
    }
    
    /**
     * Returns list of books by given genre.
     * @param genre
     * @return 
     */
    public List<Book> booksByGenre(final String genre) {
        assert genre != null && !genre.equals("");
        return books.stream()
                .filter(b -> b.getGenre().equals(genre))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns books starting from given index and given size of the returned list.
     * @param start
     * @param size
     * @return 
     */
    public List<Book> booksPaginated(final int start, final int size) {
        assert start >= 0 && start < books.size() && size > 0 && start + size < books.size();
        return books.stream()
                .skip(start)
                .limit(size)
                .collect(Collectors.toList());
    }

    public List<Book> booksBySearch(String search) {
        assert search != null && !search.equals("");
        List<Book> foundBooks = books.stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(search.toLowerCase()) ||
                        b.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                        b.getDescription().toLowerCase().contains(search.toLowerCase()) ||
                        (String.valueOf(b.getYear())).toLowerCase().contains(search.toLowerCase()) ||
                        (String.valueOf(b.getPrice())).toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
        return foundBooks;
    }
}
