package com.alibou.security.service;

import com.alibou.security.book.Book;
import com.alibou.security.book.BookRepository;
import com.alibou.security.book.BookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 书籍服务
 */
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;

    /**
     * 保存书籍
     * @param request BookRequest
     */
    public void save(BookRequest request) {
        var book = Book.builder()
                .id(request.getId())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .build();
        repository.save(book);
    }

    /**
     * 查找所有书籍
     * @return List<Book>
     */
    public List<Book> findAll() {
        return repository.findAll();
    }
}
