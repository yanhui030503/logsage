package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 每日使用记录实体类（用于额度控制）
 */
@Entity
@Table(name = "usage_daily", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "user_id", "date" })
})
public class UsageDaily {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "count", nullable = false)
	private Integer count = 0;

	public UsageDaily() {
	}

	public UsageDaily(Long userId, LocalDate date) {
		this.userId = userId;
		this.date = date;
		this.count = 0;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public void increment() {
		this.count++;
	}
}
