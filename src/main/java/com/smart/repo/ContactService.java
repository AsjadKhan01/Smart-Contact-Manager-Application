package com.smart.repo;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.SaveImgDB;
import com.smart.helper.SmartMessage;

import jakarta.servlet.http.HttpSession;

@Service
public class ContactService {

	@Autowired
	ContactRepository contactRepository;
	@Autowired
	UserRepository  userRepository;
	@Autowired
	private SaveImgDB saveImgDB;
	
	public Page<Contact> getActiveContactsByUser(int userId, Pageable pageable) {
	    return contactRepository.getActiveContactsByUser(userId, pageable);
	}
	
	public List<Contact> getInActiveContactsByUser(int userId) {
	    return contactRepository.getInActiveContactsByUser(userId);
	}
	
	public long getCountOfActiveContactsByUser(int userId) {
	    return contactRepository.countActiveContactsByUser(userId);
	}
	public long getCountOfInActiveContactsByUser(int userId) {
	    return contactRepository.countInActiveContactsByUser(userId);
	}

	@Transactional
	public void updateContactStatus(Integer contactId, String status) {
		contactRepository.updateStatusById(contactId, status);
	}
	
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
}
