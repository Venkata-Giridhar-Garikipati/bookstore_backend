package com.bookstore.repository;

import com.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findByCategoryId(Long categoryId);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Combined search method for simple keyword search
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    @Query("SELECT b FROM Book b WHERE b.stock > 0")
    List<Book> findBooksInStock();

    @Query("SELECT b FROM Book b WHERE b.category.id = :categoryId AND b.stock > 0")
    List<Book> findBooksInStockByCategory(@Param("categoryId") Long categoryId);

    // Phase 4 additions - Enhanced features
    @Query("SELECT b FROM Book b WHERE b.featured = true ORDER BY b.averageRating DESC")
    List<Book> findByFeaturedTrueOrderByAverageRatingDesc();

    // This method was missing the @Param annotation - FIXED
    @Query("SELECT b FROM Book b WHERE b.averageRating > :rating ORDER BY b.averageRating DESC")
    Page<Book> findByAverageRatingGreaterThan(@Param("rating") Double rating, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.stock < :threshold")
    Long countByStockLessThan(@Param("threshold") Integer threshold);

    List<Book> findTop10ByOrderByViewCountDesc();

    @Query("SELECT b FROM Book b WHERE b.stock <= :threshold")
    List<Book> findLowStockBooks(@Param("threshold") Integer threshold);

    // Count books by category
    Long countByCategoryId(Long categoryId);

    // Find books with pagination and sorting
    Page<Book> findAll(Pageable pageable);

    // Additional useful queries
    @Query("SELECT b FROM Book b WHERE b.averageRating >= :minRating ORDER BY b.averageRating DESC")
    List<Book> findByMinimumRating(@Param("minRating") Double minRating);

    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice")
    List<Book> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Find books by multiple criteria
    @Query("SELECT b FROM Book b WHERE " +
            "(:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR b.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR b.price <= :maxPrice) AND " +
            "(:inStock IS NULL OR (:inStock = true AND b.stock > 0) OR (:inStock = false))")
    Page<Book> findBooksWithCriteria(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);
}