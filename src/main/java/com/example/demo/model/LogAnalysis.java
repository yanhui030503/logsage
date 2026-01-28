package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 日志分析报告实体类
 */
@Entity
@Table(name = "log_analysis")
public class LogAnalysis {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "log_type", nullable = false)
	private String logType; // JAVA 或 SPRING

	@Column(name = "raw_log", columnDefinition = "TEXT")
	private String rawLog; // 原始日志

	@Column(name = "sanitized_log", columnDefinition = "TEXT")
	private String sanitizedLog; // 脱敏后的日志

	@Column(name = "title")
	private String title; // 自动生成的标题

	@Column(name = "tldr", columnDefinition = "TEXT")
	private String tldr; // TL;DR 摘要

	@Column(name = "top_causes", columnDefinition = "TEXT")
	private String topCauses; // Top 3 可能原因（JSON 格式）

	@Column(name = "verification_steps", columnDefinition = "TEXT")
	private String verificationSteps; // 最短验证步骤（JSON 格式）

	@Column(name = "suggested_fixes", columnDefinition = "TEXT")
	private String suggestedFixes; // 建议修复（JSON 格式）

	@Column(name = "need_more_info", columnDefinition = "TEXT")
	private String needMoreInfo; // 需要更多信息

	@Column(name = "options", columnDefinition = "TEXT")
	private String options; // 分析选项（JSON 格式）

	@Column(name = "tried", columnDefinition = "TEXT")
	private String tried; // 用户已尝试过的排查步骤

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public LogAnalysis() {
		this.createdAt = LocalDateTime.now();
	}

	// Getter 和 Setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getRawLog() {
		return rawLog;
	}

	public void setRawLog(String rawLog) {
		this.rawLog = rawLog;
	}

	public String getSanitizedLog() {
		return sanitizedLog;
	}

	public void setSanitizedLog(String sanitizedLog) {
		this.sanitizedLog = sanitizedLog;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTldr() {
		return tldr;
	}

	public void setTldr(String tldr) {
		this.tldr = tldr;
	}

	public String getTopCauses() {
		return topCauses;
	}

	public void setTopCauses(String topCauses) {
		this.topCauses = topCauses;
	}

	public String getVerificationSteps() {
		return verificationSteps;
	}

	public void setVerificationSteps(String verificationSteps) {
		this.verificationSteps = verificationSteps;
	}

	public String getSuggestedFixes() {
		return suggestedFixes;
	}

	public void setSuggestedFixes(String suggestedFixes) {
		this.suggestedFixes = suggestedFixes;
	}

	public String getNeedMoreInfo() {
		return needMoreInfo;
	}

	public void setNeedMoreInfo(String needMoreInfo) {
		this.needMoreInfo = needMoreInfo;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getTried() {
		return tried;
	}

	public void setTried(String tried) {
		this.tried = tried;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
