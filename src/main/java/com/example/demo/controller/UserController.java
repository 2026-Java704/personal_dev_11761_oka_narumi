package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Controller
public class UserController {
	private final UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// ログイン画面を表示
	@GetMapping({ "/", "/login" })
	public String index() {
		// セッション情報を全てクリアする

		return "login";
	}

	// ログインを実行
	@PostMapping("/login")
	public String login(
			@RequestParam String email,
			@RequestParam String password,
			Model model) {
		// エラーチェック
		List<String> errorList = new ArrayList<>();
		// メールアドレスが空の場合にエラーとする
		if (email == null || email.length() == 0) {
			errorList.add("メールアドレスを入力してください");
		}
		//　パスワードが空の場合にエラーとする
		if (password == null || password.length() == 0) {
			errorList.add("パスワードを入力してください");
		}

		// ユーザークラスのオブジェクト（モデル）を生成
		User user = new User(email, password);
		// Thymeleafに渡すデータ（モデル）を追加
		model.addAttribute("user", user);

		// エラー発生時はお問い合わせフォームに戻す
		if (errorList.size() > 0) {
			model.addAttribute("errorList", errorList);
			return "login";
		}

		List<User> userList = userRepository.findByEmailAndPassword(email, password);
		if (userList == null || userList.size() == 0) {
			// 存在しなかった場合
			model.addAttribute("message", "メールアドレスとパスワードが一致しませんでした");
			return "login";
		}
		User user1 = userList.get(0);
		// セッション管理されたアカウント情報にIDと名前をセット
		user.setId(user1.getId());
		user.setName(user1.getName());
		// 「/login」へのリダイレクト
		return "redirect:/login";

	}

	//新規ユーザー登録
	@GetMapping("/users/new")
	public String create() {

		return "userForm";
	}

	//新規登録処理
	@PostMapping("/users/add")
	public String add(@RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "") String email,
			@RequestParam(defaultValue = "") String password,
			Model model) {

		// エラーチェック
		List<String> errorList = new ArrayList<>();
		// 名前が空の場合にエラーとする
		if (name == null || name.length() == 0) {
			errorList.add("名前を入力してください");
		}
		// メールアドレスが空の場合にエラーとする
		if (email == null || email.length() == 0) {
			errorList.add("メールアドレスを入力してください");
		}
		//　パスワードが空の場合にエラーとする
		if (password == null || password.length() == 0) {
			errorList.add("パスワードを入力してください");
		}

		// ユーザークラスのオブジェクト（モデル）を生成
		User user = new User(name, email, password);
		// Thymeleafに渡すデータ（モデル）を追加
		model.addAttribute("user", user);

		// エラー発生時はお問い合わせフォームに戻す
		if (errorList.size() > 0) {
			model.addAttribute("errorList", errorList);
			return "userForm";
		}

		// Userオブジェクトの生成
		User user1 = new User(name, email, password);
		// usersテーブルへの反映（INSERT）
		userRepository.save(user1);
		// 「/login」にGETでリクエストし直す（リダイレクト）
		return "redirect:/login";
	}
}
