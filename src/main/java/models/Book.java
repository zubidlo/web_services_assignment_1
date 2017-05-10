package models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Book POJO
 * @author martin
 */

@XmlRootElement
public class Book {
    private long id;
    private String author;
    private String title;
    private String genre;
    private int year;
    private double price;
    private String description;
    
    public Book() {}

    public Book(long id, String author, String title, String genre, int year, double price, String description) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.price = price;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        assert id > 0;
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        assert author != null && !author.equals("");
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        assert title != null && !title.equals("");
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        assert genre != null && !genre.equals("");
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        assert year > 1600 && year < 2100;
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        assert price >= 0;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        assert description != null;
        this.description = description;
    }

}
