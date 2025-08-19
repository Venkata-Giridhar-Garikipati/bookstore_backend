package com.bookstore.repository;

import com.bookstore.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    List<Category> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.books WHERE c.id = :id")
    Optional<Category> findByIdWithBooks(@Param("id") Long id);

    @Query("SELECT c FROM Category c ORDER BY c.name ASC")
    List<Category> findAllOrderByName();
}
