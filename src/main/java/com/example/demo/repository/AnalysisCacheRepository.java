package com.example.demo.repository;

import com.example.demo.model.AnalysisCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 分析缓存数据访问接口
 */
@Repository
public interface AnalysisCacheRepository extends JpaRepository<AnalysisCache, Long> {
	// 根据用户ID和日志哈希查找缓存
	Optional<AnalysisCache> findByUserIdAndLogHash(Long userId, String logHash);
}
