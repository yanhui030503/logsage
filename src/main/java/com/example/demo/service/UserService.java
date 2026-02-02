package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户服务类
 */
@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/**
	 * 注册新用户
	 */
	public User register(String email, String password) {
		// 检查邮箱是否已存在
		if (userRepository.findByEmail(email).isPresent()) {
			throw new RuntimeException("该邮箱已被注册");
		}

		// 创建新用户并加密密码
		User user = new User(email, passwordEncoder.encode(password));
		return userRepository.save(user);
	}

	/**
	 * 根据邮箱查找用户
	 */
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/**
	 * 验证密码
	 */
	public boolean verifyPassword(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	/**
	 * 更新用户默认设置（分析页偏好）
	 */
	public User updateUserSettings(Long userId, String defaultType, Boolean defaultSanitize, String defaultDepth) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("用户不存在"));
		if (defaultType != null && (defaultType.equals("JAVA") || defaultType.equals("SPRING"))) {
			user.setDefaultType(defaultType);
		}
		if (defaultSanitize != null) {
			user.setDefaultSanitize(defaultSanitize);
		}
		if (defaultDepth != null && (defaultDepth.equals("FAST") || defaultDepth.equals("DEEP"))) {
			user.setDefaultDepth(defaultDepth);
		}
		return userRepository.save(user);
	}
}
