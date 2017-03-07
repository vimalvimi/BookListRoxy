package com.vimalroxy.booklistroxy;

/**
 * Created by Roxy on 03-Mar-17.
 */

public class Book {

    private String bookName;
    private String author;
    private String bookIcon;

    public Book(String bookName, String author, String bookIcon) {
        this.bookName = bookName;
        this.author = author;
        this.bookIcon = bookIcon;
    }

    public Book(String bookName, String author) {
        this.bookName = bookName;
        this.author = author;
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
}