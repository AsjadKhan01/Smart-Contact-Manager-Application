package com.smart.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.SaveImgDB;
import com.smart.helper.SmartMessage;
import com.smart.repo.ContactRepository;
import com.smart.repo.ContactService;
import com.smart.repo.UserRepository;
import com.smart.repo.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	ContactRepository contactRepository;
	@Autowired
	ContactService contactService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserService userService;
	@Autowired
	SaveImgDB saveImgDB;
	@Autowired
	PasswordEncoder passwordEncoder;

	@ModelAttribute
	public void commonDataMethod(Model model, Principal principal) throws NullPointerException{
		String name = principal.getName();
		User userByUserName = this.userRepository.getUserByUserName(name);
		int userId = userByUserName.getId();
		long countActiveContacts = this.contactRepository.countActiveContactsByUser(userId);
		long countInActiveContacts = this.contactRepository.countInActiveContactsByUser(userId);
		model.addAttribute("userByUserName", userByUserName);
		model.addAttribute("countActiveContacts", countActiveContacts);
		model.addAttribute("countInActiveContacts", countInActiveContacts);
		model.addAttribute("hide_email", userByUserName.getEmail());

	}

	@GetMapping("/add-contact")
	public String AddConatct(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "/normal/add_contact";
	}

	// show user profile
	@GetMapping("/user-profile")
	public String profile(Principal principal, Model model) {

		String loginUserEmail = principal.getName();
		User user = this.userRepository.getUserByUserName(loginUserEmail);
		model.addAttribute("user", user);
		model.addAttribute("title", "Profile");
		return "normal/user_profile";
	}

	@GetMapping("/user_setting")
	public String setting(Model model) {
		model.addAttribute("title", "Setting");
		return "normal/user_setting";
	}

	@GetMapping("/openResetPassword")
	public String openResetPage() {
		return "normal/resetPassword";
	}

	@GetMapping("/openEditPage")
	public String openEditPage() {
		return "normal/change_about&email";
	}

	@PostMapping("/editProfile_form")
	public String editProfile(@RequestParam("changeName") String changeName,
			@RequestParam("changeEmail") String changeEmail, @RequestParam("changeAbout") String changeAbout,
			@RequestParam("enteredPassword") String enteredPassword, @RequestParam("imageUrl") MultipartFile file,
			Principal principal, HttpSession httpSession, Model model) throws Exception {

		this.userService.editProfile(enteredPassword, changeName, changeEmail, changeAbout, file, model, httpSession,
				principal);

		return "normal/change_about&email";

	}

	// change user password
	@PostMapping("/do_change")
	public String changePassword(@RequestParam("oldPassword") String oldPass,
			@RequestParam("newPassword") String newPass, @RequestParam("confirmPassword") String confPass,
			Principal principal, Model model, HttpSession session) {

		String name = principal.getName();
		String userPassword = this.userRepository.getUserByUserName(name).getPassword();
		Integer id = this.userRepository.getUserByUserName(name).getId();

		passwordEncoder.encode(userPassword);
		this.userService.changeUserPassword(id, oldPass, newPass, confPass, userPassword, session);
		return "normal/user_setting";
	}

	@PostMapping("/do_delete_user")
	public String deleteUser(@RequestParam("email") String email, @RequestParam("password") String password,
			Principal principal, Model model, HttpSession session) {
		this.userService.deleteAccount(model, session, email, password);
		return "normal/user_setting";
	}
}
