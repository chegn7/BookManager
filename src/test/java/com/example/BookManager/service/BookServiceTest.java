package com.example.BookManager.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class BookServiceTest {

    @Resource
    BookService bookService;

    @Test
    void getAllBooks() {
        System.out.println(bookService.getAllBooks());
    }

    @Test
    void getBook() {
        System.out.println(bookService.getBook(1));
    }

    @Test
    void testGetBook() {
    }

    @Test
    void addBook() {
    }
}