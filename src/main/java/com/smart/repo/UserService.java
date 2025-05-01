package com.smart.repo;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.User;
import com.smart.helper.OtpGenerater;
import com.smart.helper.SaveImgDB;
import com.smart.helper.SmartMessage;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OtpGenerater otpGenerater;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private SaveImgDB saveImgDB;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	private String setOtp = "hello";
	User user;

	public long getCountOfActiveUser() {
		return userRepository.countActiveUser();
	}

	public long getCountOfInActiveUser() {
		return userRepository.countInActiveUser();
	}

	@Transactional
	public void changeUserPassword(int id, String oldPass, String newPass, String confPass, String userPassword,
			HttpSession session) {
		if (!passwordEncoder.matches(oldPass,userPassword)) {
			session.setAttribute("message", new SmartMessage("Old Password Incorrect", "alert-warning"));
		} else if (!newPass.equals(confPass)) {
			session.setAttribute("message", new SmartMessage("Password Does't Match!!", "alert-warning"));
		} else {
			// changing password
			this.userRepository.updateUserPassword(id, userPassword);
			session.setAttribute("message", new SmartMessage("Changed Password Successfully!!", "alert-success"));
			this.userRepository.updateUserPassword(id, passwordEncoder.encode(newPass));
		}
	}

	public String otpGenService(HttpSession httpSession, Model m, Principal principal) {

		setOtp = this.otpGenerater.setOtp();
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);

		// send otp to user through email
		boolean sendEmail = this.emailSender.sendEmail(user.getEmail(), "Reset OTP ", setOtp);

		if (sendEmail) {
			httpSession.setAttribute("message",
					new SmartMessage("Otp sented you email " + user.getEmail(), "alert-success"));
		} else {
			httpSession.setAttribute("message",
					new SmartMessage("Check Your Internet Connect or something else !!", "alert-danger"));
		}
		return setOtp;
	}

	@Transactional
	public boolean resetUserPassword(String resetOtp, String userPassword, String confPass, HttpSession httpSession,
			Principal principal) {

		String name = principal.getName();
		User userByUserName = this.userRepository.getUserByUserName(name);
		int id = userByUserName.getId();

		if (setOtp.equals("hello")) {
			httpSession.setAttribute("message", new SmartMessage("Please send Otp First!!", "alert-warning"));

		} else {
			if (setOtp.equals(resetOtp)) {
				// changing password logic
				if (userPassword.equals(confPass)) {

					// change password method
					try {
						this.userRepository.updateUserPassword(id, passwordEncoder.encode(confPass));
						httpSession.setAttribute("message",
								new SmartMessage("Password Changed Successfully", "alert-success"));
					} catch (Exception e) {
						// TODO: handle exception
						httpSession.setAttribute("message",
								new SmartMessage("Something Wrong in Backend !", "alert-danger"));
					}
				} else {
					httpSession.setAttribute("message",
							new SmartMessage("Password is miss macthed !!", "alert-danger"));
				}
			} else {
				httpSession.setAttribute("message", new SmartMessage("your otp is miss matched !", "alert-danger"));
			}
		}

		return true;
	}

	public String findByEmail(String email) {
		user = userRepository.findByEmail(email);
		if (user != null) {
			return user.getPassword();
		} else {
			throw new RuntimeException("User not found with this email " + email);
		}
	}

	public boolean verifyPass(String pass) {
		if (user.getPassword().equals(pass)) {
			return true;
		}
		return false;
	}

	@Transactional
	public void deleteAccount(Model model, HttpSession session, String email, String password) {

		User userByEmail = this.userRepository.findByEmail(email);
		String varificEmail = userByEmail.getEmail();
		String varificPassword = userByEmail.getPassword();
		int id = userByEmail.getId();

		if (!varificEmail.equals(email)) {
			session.setAttribute("message", new SmartMessage("Inavlid Email!!", "alert-warning"));

		} else if (!passwordEncoder.matches(password , varificPassword)){
			session.setAttribute("message", new SmartMessage("Password Does't Match!!", "alert-warning"));
		} else {

			session.setAttribute("message", new SmartMessage("Succefully delete !", "alert-success"));
		}
		this.userRepository.deleteUserAsDisable(id, false);
	}

	// Registration User or Sign up
	public void register(boolean tc, MultipartFile file, User user, Model model, HttpSession session) throws Exception {
		if (!tc) {
			System.out.println("Please check terms & condition!!");
			throw new Exception("Please check terms & condition");
		}
		if (file.isEmpty()) {
			user.setImageUrl("defaultUser.jpg");
		}
		try {
			byte[] saveImgInDB = this.saveImgDB.saveImgInDB(file);
			user.setImage(saveImgInDB);
			this.userRepository.save(user);
			session.setAttribute("message", new SmartMessage("Contact Added Successfully.", "alert-success"));
		} catch (Exception e) {
			session.setAttribute("message", new SmartMessage("Something Went Wrong!!", "alert-danger"));
		}
		this.userRepository.save(user);
		model.addAttribute("userObject", new User());
		session.setAttribute("message", new SmartMessage("Successfully Register", "alert-success"));
	}

	// Edit Profile of User
	public void editProfile(String enteredPassword, String changeName, String changeEmail, String changeAbout,
			MultipartFile file, Model model, HttpSession httpSession, Principal principal) throws Exception {
		
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		int id = user.getId();
		String password = user.getPassword();
		String role = user.getRole();
		boolean enable = user.isEnabled();
		String OldImageUrl = user.getImageUrl();
		
		if(!passwordEncoder.matches(enteredPassword , password)) {
			httpSession.setAttribute("message", new SmartMessage("Password is Incorrect", "alert-danger"));
		}
		
		User userObj = new User();
		userObj.setId(id);
		userObj.setName(changeName);
		userObj.setEmail(changeEmail);
		userObj.setAbout(changeAbout);
		userObj.setImageUrl(file.getOriginalFilename());
		userObj.setEnabled(enable);
		userObj.setRole(role);
		userObj.setPassword(password);
		
		if (file.isEmpty()) {
			//set previous image 
			userObj.setImageUrl(OldImageUrl);
		}

		try {
			byte[] saveImgInDB = this.saveImgDB.saveImgInDB(file);
			userObj.setImage(saveImgInDB);
			this.userRepository.save(userObj);
			httpSession.setAttribute("message", new SmartMessage("save Successfully.", "alert-success"));
		} catch (Exception e) {
			httpSession.setAttribute("message", new SmartMessage("Something Went Wrong!!", "alert-danger"));
		}
		this.userRepository.save(userObj);
		model.addAttribute("userObject", new User());
		httpSession.setAttribute("message", new SmartMessage("Successfully Update", "alert-success"));
	}
}
