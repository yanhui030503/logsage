package com.example.demo.repository;

import com.example.demo.model.LogAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 日志分析报告数据访问接口
 */
@Repository
public interface LogAnalysisRepository extends JpaRepository<LogAnalysis, Long> {
	// 根据用户ID查找所有分析报告，按创建时间倒序
	List<LogAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId);
}
