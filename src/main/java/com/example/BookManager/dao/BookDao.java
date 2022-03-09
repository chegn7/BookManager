package com.example.BookManager.dao;


import com.example.BookManager.model.Book;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface BookDao {

    List<Book> selectAll();

    int addBook(Book book);

    Book selectById(int id);

    Book selectByName(String name);
}
