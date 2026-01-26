package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
		// 重定向到分析页（Spring Security 会自动处理未登录情况）
		return "redirect:/analyze";
	}
}


