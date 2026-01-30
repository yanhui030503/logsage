package com.example.demo.service;

import com.example.demo.model.AnalysisCache;
import com.example.demo.model.LogAnalysis;
import com.example.demo.model.UsageDaily;
import com.example.demo.repository.AnalysisCacheRepository;
import com.example.demo.repository.LogAnalysisRepository;
import com.example.demo.repository.UsageDailyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 日志分析服务类
 */
@Service
public class LogAnalysisService {
	@Autowired
	private LogAnalysisRepository logAnalysisRepository;

	@Autowired
	private AnalysisCacheRepository analysisCacheRepository;

	@Autowired
	private UsageDailyRepository usageDailyRepository;

	@Autowired
	private FakeAiClient fakeAiClient;

	@Value("${logsage.daily.limit:20}")
	private int dailyLimit;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 检查用户今日是否还有额度
	 */
	public boolean checkDailyLimit(Long userId) {
		LocalDate today = LocalDate.now();
		Optional<UsageDaily> usage = usageDailyRepository.findByUserIdAndDate(userId, today);
		int usedCount = usage.map(UsageDaily::getCount).orElse(0);
		return usedCount < dailyLimit;
	}

	/**
	 * 获取用户今日使用情况
	 */
	public Map<String, Object> getDailyUsage(Long userId) {
		LocalDate today = LocalDate.now();
		Optional<UsageDaily> usage = usageDailyRepository.findByUserIdAndDate(userId, today);
		int usedCount = usage.map(UsageDaily::getCount).orElse(0);
		int remaining = Math.max(0, dailyLimit - usedCount);

		Map<String, Object> result = new HashMap<>();
		result.put("used", usedCount);
		result.put("limit", dailyLimit);
		result.put("remaining", remaining);
		return result;
	}

	/**
	 * 脱敏日志（简单实现：移除可能的敏感信息）
	 */
	public String sanitizeLog(String rawLog) {
		if (rawLog == null) {
			return "";
		}
		// 简单脱敏：移除可能的 IP、邮箱、密码等
		return rawLog
				.replaceAll("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b", "[IP]")
				.replaceAll("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b", "[EMAIL]")
				.replaceAll("password\\s*=\\s*[^\\s]+", "password=[HIDDEN]")
				.replaceAll("pwd\\s*=\\s*[^\\s]+", "pwd=[HIDDEN]");
	}

	/**
	 * 计算日志哈希（用于缓存去重）
	 */
	private String calculateLogHash(String sanitizedLog, String logType, String options) {
		try {
			String input = sanitizedLog + "|" + logType + "|" + options;
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException("计算哈希失败", e);
		}
	}

	/**
	 * 分析日志
	 */
	@Transactional
	public LogAnalysis analyzeLog(Long userId, String rawLog, String logType, boolean sanitize,
			boolean generateActionList, String depth, String tried) {
		// 检查输入长度
		if (rawLog != null && rawLog.length() > 8000) {
			throw new RuntimeException("日志长度超过 8000 字符限制");
		}

		// 检查每日额度
		if (!checkDailyLimit(userId)) {
			throw new RuntimeException("今日分析次数已达上限（" + dailyLimit + " 次）");
		}

		// 脱敏
		String sanitizedLog = sanitize ? sanitizeLog(rawLog) : rawLog;

		// 构建选项 JSON
		Map<String, Object> optionsMap = new HashMap<>();
		optionsMap.put("sanitize", sanitize);
		optionsMap.put("generateActionList", generateActionList);
		optionsMap.put("depth", depth);
		String optionsJson;
		try {
			optionsJson = objectMapper.writeValueAsString(optionsMap);
		} catch (Exception e) {
			optionsJson = "{}";
		}

		// 如果有 tried 字段，不使用缓存（因为不同的 tried 应该产生不同的结果）
		// 检查缓存（仅当没有 tried 时）
		Optional<AnalysisCache> cache = Optional.empty();
		if (tried == null || tried.trim().isEmpty()) {
			String logHash = calculateLogHash(sanitizedLog, logType, optionsJson);
			cache = analysisCacheRepository.findByUserIdAndLogHash(userId, logHash);
			if (cache.isPresent()) {
				// 返回缓存的分析结果
				Long cachedAnalysisId = cache.get().getAnalysisId();
				return logAnalysisRepository.findById(cachedAnalysisId)
						.orElseThrow(() -> new RuntimeException("缓存的分析结果不存在"));
			}
		}

		// 调用 AI 分析
		FakeAiClient.AnalysisResult aiResult = fakeAiClient.analyzeLog(sanitizedLog, logType, generateActionList,
				depth, tried);

		// 生成标题
		String title = generateTitle(rawLog, logType);

		// 保存分析结果
		LogAnalysis analysis = new LogAnalysis();
		analysis.setUserId(userId);
		analysis.setLogType(logType);
		analysis.setRawLog(rawLog);
		analysis.setSanitizedLog(sanitizedLog);
		analysis.setTitle(title);
		analysis.setTldr(aiResult.getTldr());
		try {
			analysis.setTopCauses(objectMapper.writeValueAsString(aiResult.getTopCauses()));
			analysis.setVerificationSteps(objectMapper.writeValueAsString(aiResult.getVerificationSteps()));
			analysis.setSuggestedFixes(objectMapper.writeValueAsString(aiResult.getSuggestedFixes()));
		} catch (Exception e) {
			// 忽略 JSON 序列化错误
		}
		analysis.setNeedMoreInfo(aiResult.getNeedMoreInfo());
		analysis.setOptions(optionsJson);
		analysis.setTried(tried != null ? tried.trim() : null);
		analysis.setErrorCategory(deriveErrorCategory(sanitizedLog));

		LogAnalysis saved = logAnalysisRepository.save(analysis);

		// 保存缓存（仅当没有 tried 时）
		if (tried == null || tried.trim().isEmpty()) {
			String logHash = calculateLogHash(sanitizedLog, logType, optionsJson);
			AnalysisCache cacheEntry = new AnalysisCache(userId, logHash, saved.getId());
			analysisCacheRepository.save(cacheEntry);
		}

		// 更新每日使用计数
		LocalDate today = LocalDate.now();
		UsageDaily usage = usageDailyRepository.findByUserIdAndDate(userId, today)
				.orElse(new UsageDaily(userId, today));
		usage.increment();
		usageDailyRepository.save(usage);

		return saved;
	}

