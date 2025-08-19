package com.bookstore.specification;

import com.bookstore.dto.BookSearchRequest;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    public static Specification<Book> createSpecification(BookSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search by keyword (title or author)
            if (StringUtils.hasText(searchRequest.getKeyword())) {
                String keyword = "%" + searchRequest.getKeyword().toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), keyword);
                Predicate authorPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("author")), keyword);
                predicates.add(criteriaBuilder.or(titlePredicate, authorPredicate));
            }

            // Filter by category
            if (searchRequest.getCategoryId() != null) {
                Join<Book, Category> categoryJoin = root.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("id"), searchRequest.getCategoryId()));
            }

            // Filter by author
            if (StringUtils.hasText(searchRequest.getAuthor())) {
                String author = "%" + searchRequest.getAuthor().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("author")), author));
            }

            // Filter by price range
            if (searchRequest.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"), searchRequest.getMinPrice()));
            }

            if (searchRequest.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"), searchRequest.getMaxPrice()));
            }

            // Filter by minimum rating
            if (searchRequest.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("averageRating"), searchRequest.getMinRating().doubleValue()));
            }

            // Filter by stock availability
            if (searchRequest.getInStock() != null && searchRequest.getInStock()) {
                predicates.add(criteriaBuilder.greaterThan(root.get("stock"), 0));
            }

            // Filter by featured books
            if (searchRequest.getFeatured() != null && searchRequest.getFeatured()) {
                predicates.add(criteriaBuilder.equal(root.get("featured"), true));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
