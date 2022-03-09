package com.example.BookManager.service;

import com.example.BookManager.dao.BookDao;
import com.example.BookManager.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookDao bookDao;

    public List<Book> getAllBooks() {
        return bookDao.selectAll();
    }

    public Book getBook(int id) {
        try {
            return bookDao.selectById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Book getBook(String name) {
        try {
            return bookDao.selectByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean addBook(Book book) {
        try {
            return bookDao.addBook(book) == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
