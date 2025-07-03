package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.User;
import com.smart.helper.OtpGenerater;
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
	@Autowired
	private PasswordEncoder passwordEncoder;

	@ModelAttribute
	public void commonData(Model m, Principal principal) throws NullPointerException {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			String role = user.getRole();
			m.addAttribute("userByUserName", user);
			m.addAttribute("user", user);

			if (role.equalsIgnoreCase("ADMIN")) {
				m.addAttribute("role", role);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Default open home API
	@GetMapping("/")
	public String AllPage() throws NullPointerException {
		return "redirect:/home";
	}

	// Open Home API
	@GetMapping("/home")
	public String homePage(Model m) throws NullPointerException {
		try {
			m.addAttribute("title", "Home - SmartContactManager");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "home";
	}

	// Open About page api
	@GetMapping("/about")
	public String aboutPage(Model m) throws NullPointerException {
		m.addAttribute("title", "About - SmartContactManager");
		return "about";
	}


	// OTP generate by click button
	@GetMapping("/otp-gen")
	public String Otp(Model m, HttpSession httpSession, Principal principal) {
		m.addAttribute("title", "Verify OTP - SmartContactManager");
		this.userService.otpGenService(httpSession, m, principal);
		return "normal/resetPassword";
	}

	// Forget password(recover by OTP) API
	@PostMapping("/resetPasswordForm")
	public String ResetOtpForm(@RequestParam("resetOtp") String resetOtp,
			@RequestParam("password1") String userPassword, @RequestParam("confPassword") String confPass, Model m,
			HttpSession httpSession, Principal principal) {
		m.addAttribute("title", "ResetPassword - SmartContactManager");
		this.userService.resetUserPassword(resetOtp, userPassword, confPass, httpSession, principal);
		return "normal/resetPassword";
	}
	
	// Open signin or Login page
		@GetMapping("/signin")
		public String customLogin(Model m, HttpSession session) {
			m.addAttribute("title", "Login - SmartContactManager");
		//	session.setAttribute("message", new SmartMessage("Registered ", "alert-success"));
			return "login";
		}
		

	// Open signUp or Registration API
	@GetMapping("/signup")
	public String signupPage(Model m) {
		m.addAttribute("title", "SignUp - SmartContactManager");
		m.addAttribute("userObject", new User());
		return "signup";
	}

	// Registration API
	@PostMapping("/do_register")
	public String signUpAccepter(@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("about") String about,
			@RequestParam("imageUrl") MultipartFile file,
			@RequestParam(value = "agreement", defaultValue = "false") boolean tc, Model model, HttpSession session) {

		User user = new User();
		user.setName(name);
		
		//Set user Admin OR Normal
		try {
			String substring = name.substring(name.length()-6, name.length());
			if(substring.equalsIgnoreCase("@admin")) 
				user.setRole("ADMIN");	
			else
			user.setRole("NORMAL");
		} catch (Exception e) {
			// TODO: handle exception
			user.setRole("NORMAL");
		}

		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setAbout(about);
		user.setImageUrl(file.getOriginalFilename());
		user.setEnabled(true);
		
		try {

			boolean existsByEmail = this.userRepository.existsByEmail(email);
			if (!existsByEmail) {
				model.addAttribute("userObject", user);
				if(name.length()>8) 
				this.userService.register(tc, file, user, model, session);
				else
				session.setAttribute("message", new SmartMessage("UserName Must be Minimum 8 Character ! ", "alert-danger"));
					
				// sending email of registration message
				this.emailSender.sendEmail("asjad01122@gmail.com", "Smart Contact Manager App",
						"A User(" + name + ")is Signed Up on your Contact Manager Application");
			} else{
				model.addAttribute("userObject", user);
				session.setAttribute("message", new SmartMessage("This Email Has been Used ! ", "alert-danger"));

			}

			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("userObject", user);
			
			  session.setAttribute("message", new SmartMessage("Something went wrong!! " +
			  e.getMessage(), "alert-danger"));
			 
			return "signup";
		}
	}
}
