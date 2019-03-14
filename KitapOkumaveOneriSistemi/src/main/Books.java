
package main;

/**
 * Yazilim Laboratuvari I Proje 1
 * @author Oguz Aktas
 */
public class Books {
    
    private String ISBN;
    private String title;
    private String author;
    private int year;
    private String publisher;
    private String imgurlsmall;
    private String imgurlmedium;
    private String imgurllarge;

    public Books(String ISBN, String title, String author, int year, String publisher, String imgurlsmall, String imgurlmedium, String imgurllarge) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
        this.imgurlsmall = imgurlsmall;
        this.imgurlmedium = imgurlmedium;
        this.imgurllarge = imgurllarge;
    }

    public Books(String ISBN, String title, String author, int year, String publisher, String imgurlmedium) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
        this.imgurlmedium = imgurlmedium;
    }
    
    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getImgurlsmall() {
        return imgurlsmall;
    }

    public String getImgurlmedium() {
        return imgurlmedium;
    }

    public String getImgurllarge() {
        return imgurllarge;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setImgurlsmall(String imgurlsmall) {
        this.imgurlsmall = imgurlsmall;
    }

    public void setImgurlmedium(String imgurlmedium) {
        this.imgurlmedium = imgurlmedium;
    }

    public void setImgurllarge(String imgurllarge) {
        this.imgurllarge = imgurllarge;
    }
    
}
