package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 分析缓存实体类（用于去重，避免重复分析相同日志）
 */
@Entity
@Table(name = "analysis_cache", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "user_id", "log_hash" })
})
public class AnalysisCache {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "log_hash", nullable = false, length = 64)
	private String logHash; // SHA-256 哈希值

	@Column(name = "analysis_id", nullable = false)
	private Long analysisId; // 关联的 LogAnalysis ID

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public AnalysisCache() {
		this.createdAt = LocalDateTime.now();
	}

	public AnalysisCache(Long userId, String logHash, Long analysisId) {
		this();
		this.userId = userId;
		this.logHash = logHash;
		this.analysisId = analysisId;
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

	public String getLogHash() {
		return logHash;
	}

	public void setLogHash(String logHash) {
		this.logHash = logHash;
	}

	public Long getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
