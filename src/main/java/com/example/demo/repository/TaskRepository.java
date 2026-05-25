package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
	List<Task> findByCategoryId(Integer categoryId);

	// SELECT * FROM items WHERE name LIKE ?
	// List<Task> findByNameLike(String keyword);
	List<Task> findByTitleContaining(String keyword);

	List<Task> findByUserId(Integer userId);

	List<Task> findByUserIdAndCategoryId(Integer userId, Integer categoryId);

	List<Task> findByUserIdAndTitleContaining(Integer userId, String keyword);

	List<Task> findByUserIdOrderByClosingDateAsc(Integer userId);
}
