//package com.example.demo.repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.example.demo.entity.Category;
//
//public interface CategoryRepository extends JpaRepository<Category, Integer> {
//	List<Category> findByUserId(Integer userId);
//}

package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findByUserIdOrUserId(Integer defaultUserId, Integer userId);
}