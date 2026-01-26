package com.example.demo.repository;

import com.example.demo.model.UsageDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 每日使用记录数据访问接口
 */
@Repository
public interface UsageDailyRepository extends JpaRepository<UsageDaily, Long> {
	// 根据用户ID和日期查找使用记录
	Optional<UsageDaily> findByUserIdAndDate(Long userId, LocalDate date);
}
