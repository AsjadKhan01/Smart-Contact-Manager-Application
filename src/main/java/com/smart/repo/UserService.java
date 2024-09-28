package com.smart.repo;

import java.security.Principal;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.SmartMessage;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	User user;
	
	public long getCountOfActiveUser() {
	    return userRepository.countActiveUser();
	}
	public long getCountOfInActiveUser() {
	    return userRepository.countInActiveUser();
	}
	
	@Transactional
	public void changeUserPassword(int id, String oldPass, String newPass, String confPass,String userPassword ,HttpSession session ) 
	{
		if (!oldPass.equals(userPassword)) {
			session.setAttribute("message", new SmartMessage("Old Password Incorrect", "alert-warning"));
		} else if (!newPass.equals(confPass)) {
			session.setAttribute("message", new SmartMessage("Password Does't Match!!", "alert-warning"));
		} else {
			// changing password
			this.userRepository.updateUserPassword(id, userPassword);

			session.setAttribute("message", new SmartMessage("Changed Password Successfully!!", "alert-success"));

		}
	    this.userRepository.updateUserPassword(id, newPass);	
	}
	
	
	public String findByEmail(String email) {
		 user = userRepository.findByEmail(email);
		if(user != null) {
			return user.getPassword();
		}else{
			throw new RuntimeException("User not found with this email "+email);
		}
	}

	public boolean verifyPass(String pass) {
		if(user.getPassword().equals(pass)) {
			return true;
		}
		return false;
	}
	
	@Transactional
	public void deleteAccount(Model model, HttpSession session,String email, String password) {
		
		User userByEmail = this.userRepository.findByEmail(email);
		String varificEmail = userByEmail.getEmail();
		String varificPassword = userByEmail.getPassword();
		int id = userByEmail.getId();
		
		if(!varificEmail.equals(email)) {
			session.setAttribute("message", new SmartMessage("Inavlid Email!!", "alert-warning"));

		}else if(!varificPassword.equals(password)) {
			session.setAttribute("message", new SmartMessage("Password Does't Match!!", "alert-warning"));
		}else {
			
			session.setAttribute("message", new SmartMessage("Succefully delete !", "alert-success"));
		}
		this.userRepository.deleteUserAsDisable(id, false);
	}
}
