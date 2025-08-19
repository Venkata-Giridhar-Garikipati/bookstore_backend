package com.bookstore.controller;

import com.bookstore.dto.BookDTO;
import com.bookstore.dto.BookSearchRequest;
import com.bookstore.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        BookDTO book = bookService.getBookById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BookDTO>> getBooksByCategory(@PathVariable Long categoryId) {
        List<BookDTO> books = bookService.getBooksByCategory(categoryId);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<List<BookDTO>> getBooksInStock() {
        List<BookDTO> books = bookService.getBooksInStock();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<BookDTO>> getFeaturedBooks() {
        List<BookDTO> books = bookService.getFeaturedBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<BookDTO>> getTopRatedBooks(
            @RequestParam(defaultValue = "10") int limit) {
        List<BookDTO> books = bookService.getTopRatedBooks(limit);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PostMapping("/{id}/increment-view")
    public ResponseEntity<BookDTO> incrementViewCount(@PathVariable Long id) {
        BookDTO book = bookService.incrementViewCount(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookDTO>> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        BookSearchRequest searchRequest = new BookSearchRequest(
                keyword, categoryId, author, minPrice, maxPrice, minRating,
                inStock, featured, page, size, sortBy, sortDirection
        );

        return ResponseEntity.ok(bookService.searchBooks(searchRequest));
    }

    // Additional search endpoints for specific use cases
    @GetMapping("/search/by-keyword")
    public ResponseEntity<Page<BookDTO>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(sortDirection) ?
                        Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

        return ResponseEntity.ok(bookService.searchBooksByKeyword(keyword, pageable));
    }

    @GetMapping("/search/by-author")
    public ResponseEntity<Page<BookDTO>> searchByAuthor(
            @RequestParam String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(sortDirection) ?
                        Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

        return ResponseEntity.ok(bookService.searchBooksByAuthor(author, pageable));
    }

    @GetMapping("/search/by-price-range")
    public ResponseEntity<List<BookDTO>> searchByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {

        return ResponseEntity.ok(bookService.getBooksByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/search/by-rating")
    public ResponseEntity<List<BookDTO>> searchByMinimumRating(
            @RequestParam BigDecimal minRating) {

        return ResponseEntity.ok(bookService.getBooksByMinimumRating(minRating));
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id,
                                              @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>("Book deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/{id}/featured")
    public ResponseEntity<BookDTO> toggleFeaturedStatus(@PathVariable Long id) {
        BookDTO book = bookService.toggleFeaturedStatus(id);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}/discount")
    public ResponseEntity<BookDTO> updateDiscountPercentage(
            @PathVariable Long id,
            @RequestParam Integer discountPercentage) {
        BookDTO book = bookService.updateDiscountPercentage(id, discountPercentage);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/discounted")
    public ResponseEntity<List<BookDTO>> getDiscountedBooks() {
        List<BookDTO> books = bookService.getDiscountedBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<List<BookDTO>> getMostViewedBooks(
            @RequestParam(defaultValue = "10") int limit) {
        List<BookDTO> books = bookService.getMostViewedBooks(limit);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/{id}/recalculate-rating")
    public ResponseEntity<String> recalculateBookRating(@PathVariable Long id) {
        bookService.recalculateBookRating(id);
        return ResponseEntity.ok("Rating recalculated successfully");
    }

}