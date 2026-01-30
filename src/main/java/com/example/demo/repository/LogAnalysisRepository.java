package com.example.demo.repository;

import com.example.demo.model.LogAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 日志分析报告数据访问接口
 */
@Repository
public interface LogAnalysisRepository extends JpaRepository<LogAnalysis, Long> {

	/** 根据用户ID查找所有分析报告，按创建时间倒序 */
	List<LogAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId);

	/** 按用户 + 错误分类筛选，按创建时间倒序 */
	List<LogAnalysis> findByUserIdAndErrorCategoryOrderByCreatedAtDesc(Long userId, String errorCategory);

	/** 按用户 + 关键词搜索（title 或 sanitizedLog 包含），按创建时间倒序 */
	@Query("SELECT la FROM LogAnalysis la WHERE la.userId = :userId AND (LOWER(la.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(la.sanitizedLog) LIKE LOWER(CONCAT('%', :q, '%'))) ORDER BY la.createdAt DESC")
	List<LogAnalysis> searchByUserIdAndQueryOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("q") String q);

	/** 按用户 + 分类 + 关键词搜索，按创建时间倒序 */
	@Query("SELECT la FROM LogAnalysis la WHERE la.userId = :userId AND la.errorCategory = :cat AND (LOWER(la.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(la.sanitizedLog) LIKE LOWER(CONCAT('%', :q, '%'))) ORDER BY la.createdAt DESC")
	List<LogAnalysis> searchByUserIdAndCategoryAndQueryOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("cat") String cat, @Param("q") String q);
}
