package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; // カテゴリーID
	@Column(name = "user_id")
	private Integer userId;
	@Column(name = "category_id")
	private Integer categoryId;

	private String title;
	private LocalDate closing_date;
	private Integer progress;
	private String memo;

	public Task() {
	}

	public Task(Integer userId, Integer categoryId, String title, LocalDate closing_date, Integer progress,
			String memo) {
		this.userId = userId;
		this.categoryId = categoryId;
		this.title = title;
		this.closing_date = closing_date;
		this.progress = progress;
		this.memo = memo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDate getClosing_date() {
		return closing_date;
	}

	public void setClosing_date(LocalDate closing_date) {
		this.closing_date = closing_date;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
