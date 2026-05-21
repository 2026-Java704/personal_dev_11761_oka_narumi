package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Category;
import com.example.demo.entity.Task;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TaskRepository;

@Controller
public class TaskController {
	private final TaskRepository taskRepository;
	private final CategoryRepository categoryRepository;

	public TaskController(
			TaskRepository taskRepository,
			CategoryRepository categoryRepository) {
		this.taskRepository = taskRepository;
		this.categoryRepository = categoryRepository;
	}

	// タスク一覧表示
	@GetMapping("/tasks")
	public String index(
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(defaultValue = "") String title,
			@RequestParam(defaultValue = "") String keyword,

			Model model) {

		// 全カテゴリー一覧を取得
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categories", categoryList);

		// タスク一覧情報の取得
		List<Task> taskList = null;
		if (categoryId != null) {
			// tasksテーブルをカテゴリーIDを指定して一覧を取得
			taskList = taskRepository.findByCategoryId(categoryId);
		} else if (keyword.length() > 0) {
			// タイトルによる部分一致検索 
			//taskList = taskRepository.findByNameLike("%" + keyword + "%");  //  // Likeを利用した場合は「%」が必要です
			taskList = taskRepository.findByTitleContaining(keyword);
		} else {
			// 全商品検索
			taskList = taskRepository.findAll();
		}
		model.addAttribute("keyword", keyword);
		model.addAttribute("tasks", taskList);

		return "tasks";
	}

	// 新規タスク画面の表示
	@GetMapping("/tasks/new")
	public String add() {
		// addTask.htmlを出力
		return "addTask";
	}

	//新規タスク処理
	@PostMapping("/tasks/add")
	public String add(
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(defaultValue = "") String title,
			@RequestParam(defaultValue = "") LocalDate closing_date,
			@RequestParam(defaultValue = "") Integer progress,
			@RequestParam(defaultValue = "") String memo

	) {

		Category category = categoryRepository.findById(categoryId).get();

		//Taskオブジェクトの生成
		Task task = new Task(category, title, closing_date, progress, memo);
		//tasksテーブルへの反映（INSERT）
		taskRepository.save(task);
		//「/tasks」にGETでリクエストしなおす（リダイレクト）
		return "redirect:/tasks";
	}

	//タスク変更画面表示

	@GetMapping("/tasks/{id}/edit")
	public String edit(@PathVariable Integer id, Model model) {

		Task task = taskRepository.findById(id).get();
		List<Category> categoryList = categoryRepository.findAll();

		model.addAttribute("task", task);
		model.addAttribute("categories", categoryList);

		return "editTask";
	}

	//タスク変更処理
	@PostMapping("/tasks/{id}/edit")
	public String update(
			@PathVariable Integer id,
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(defaultValue = "") String title,
			@RequestParam(defaultValue = "") LocalDate closing_date,
			@RequestParam(defaultValue = "") Integer progress,
			@RequestParam(defaultValue = "") String memo) {

		//tasksテーブルをID（主キー）で検索
		Task task = taskRepository.findById(id).get();

		Category category = categoryRepository.findById(categoryId).get();

		task.setCategory(category);
		task.setTitle(title);
		task.setClosing_date(closing_date);
		task.setProgress(progress);
		task.setMemo(memo);

		//tasksテーブルへの反映（UPDATE）
		taskRepository.save(task);
		//「/tasks」にGETでリクエストし直す（リダイレクト）
		return "redirect:/tasks";
	}

	//削除処理
	@PostMapping("/tasks/{id}/delete")
	public String delete(@PathVariable Integer id) {
		//tasksテーブルから削除（DELETE）
		taskRepository.deleteById(id);
		//「/tasks」にGETでリクエストし直す（リダイレクト）
		return "redirect:/tasks";

	}

}
