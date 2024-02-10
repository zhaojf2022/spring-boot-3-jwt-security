package com.alibou.security.controller;

import com.alibou.security.book.Book;
import com.alibou.security.book.BookRequest;
import com.alibou.security.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 书籍控制器
 */
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;

    /**
     * 保存书籍
     * @param request BookRequest
     * @return ResponseEntity<?>
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestBody BookRequest request) {
        service.save(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * 查找所有书籍
     * @return ResponseEntity<List<Book>>
     */
    @GetMapping
    public ResponseEntity<List<Book>> findAllBooks() {
        return ResponseEntity.ok(service.findAll());
    }
}
