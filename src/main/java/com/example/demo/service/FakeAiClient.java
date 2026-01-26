package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 模拟 AI 客户端（用于 MVP，后续可替换为真实 AI 调用）
 */
@Service
public class FakeAiClient {

	/**
	 * 分析日志并返回结构化结果
	 */
	public AnalysisResult analyzeLog(String sanitizedLog, String logType, boolean generateActionList, String depth) {
		AnalysisResult result = new AnalysisResult();

		// 生成 TL;DR
		result.setTldr(generateTldr(sanitizedLog, logType));

		// 生成 Top 3 可能原因
		result.setTopCauses(generateTopCauses(sanitizedLog, logType));

		// 生成验证步骤
		if (generateActionList) {
			result.setVerificationSteps(generateVerificationSteps(sanitizedLog, logType));
		}

		// 生成建议修复
		result.setSuggestedFixes(generateSuggestedFixes(sanitizedLog, logType, depth));

		// 生成需要更多信息
		result.setNeedMoreInfo(generateNeedMoreInfo(sanitizedLog, logType));

		return result;
	}

	private String generateTldr(String log, String logType) {
		if (log.contains("NullPointerException")) {
			return "检测到空指针异常，可能是对象未初始化或为空导致的运行时错误。";
		} else if (log.contains("BeanCreationException")) {
			return "Spring Bean 创建失败，可能是依赖注入或配置问题。";
		} else if (log.contains("SQLException")) {
			return "数据库连接或 SQL 执行异常，检查数据库配置和 SQL 语句。";
		} else if (log.contains("OutOfMemoryError")) {
			return "内存溢出错误，需要检查内存使用情况或增加堆内存。";
		}
		return "检测到 " + logType + " 相关错误，需要进一步分析日志详情。";
	}

	private List<Map<String, Object>> generateTopCauses(String log, String logType) {
		List<Map<String, Object>> causes = new ArrayList<>();

		if (log.contains("NullPointerException")) {
			Map<String, Object> cause1 = new HashMap<>();
			cause1.put("cause", "对象未初始化");
			cause1.put("confidence", 0.85);
			cause1.put("evidence", "日志中出现 NullPointerException，通常表示访问了 null 对象");
			causes.add(cause1);

			Map<String, Object> cause2 = new HashMap<>();
			cause2.put("cause", "方法返回 null");
			cause2.put("confidence", 0.75);
			cause2.put("evidence", "某个方法可能返回了 null 值，但调用方未做空值检查");
			causes.add(cause2);

			Map<String, Object> cause3 = new HashMap<>();
			cause3.put("cause", "集合或数组为空");
			cause3.put("confidence", 0.65);
			cause3.put("evidence", "可能访问了空集合或数组的元素");
			causes.add(cause3);
		} else if (log.contains("BeanCreationException")) {
			Map<String, Object> cause1 = new HashMap<>();
			cause1.put("cause", "循环依赖");
			cause1.put("confidence", 0.80);
			cause1.put("evidence", "Spring Bean 之间存在循环依赖关系");
			causes.add(cause1);

			Map<String, Object> cause2 = new HashMap<>();
			cause2.put("cause", "缺少必需的依赖");
			cause2.put("confidence", 0.75);
			cause2.put("evidence", "Bean 的构造函数或字段需要注入的依赖不存在");
			causes.add(cause2);

			Map<String, Object> cause3 = new HashMap<>();
			cause3.put("cause", "配置错误");
			cause3.put("confidence", 0.70);
			cause3.put("evidence", "@Component 或 @Service 注解可能缺失，或包扫描路径不正确");
			causes.add(cause3);
		} else {
			// 通用原因
			Map<String, Object> cause1 = new HashMap<>();
			cause1.put("cause", "配置问题");
			cause1.put("confidence", 0.70);
			cause1.put("evidence", "检查相关配置文件是否正确");
			causes.add(cause1);

			Map<String, Object> cause2 = new HashMap<>();
			cause2.put("cause", "资源不足");
			cause2.put("confidence", 0.60);
			cause2.put("evidence", "可能是内存、连接池等资源不足");
			causes.add(cause2);

			Map<String, Object> cause3 = new HashMap<>();
			cause3.put("cause", "版本不兼容");
			cause3.put("confidence", 0.55);
			cause3.put("evidence", "检查依赖版本是否兼容");
			causes.add(cause3);
		}

		return causes;
	}

