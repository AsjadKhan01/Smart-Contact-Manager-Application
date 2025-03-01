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
	UserRepository userRepository;

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
}
