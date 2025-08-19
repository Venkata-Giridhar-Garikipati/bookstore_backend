package com.bookstore.service;

import com.bookstore.dto.ReviewDTO;
import com.bookstore.entity.*;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        User currentUser = getCurrentUser();
        Book book = bookRepository.findById(reviewDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        // Check if user has purchased this book
        boolean hasPurchased = orderRepository.existsByUserIdAndOrderItemsBookId(
                currentUser.getId(), book.getId());

        if (!hasPurchased) {
            throw new IllegalStateException("You can only review books you have purchased");
        }

        // Check if user already reviewed this book
        if (reviewRepository.existsByUserIdAndBookId(currentUser.getId(), book.getId())) {
            throw new DuplicateResourceException("You have already reviewed this book");
        }

        Review review = new Review();
        review.setBook(book);
        review.setUser(currentUser);
        review.setRating(reviewDTO.getRating());
        review.setReviewText(reviewDTO.getReviewText());

        review = reviewRepository.save(review);

        // Update book average rating
        updateBookRating(book);

        return convertToDTO(review);
    }

    public List<ReviewDTO> getBookReviews(Long bookId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByBookIdWithUsers(bookId, pageRequest);

        return reviews.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO updateReview(Long reviewId, ReviewDTO reviewDTO) {
        User currentUser = getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only update your own reviews");
        }

        review.setRating(reviewDTO.getRating());
        review.setReviewText(reviewDTO.getReviewText());
        review = reviewRepository.save(review);

        // Update book average rating
        updateBookRating(review.getBook());

        return convertToDTO(review);
    }

    public void deleteReview(Long reviewId) {
        User currentUser = getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("You can only delete your own reviews");
        }

        Book book = review.getBook();
        reviewRepository.delete(review);

        // Update book average rating
        updateBookRating(book);
    }

    private void updateBookRating(Book book) {
        List<Review> reviews = reviewRepository.findByBookId(book.getId());
        if (!reviews.isEmpty()) {
            double average = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            // Convert to BigDecimal with proper scale and rounding
            book.setAverageRating(BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP));
            book.setTotalReviews(reviews.size());
        } else {
            book.setAverageRating(BigDecimal.ZERO);
            book.setTotalReviews(0);
        }
        bookRepository.save(book);
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getBook().getId(),
//                review.getBook().getTitle(),
                review.getUser().getId(),
//                review.getUser().getName(),
                review.getRating(),
                review.getReviewText(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
