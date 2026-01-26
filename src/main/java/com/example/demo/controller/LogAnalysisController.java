package com.example.demo.controller;

import com.example.demo.model.LogAnalysis;
import com.example.demo.model.User;
import com.example.demo.service.LogAnalysisService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 日志分析控制器
 */
@Controller
public class LogAnalysisController {
	@Autowired
	private LogAnalysisService logAnalysisService;

	@Autowired
	private UserService userService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 获取当前登录用户
	 */
	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return userService.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("用户不存在"));
	}

	/**
	 * 分析页面
	 */
	@GetMapping("/analyze")
	public String analyzePage(Model model) {
		User user = getCurrentUser();
		Map<String, Object> usage = logAnalysisService.getDailyUsage(user.getId());
		model.addAttribute("usage", usage);
		model.addAttribute("defaultSanitize", user.getDefaultSanitize());
		model.addAttribute("defaultDepth", user.getDefaultDepth());
		return "analyze";
	}

	/**
	 * 提交分析
	 */
	@PostMapping("/analyze")
	public String analyze(@RequestParam String rawLog, @RequestParam String logType,
			@RequestParam(required = false, defaultValue = "true") boolean sanitize,
			@RequestParam(required = false, defaultValue = "true") boolean generateActionList,
			@RequestParam(required = false, defaultValue = "FAST") String depth,
			@RequestParam(required = false) String saveAsReport,
			Model model) {
		User user = getCurrentUser();

		try {
			// 检查输入长度
			if (rawLog != null && rawLog.length() > 8000) {
				model.addAttribute("error", "日志长度超过 8000 字符限制");
				Map<String, Object> usage = logAnalysisService.getDailyUsage(user.getId());
				model.addAttribute("usage", usage);
				model.addAttribute("defaultSanitize", user.getDefaultSanitize());
				model.addAttribute("defaultDepth", user.getDefaultDepth());
				return "analyze";
			}

			// 执行分析
			LogAnalysis analysis = logAnalysisService.analyzeLog(user.getId(), rawLog, logType, sanitize,
					generateActionList, depth);

			// 解析 JSON 字段
			Map<String, Object> analysisData = new HashMap<>();
			analysisData.put("id", analysis.getId());
			analysisData.put("tldr", analysis.getTldr());
			try {
				analysisData.put("topCauses", objectMapper.readValue(analysis.getTopCauses(), List.class));
				analysisData.put("verificationSteps",
						objectMapper.readValue(analysis.getVerificationSteps(), List.class));
				analysisData.put("suggestedFixes", objectMapper.readValue(analysis.getSuggestedFixes(), List.class));
			} catch (Exception e) {
				// 忽略解析错误
			}
			analysisData.put("needMoreInfo", analysis.getNeedMoreInfo());

			model.addAttribute("analysis", analysisData);
			model.addAttribute("rawLog", rawLog);
			model.addAttribute("logType", logType);

			// 如果用户选择保存为报告，重定向到报告详情页
			if ("true".equals(saveAsReport)) {
				return "redirect:/report/" + analysis.getId();
			}

			// 更新使用情况
			Map<String, Object> usage = logAnalysisService.getDailyUsage(user.getId());
			model.addAttribute("usage", usage);
			model.addAttribute("defaultSanitize", user.getDefaultSanitize());
			model.addAttribute("defaultDepth", user.getDefaultDepth());

			return "analyze";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			Map<String, Object> usage = logAnalysisService.getDailyUsage(user.getId());
			model.addAttribute("usage", usage);
			model.addAttribute("defaultSanitize", user.getDefaultSanitize());
			model.addAttribute("defaultDepth", user.getDefaultDepth());
			return "analyze";
		}
	}

	/**
	 * 历史记录页面
	 */
	@GetMapping("/history")
	public String historyPage(Model model) {
		User user = getCurrentUser();
		List<LogAnalysis> history = logAnalysisService.getUserHistory(user.getId());
		model.addAttribute("history", history);
		return "history";
	}

	/**
	 * 报告详情页面
	 */
	@GetMapping("/report/{id}")
	public String reportPage(@PathVariable Long id, Model model) {
		User user = getCurrentUser();
		Optional<LogAnalysis> analysisOpt = logAnalysisService.getAnalysisById(id, user.getId());

		if (analysisOpt.isEmpty()) {
			model.addAttribute("error", "报告不存在或无权限访问");
			return "error";
		}

		LogAnalysis analysis = analysisOpt.get();
		Map<String, Object> analysisData = new HashMap<>();
		analysisData.put("id", analysis.getId());
		analysisData.put("title", analysis.getTitle());
		analysisData.put("logType", analysis.getLogType());
		analysisData.put("createdAt", analysis.getCreatedAt());
		analysisData.put("tldr", analysis.getTldr());
		try {
			analysisData.put("topCauses", objectMapper.readValue(analysis.getTopCauses(), List.class));
			analysisData.put("verificationSteps",
					objectMapper.readValue(analysis.getVerificationSteps(), List.class));
			analysisData.put("suggestedFixes", objectMapper.readValue(analysis.getSuggestedFixes(), List.class));
		} catch (Exception e) {
			// 忽略解析错误
		}
		analysisData.put("needMoreInfo", analysis.getNeedMoreInfo());

		model.addAttribute("analysis", analysisData);
		return "report";
	}

	/**
	 * 设置页面
	 */
	@GetMapping("/settings")
	public String settingsPage(Model model) {
		User user = getCurrentUser();
		Map<String, Object> usage = logAnalysisService.getDailyUsage(user.getId());
		model.addAttribute("usage", usage);
		model.addAttribute("user", user);
		return "settings";
	}

	/**
	 * 更新设置
	 */
	@PostMapping("/settings/update")
	public String updateSettings(@RequestParam(required = false) Boolean defaultSanitize,
			@RequestParam(required = false) String defaultDepth) {
		User user = getCurrentUser();
		userService.updateUserSettings(user.getId(), defaultSanitize, defaultDepth);
		return "redirect:/settings?updated=true";
	}
}
