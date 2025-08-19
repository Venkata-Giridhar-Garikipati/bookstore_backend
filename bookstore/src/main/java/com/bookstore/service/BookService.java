package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.dto.BookSearchRequest;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return convertToDTO(book);
    }

    public List<BookDTO> getBooksByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return bookRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookDTO> getBooksInStock() {
        return bookRepository.findBooksInStock()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Category category = categoryRepository.findById(bookDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + bookDTO.getCategoryId()));

        Book book = convertToEntity(bookDTO);
        book.setCategory(category);

        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        Category category = categoryRepository.findById(bookDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + bookDTO.getCategoryId()));

        // Update basic fields
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setDescription(bookDTO.getDescription());
        existingBook.setPrice(bookDTO.getPrice());
        existingBook.setStock(bookDTO.getStock());
        existingBook.setImageUrl(bookDTO.getImageUrl());
        existingBook.setCategory(category);

        // Update enhanced fields
        if (bookDTO.getFeatured() != null) {
            existingBook.setFeatured(bookDTO.getFeatured());
        }
        if (bookDTO.getDiscountPercentage() != null) {
            existingBook.setDiscountPercentage(bookDTO.getDiscountPercentage());
        }

        Book updatedBook = bookRepository.save(existingBook);
        return convertToDTO(updatedBook);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    // Enhanced search method using Specification
    public Page<BookDTO> searchBooks(BookSearchRequest searchRequest) {
        Specification<Book> spec = BookSpecification.createSpecification(searchRequest);

        Pageable pageable = PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                createSort(searchRequest.getSortBy(), searchRequest.getSortDirection())
        );

        Page<Book> books = bookRepository.findAll(spec, pageable);
        return books.map(this::convertToDTO);
    }

    public List<BookDTO> getFeaturedBooks() {
        return bookRepository.findByFeaturedTrueOrderByAverageRatingDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookDTO> getTopRatedBooks(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit,
                Sort.by("averageRating").descending());
        return bookRepository.findByAverageRatingGreaterThan(4.0, pageRequest)
                .getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookDTO incrementViewCount(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        book.setViewCount(Optional.ofNullable(book.getViewCount()).orElse(0L) + 1);

        book = bookRepository.save(book);
        return convertToDTO(book);
    }

    // New methods for enhanced functionality
    public BookDTO toggleFeaturedStatus(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        book.setFeatured(!book.getFeatured());
        book = bookRepository.save(book);
        return convertToDTO(book);
    }

    public BookDTO updateDiscountPercentage(Long bookId, Integer discountPercentage) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }

        book.setDiscountPercentage(discountPercentage);
        book = bookRepository.save(book);
        return convertToDTO(book);
    }

    public List<BookDTO> getDiscountedBooks() {
        return bookRepository.findAll()
                .stream()
                .filter(book -> book.getDiscountPercentage() != null && book.getDiscountPercentage() > 0)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookDTO> getMostViewedBooks(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit,
                Sort.by("viewCount").descending());
        return bookRepository.findAll(pageRequest)
                .getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Trigger rating recalculation (typically called after review operations)
    @Transactional
    public void recalculateBookRating(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        book.calculateAverageRating();
        bookRepository.save(book);
    }

    // Advanced search methods using specifications
    public Page<BookDTO> searchBooksByKeyword(String keyword, Pageable pageable) {
        BookSearchRequest searchRequest = new BookSearchRequest();
        searchRequest.setKeyword(keyword);
        return searchBooks(searchRequest);
    }

    public Page<BookDTO> searchBooksByAuthor(String author, Pageable pageable) {
        BookSearchRequest searchRequest = new BookSearchRequest();
        searchRequest.setAuthor(author);
        return searchBooks(searchRequest);
    }

    public List<BookDTO> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        BookSearchRequest searchRequest = new BookSearchRequest();
        searchRequest.setMinPrice(minPrice);
        searchRequest.setMaxPrice(maxPrice);
        searchRequest.setSize(Integer.MAX_VALUE); // Get all results

        return searchBooks(searchRequest).getContent();
    }

    public List<BookDTO> getBooksByMinimumRating(BigDecimal minRating) {
        BookSearchRequest searchRequest = new BookSearchRequest();
        searchRequest.setMinRating(minRating);
        searchRequest.setSize(Integer.MAX_VALUE); // Get all results

        return searchBooks(searchRequest).getContent();
    }

    // Updated conversion methods
    private BookDTO convertToDTO(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getPrice(),
                book.getStock(),
                book.getImageUrl(),
                book.getCategory().getId(),
                book.getCategory().getName(),
                book.getAverageRating(),
                book.getTotalReviews(),
                book.getViewCount(),
                book.getFeatured(),
                book.getDiscountPercentage()
        );
    }

    private Book convertToEntity(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setDescription(bookDTO.getDescription());
        book.setPrice(bookDTO.getPrice());
        book.setStock(bookDTO.getStock());
        book.setImageUrl(bookDTO.getImageUrl());

        // Set enhanced fields with defaults if not provided
        book.setAverageRating(bookDTO.getAverageRating() != null ? bookDTO.getAverageRating() : BigDecimal.ZERO);
        book.setTotalReviews(bookDTO.getTotalReviews() != null ? bookDTO.getTotalReviews() : 0);
        book.setViewCount(bookDTO.getViewCount() != null ? bookDTO.getViewCount() : 0L);
        book.setFeatured(bookDTO.getFeatured() != null ? bookDTO.getFeatured() : false);
        book.setDiscountPercentage(bookDTO.getDiscountPercentage() != null ? bookDTO.getDiscountPercentage() : 0);

        return book;
    }

    private Sort createSort(String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        return switch (sortBy.toLowerCase()) {
            case "price" -> Sort.by(sortDirection, "price");
            case "rating" -> Sort.by(sortDirection, "averageRating");
            case "newest" -> Sort.by(sortDirection, "id");
            case "popular" -> Sort.by(sortDirection, "viewCount");
            case "author" -> Sort.by(sortDirection, "author");
            case "reviews" -> Sort.by(sortDirection, "totalReviews");
            case "discount" -> Sort.by(sortDirection, "discountPercentage");
            default -> Sort.by(sortDirection, "title");
        };
    }
}
