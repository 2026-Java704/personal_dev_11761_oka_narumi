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
			@RequestParam(defaultValue = "") String sort,
			@RequestParam(defaultValue = "") Integer importance,
			@RequestParam(defaultValue = "") LocalDate closinDate,
			Model model) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		// ユーザーごとの全カテゴリー一覧を取得
		List<Category> categoryList = categoryRepository.findByUserIdOrUserId(0, account.getId());
		model.addAttribute("categories", categoryList);

		// タスク一覧情報の取得
		List<Task> taskList = null;
		if (categoryId != null) {
			taskList = taskRepository.findByUserIdAndCategoryIdAndCompletedFalse(account.getId(), categoryId);
		} else if (keyword.length() > 0) {
			taskList = taskRepository.findByUserIdAndTitleContainingAndCompletedFalse(account.getId(), keyword);
		} else if ("closingDateAsc".equals(sort)) {
			taskList = taskRepository.findByUserIdAndCompletedFalseOrderByClosingDateAsc(account.getId());
		} else if (importance != null) {
			taskList = taskRepository.findByUserIdAndImportanceAndCompletedFalse(account.getId(), importance);
		} else {
			taskList = taskRepository.findByUserIdAndCompletedFalse(account.getId());
		}

		model.addAttribute("keyword", keyword);
		model.addAttribute("tasks", taskList);

		return "tasks";
	}

	@GetMapping("/tasks/history")
	public String history(Model model) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		List<Task> taskList = taskRepository.findByUserIdAndCompletedTrue(account.getId());
		model.addAttribute("tasks", taskList);

		return "history";
	}

	// 新規タスク画面の表示
	@GetMapping("/tasks/new")
	public String add(Model model) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		List<Category> categoryList = categoryRepository.findByUserIdOrUserId(0, account.getId());
		model.addAttribute("categories", categoryList);

		// addTask.htmlを出力
		return "addTask";
	}

	//新規タスク処理
	@PostMapping("/tasks/add")
	public String add(
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(defaultValue = "") String title,
			@RequestParam(defaultValue = "") LocalDate closingDate,
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
		if (closingDate == null) {
			errorList.add("期限を入力してください");
		}

		// エラー発生時はお問い合わせフォームに戻す
		if (errorList.size() > 0) {
			model.addAttribute("errorList", errorList);
			model.addAttribute("title", title);
			model.addAttribute("closingDate", closingDate);
			return "addTask";
		}

		Category category = categoryRepository.findById(categoryId).get();

		//Taskオブジェクトの生成
		Boolean completed = false;
		if (progress != null && progress == 2) {
			completed = true;
		}

		Task task = new Task(account.getId(), category, title, closingDate, progress, importance, memo, completed);
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
		List<Category> categoryList = categoryRepository.findByUserIdOrUserId(0, account.getId());
		model.addAttribute("categories", categoryList);
		model.addAttribute("task", task);

		return "editTask";
	}

	//タスク変更処理
	@PostMapping("/tasks/{id}/edit")
	public String update(
			@PathVariable Integer id,
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(defaultValue = "") String title,
			@RequestParam(defaultValue = "") LocalDate closingDate,
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
		task.setClosingDate(closingDate);
		task.setProgress(progress);
		task.setImportance(importance);
		task.setMemo(memo);
		if (progress != null && progress == 2) {
			task.setCompleted(true);
		} else {
			task.setCompleted(false);
		}

		//tasksテーブルへの反映（UPDATE）
		taskRepository.save(task);
		//「/tasks」にGETでリクエストし直す（リダイレクト）
		return "redirect:/tasks";
	}

	@PostMapping("/tasks/{id}/complete")
	public String complete(@PathVariable Integer id) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		Task task = taskRepository.findById(id).get();

		if (!task.getUserId().equals(account.getId())) {
			return "redirect:/tasks";
		}

		task.setCompleted(true);
		task.setProgress(2);
		taskRepository.save(task);

		return "redirect:/tasks/history";
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

	//カテゴリー新規作成
	@GetMapping("/categories/add")
	public String addCategory() {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		// addCategory.htmlを出力
		return "addCategory";
	}

	@PostMapping("/categories/add")
	public String store(
			@RequestParam(defaultValue = "") String name,
			Model model) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		if (name == null || name.length() == 0) {
			model.addAttribute("error", "カテゴリー名を入力してください");
			return "addCategory";
		}

		Category category = new Category(account.getId(), name);

		categoryRepository.save(category);

		return "redirect:/tasks";
	}
	//	// カテゴリー新規登録処理
	//	@PostMapping("/categories/add")
	//	public String store(
	//			@RequestParam(defaultValue = "") String name,
	//			Model model) {
	//
	//		if (account.getId() == null) {
	//			return "redirect:/login";
	//		}
	//
	//		if (name == null || name.length() == 0) {
	//			model.addAttribute("error", "カテゴリー名を入力してください");
	//			return "addCategory";
	//		}
	//
	//		Category category = new Category(account.getId(), name);
	//
	//		categoryRepository.save(category);
	//		return "redirect:/tasks";
	//	}

	//	// 更新画面表示
	//	@GetMapping("/categories/{id}/edit")
	//	public String editCategory(
	//			@PathVariable Integer id,
	//			Model model) {
	//
	//		// テーブルをID（主キー）で検索
	//		Category category = categoryRepository.findById(id).get();
	//		model.addAttribute("category", category);
	//
	//		return "editCategory";
	//	}
	//
	//	// 更新処理
	//	@PostMapping("/categories/{id}/edit")
	//	public String update(
	//			@PathVariable Integer id,
	//			@RequestParam(defaultValue = "") String name,
	//			Model model) {
	//
	//		// テーブルをID（主キー）で検索
	//		Category category = categoryRepository.findById(id).get();
	//
	//		// セッターを利用して、categoryオブジェクトのフィールドを書き換える
	//		category.setName(name);
	//
	//		// itemsテーブルへの反映（UPDATE）
	//		categoryRepository.save(category);
	//		return "redirect:/categories";
	//	}

	// 削除処理
	@PostMapping("/categories/{id}/delete")
	public String deleteCategory(@PathVariable Integer id, Model model) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		Category category = categoryRepository.findById(id).get();

		if (!category.getUserId().equals(account.getId())) {
			return "redirect:/categories/index";
		}

		categoryRepository.deleteById(id);
		return "redirect:/categories/index";
	}

	// カテゴリー一覧表示
	@GetMapping("/categories/index")
	public String index(
			@RequestParam(defaultValue = "") Integer categoryId,
			Model model) {

		// テーブルから全カテゴリー一覧を取得
		List<Category> categoryList = categoryRepository.findByUserIdOrUserId(0, account.getId());
		model.addAttribute("categories", categoryList);

		return "category";
	}
}
