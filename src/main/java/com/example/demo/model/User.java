package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password; // BCrypt 加密后的密码

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	// 默认选项
	@Column(name = "default_sanitize")
	private Boolean defaultSanitize = true;

	@Column(name = "default_depth")
	private String defaultDepth = "FAST"; // FAST 或 DEEP

	// 构造函数
	public User() {
		this.createdAt = LocalDateTime.now();
	}

	public User(String email, String password) {
		this();
		this.email = email;
		this.password = password;
	}

	// Getter 和 Setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean getDefaultSanitize() {
		return defaultSanitize;
	}

	public void setDefaultSanitize(Boolean defaultSanitize) {
		this.defaultSanitize = defaultSanitize;
	}

	public String getDefaultDepth() {
		return defaultDepth;
	}

	public void setDefaultDepth(String defaultDepth) {
		this.defaultDepth = defaultDepth;
	}
}
