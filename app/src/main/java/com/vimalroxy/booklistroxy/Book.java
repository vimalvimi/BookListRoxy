package com.vimalroxy.booklistroxy;

public class Book {

    private String bookName;
    private String author;
    private String bookIcon;
    private String rating;

    public Book(String bookName, String author, String bookIcon, String rating) {
        this.bookName = bookName;
        this.author = author;
        this.bookIcon = bookIcon;
        this.rating = rating;
    }

    public Book(String bookName, String author, String rating) {
        this.bookName = bookName;
        this.author = author;
        this.rating = rating;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public String getBookIcon() {
        return bookIcon;
    }

    public String getRating() {
        return rating;
    }
}