	/**
	 * 生成分析报告标题
	 */
	private String generateTitle(String rawLog, String logType) {
		if (rawLog == null || rawLog.trim().isEmpty()) {
			return logType + " 日志分析";
		}
		String lowerLog = rawLog.toLowerCase();
		if (lowerLog.contains("nullpointerexception")) {
			return "NullPointerException 分析";
		} else if (lowerLog.contains("beancreationexception")) {
			return "Spring BeanCreationException 分析";
		} else if (lowerLog.contains("sqlexception")) {
			return "SQLException 分析";
		} else if (lowerLog.contains("outofmemoryerror")) {
			return "OutOfMemoryError 分析";
		}
		// 取前 50 个字符作为标题
		String title = rawLog.substring(0, Math.min(50, rawLog.length())).trim();
		return title.replaceAll("\\s+", " ");
	}

	/**
	 * 根据日志内容推导错误分类（用于历史筛选）
	 */
	public String deriveErrorCategory(String sanitizedLog) {
		if (sanitizedLog == null) {
			return "CONFIG";
		}
		String lower = sanitizedLog.toLowerCase();
		if (lower.contains("nullpointerexception")) {
			return "NPE";
		}
		if (lower.contains("nosuchbeandefinitionexception") || lower.contains("required a bean") || lower.contains("beancreationexception")) {
			return "BEAN";
		}
		if (lower.contains("bindexception") || lower.contains("address already in use") || lower.contains("port 8080")) {
			return "PORT";
		}
		if (lower.contains("sqlexception") || lower.contains("sqlsyntaxerrorexception") || lower.contains("jdbc")) {
			return "SQL";
		}
		if (lower.contains("application.properties") || lower.contains("failed to bind properties") || lower.contains("could not resolve placeholder")) {
			return "CONFIG";
		}
		return "CONFIG";
	}

	/**
	 * 获取用户分析历史（支持搜索 q 和分类筛选 cat）
	 */
	public List<LogAnalysis> getHistory(Long userId, String q, String cat) {
		boolean hasQ = q != null && !q.trim().isEmpty();
		boolean hasCat = cat != null && !"ALL".equalsIgnoreCase(cat.trim());

		if (!hasQ && !hasCat) {
			return logAnalysisRepository.findByUserIdOrderByCreatedAtDesc(userId);
		}
		if (!hasQ && hasCat) {
			return logAnalysisRepository.findByUserIdAndErrorCategoryOrderByCreatedAtDesc(userId, cat.trim());
		}
		if (hasQ && !hasCat) {
			return logAnalysisRepository.searchByUserIdAndQueryOrderByCreatedAtDesc(userId, q.trim());
		}
		return logAnalysisRepository.searchByUserIdAndCategoryAndQueryOrderByCreatedAtDesc(userId, cat.trim(), q.trim());
	}

	/** @deprecated 使用 getHistory(userId, q, cat) */
	public List<LogAnalysis> getUserHistory(Long userId) {
		return logAnalysisRepository.findByUserIdOrderByCreatedAtDesc(userId);
	}

	/**
	 * 根据 ID 获取分析报告
	 */
	public Optional<LogAnalysis> getAnalysisById(Long id, Long userId) {
		Optional<LogAnalysis> analysis = logAnalysisRepository.findById(id);
		if (analysis.isPresent() && analysis.get().getUserId().equals(userId)) {
			return analysis;
		}
		return Optional.empty();
	}
}
