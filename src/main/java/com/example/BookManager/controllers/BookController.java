package com.example.BookManager.controllers;

import com.example.BookManager.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String bookList(Model model) {
        System.out.println("bookList");
        loadAllBooksView(model);
        return "book/books";
    }

    private void loadAllBooksView(Model model) {
        Map map = new HashMap<>();
        map.put("books", bookService.getAllBooks());
        model.addAllAttributes(map);
    }
}
