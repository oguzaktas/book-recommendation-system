
package main;

/**
 * Yazilim Laboratuvari I Proje 1
 * @author Oguz Aktas
 */
public class BookRatings {
    
    private int id;
    private String ISBN;
    private int rating;

    public BookRatings(int id, String ISBN, int rating) {
        this.id = id;
        this.ISBN = ISBN;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public String getISBN() {
        return ISBN;
    }

    public int getRating() {
        return rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    
}
