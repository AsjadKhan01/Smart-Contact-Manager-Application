package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.User;
import com.smart.helper.SaveImgDB;
import com.smart.helper.SmartMessage;
import com.smart.repo.ContactService;
import com.smart.repo.EmailSender;
import com.smart.repo.UserRepository;
import com.smart.repo.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ViewController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private ContactService contactService;
	@Autowired
	private EmailSender emailSender;

	@ModelAttribute
	public void commonData(Model m, Principal principal) throws NullPointerException {
		try {
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		String role = user.getRole();

		m.addAttribute("user", user);

		if (role.equalsIgnoreCase("ADMIN")) {
			m.addAttribute("role", role);
		}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	@GetMapping("/")
	public String AllPage() throws NullPointerException{
		return "redirect:/home";
	}

	@GetMapping("/home")
	public String homePage(Model m) throws NullPointerException {
		try {
		m.addAttribute("title", "Home - SmartContactManager");
		}catch (Exception e) {
			// TODO: handle exception
		}
		return "home";
	}

	@GetMapping("/about")
	public String aboutPage(Model m) {
		m.addAttribute("title", "About - SmartContactManager");
		return "about";
	}

	@GetMapping("/signup")
	public String signupPage(Model m) {
		m.addAttribute("title", "SignUp - SmartContactManager");
		m.addAttribute("userObject", new User());
		return "signup";
	}

	@GetMapping("/signin")
	public String customLogin(Model m, HttpSession session) {
		m.addAttribute("title", "Login - SmartContactManager");
		session.setAttribute("message", new SmartMessage("Registered ", "alert-success"));
		return "login";
	}

	@PostMapping("/do_register")
	public String signUpAccepter(@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("about") String about,
			@RequestParam("imageUrl") MultipartFile file, @RequestParam("OTP") String OTP,
			@RequestParam(value = "agreement", defaultValue = "false") boolean tc, Model model, HttpSession session) {

		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setPassword(password);
		user.setAbout(about);
		user.setImageUrl(file.getOriginalFilename());
		user.setEnabled(true);
		user.setRole("NORMAL");
		
		boolean isEmailSucc = this.emailSender.sendEmail(email, "SingUp Verification", "01122");
		
		
		try {
			if(isEmailSucc) {
				System.out.println(OTP);
			}
			
			this.contactService.register(tc, file, user, model, session);

			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("userObject", user);
			session.setAttribute("message",
					new SmartMessage("Something went wrong!! " + e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
}