	private List<Map<String, Object>> generateVerificationSteps(String log, String logType) {
		List<Map<String, Object>> steps = new ArrayList<>();

		Map<String, Object> step1 = new HashMap<>();
		step1.put("step", "检查异常堆栈");
		step1.put("why", "定位具体出错的方法和行号");
		step1.put("command", "查看完整异常堆栈信息");
		steps.add(step1);

		Map<String, Object> step2 = new HashMap<>();
		step2.put("step", "检查相关变量");
		step2.put("why", "确认变量是否已正确初始化");
		step2.put("command", "在出错位置添加日志或断点");
		steps.add(step2);

		Map<String, Object> step3 = new HashMap<>();
		step3.put("step", "重现问题");
		step3.put("why", "确认问题的可重现性");
		step3.put("command", "使用相同输入重新执行操作");
		steps.add(step3);

		return steps;
	}

	private List<Map<String, Object>> generateSuggestedFixes(String log, String logType, String depth) {
		List<Map<String, Object>> fixes = new ArrayList<>();

		if (log.contains("NullPointerException")) {
			Map<String, Object> fix1 = new HashMap<>();
			fix1.put("fix", "添加空值检查：使用 Optional 或 if (obj != null) 判断");
			fix1.put("risk", "低");
			fixes.add(fix1);

			Map<String, Object> fix2 = new HashMap<>();
			fix2.put("fix", "使用 @NonNull 注解或验证器确保对象不为空");
			fix2.put("risk", "低");
			fixes.add(fix2);
		} else if (log.contains("BeanCreationException")) {
			Map<String, Object> fix1 = new HashMap<>();
			fix1.put("fix", "使用 @Lazy 注解解决循环依赖");
			fix1.put("risk", "中");
			fixes.add(fix1);

			Map<String, Object> fix2 = new HashMap<>();
			fix2.put("fix", "检查 @ComponentScan 配置，确保 Bean 能被扫描到");
			fix2.put("risk", "低");
			fixes.add(fix2);
		} else {
			Map<String, Object> fix1 = new HashMap<>();
			fix1.put("fix", "检查配置文件和相关依赖");
			fix1.put("risk", "低");
			fixes.add(fix1);
		}

		if ("DEEP".equals(depth)) {
			Map<String, Object> fixDeep = new HashMap<>();
			fixDeep.put("fix", "进行深度代码审查，检查设计模式和架构问题");
			fixDeep.put("risk", "中");
			fixes.add(fixDeep);
		}

		return fixes;
	}

	private String generateNeedMoreInfo(String log, String logType) {
		if (log.length() < 100) {
			return "日志信息较少，建议提供更多上下文信息，包括：完整的异常堆栈、相关配置、触发操作等。";
		}
		return "如果问题持续存在，建议提供：系统环境信息、相关代码片段、完整的错误日志。";
	}

	/**
	 * 分析结果内部类
	 */
	public static class AnalysisResult {
		private String tldr;
		private List<Map<String, Object>> topCauses;
		private List<Map<String, Object>> verificationSteps;
		private List<Map<String, Object>> suggestedFixes;
		private String needMoreInfo;

		// Getter 和 Setter
		public String getTldr() {
			return tldr;
		}

		public void setTldr(String tldr) {
			this.tldr = tldr;
		}

		public List<Map<String, Object>> getTopCauses() {
			return topCauses;
		}

		public void setTopCauses(List<Map<String, Object>> topCauses) {
			this.topCauses = topCauses;
		}

		public List<Map<String, Object>> getVerificationSteps() {
			return verificationSteps;
		}

		public void setVerificationSteps(List<Map<String, Object>> verificationSteps) {
			this.verificationSteps = verificationSteps;
		}

		public List<Map<String, Object>> getSuggestedFixes() {
			return suggestedFixes;
		}

		public void setSuggestedFixes(List<Map<String, Object>> suggestedFixes) {
			this.suggestedFixes = suggestedFixes;
		}

		public String getNeedMoreInfo() {
			return needMoreInfo;
		}

		public void setNeedMoreInfo(String needMoreInfo) {
			this.needMoreInfo = needMoreInfo;
		}
	}
}
