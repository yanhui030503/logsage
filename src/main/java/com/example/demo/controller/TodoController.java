package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Todo;

@Controller
public class TodoController {

	// 内存存储 todo 列表
	private List<Todo> todos = new ArrayList<>();
	// 用于生成唯一 ID
	private AtomicLong idGenerator = new AtomicLong(1);

	@GetMapping("/todos")
	public String todos(Model model) {
		model.addAttribute("todos", todos);
		return "todos";
	}

	@PostMapping("/todos/add")
	public String addTodo(@RequestParam String text) {
		if (text != null && !text.trim().isEmpty()) {
			Todo todo = new Todo(idGenerator.getAndIncrement(), text.trim());
			todos.add(todo);
		}
		return "redirect:/todos";
	}

	@PostMapping("/todos/delete")
	public String deleteTodo(@RequestParam Long id) {
		todos.removeIf(todo -> todo.getId().equals(id));
		return "redirect:/todos";
	}
}
