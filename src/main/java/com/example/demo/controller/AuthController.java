package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 认证控制器（登录、注册）
 */
@Controller
public class AuthController {
	@Autowired
	private UserService userService;

	@GetMapping("/login")
	public String loginPage(@RequestParam(required = false) String error,
			@RequestParam(required = false) String logout, Model model) {
		if (error != null) {
			model.addAttribute("error", "邮箱或密码错误");
		}
		if (logout != null) {
			model.addAttribute("message", "已成功退出登录");
		}
		return "login";
	}

	@GetMapping("/register")
	public String registerPage() {
		return "register";
	}

	@PostMapping("/register/submit")
	public String register(@RequestParam String email, @RequestParam String password, Model model) {
		try {
			// 简单验证
			if (email == null || email.trim().isEmpty()) {
				model.addAttribute("error", "邮箱不能为空");
				return "register";
			}
			if (password == null || password.length() < 6) {
				model.addAttribute("error", "密码至少需要 6 个字符");
				return "register";
			}

			userService.register(email.trim(), password);
			model.addAttribute("message", "注册成功，请登录");
			return "login";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "register";
		}
	}
}
