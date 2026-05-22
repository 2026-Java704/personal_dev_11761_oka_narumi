package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Category;
import com.example.demo.entity.Task;
import com.example.demo.model.Account;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TaskRepository;

@Controller
public class TaskController {
	private final TaskRepository taskRepository;
	private final CategoryRepository categoryRepository;
	private final Account account;

	public TaskController(
			TaskRepository taskRepository,
			CategoryRepository categoryRepository,
			Account account) {
		this.taskRepository = taskRepository;
		this.categoryRepository = categoryRepository;
		this.account = account;
	}

	// タスク一覧表示
	@GetMapping("/tasks")
	public String index(
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(defaultValue = "") String title,
			@RequestParam(defaultValue = "") String keyword,

			Model model) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		// 全カテゴリー一覧を取得
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categories", categoryList);

		// タスク一覧情報の取得
		List<Task> taskList = null;
		if (categoryId != null) {
			taskList = taskRepository.findByUserIdAndCategoryId(account.getId(), categoryId);
		} else if (keyword.length() > 0) {
			taskList = taskRepository.findByUserIdAndTitleContaining(account.getId(), keyword);
		} else {
			taskList = taskRepository.findByUserId(account.getId());
		}
		model.addAttribute("keyword", keyword);
		model.addAttribute("tasks", taskList);

		return "tasks";
	}

	// 新規タスク画面の表示
	@GetMapping("/tasks/new")
	public String add() {

		if (account.getId() == null) {
			return "redirect:/login";
		}

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
			@RequestParam(defaultValue = "") Integer importance,
			@RequestParam(defaultValue = "") String memo,

			Model model) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		// エラーチェック
		List<String> errorList = new ArrayList<>();
		// メールアドレスが空の場合にエラーとする
		if (title == null || title.length() == 0) {
			errorList.add("タイトルを入力してください");
		}
		//　パスワードが空の場合にエラーとする
		if (closing_date == null) {
			errorList.add("期限を入力してください");
		}

		// エラー発生時はお問い合わせフォームに戻す
		if (errorList.size() > 0) {
			model.addAttribute("errorList", errorList);
			model.addAttribute("title", title);
			model.addAttribute("closing_date", closing_date);
			return "addTask";
		}

		Category category = categoryRepository.findById(categoryId).get();

		//Taskオブジェクトの生成
		Task task = new Task(account.getId(), category, title, closing_date, progress, importance, memo);
		//tasksテーブルへの反映（INSERT）
		taskRepository.save(task);
		//
		task.setUserId(account.getId());
		//「/tasks」にGETでリクエストしなおす（リダイレクト）
		return "redirect:/tasks";
	}

	//タスク変更画面表示

	@GetMapping("/tasks/{id}/edit")
	public String edit(@PathVariable Integer id, Model model) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

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

			@RequestParam(defaultValue = "") Integer importance,
			@RequestParam(defaultValue = "") String memo) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		//tasksテーブルをID（主キー）で検索
		Task task = taskRepository.findById(id).get();

		if (!task.getUserId().equals(account.getId())) {
			return "redirect:/tasks";
		}

		Category category = categoryRepository.findById(categoryId).get();

		task.setCategory(category);
		task.setTitle(title);
		task.setClosing_date(closing_date);
		task.setProgress(progress);
		task.setImportance(importance);
		task.setMemo(memo);

		//tasksテーブルへの反映（UPDATE）
		taskRepository.save(task);
		//「/tasks」にGETでリクエストし直す（リダイレクト）
		return "redirect:/tasks";
	}

	//削除処理
	@PostMapping("/tasks/{id}/delete")
	public String delete(@PathVariable Integer id) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		Task task = taskRepository.findById(id).get();

		if (!task.getUserId().equals(account.getId())) {
			return "redirect:/tasks";
		}

		//tasksテーブルから削除（DELETE）
		taskRepository.deleteById(id);
		//「/tasks」にGETでリクエストし直す（リダイレクト）
		return "redirect:/tasks";

	}

}
