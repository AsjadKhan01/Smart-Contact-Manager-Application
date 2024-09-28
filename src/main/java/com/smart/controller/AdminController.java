package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.entities.User;
import com.smart.repo.UserRepository;
import com.smart.repo.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserService userService;

	@ModelAttribute
	public void commonData(Model model, Principal principal) {
		String name = principal.getName();
		User userByUserName = this.userRepository.getUserByUserName(name);
		
		long countActiveUser = this.userService.getCountOfActiveUser();
		long countInActiveUser = this.userService.getCountOfInActiveUser();	
	    long countAllUser = this.userRepository.count();
	    
	    model.addAttribute("countAllUser", countAllUser);
		model.addAttribute("userByUserName", userByUserName);
		model.addAttribute("countActiveUser", countActiveUser);
		model.addAttribute("countInActiveUser", countInActiveUser);
	}
	
	@GetMapping("/admin-panel")
	public String showAdminPanel(Principal principal, Model model) {
		String name = principal.getName();
		User userByUserName = this.userRepository.getUserByUserName(name);
		model.addAttribute("name", name);
		model.addAttribute("user", userByUserName);
		model.addAttribute("title", "Admin Panel");
		return "/admin/admin-panel";
	}
	@GetMapping("/users")
	public String showUsers(Model model){
		List<User> users = this.userRepository.findAll();
		model.addAttribute("users", users);
		model.addAttribute("title", "All Users");
		return "/admin/usersList";
	}
	
	@GetMapping("/inActiveUsers")
	public String showInActiveUsers(Model model){
		List<User> users = this.userRepository.getInActiveUsers();
		model.addAttribute("inActiveUsers", users);
		model.addAttribute("title", "Inactive Users");
		return "/admin/inActiveUsersList";
	}
	
	@GetMapping("/admin-profile")
	public String viewAdminProfile(Model model) {
		model.addAttribute("title", "Admin Profile");
		return "/admin/admin_profile";
	}
}
