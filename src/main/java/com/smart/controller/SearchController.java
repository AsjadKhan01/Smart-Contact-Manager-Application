package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.repo.ContactRepository;
import com.smart.repo.UserRepository;

@RestController
public class SearchController {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	ContactRepository contactRepository;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		
		System.err.println("controller: "+query);
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		
		List<Contact> contact = this.contactRepository.findByNameContainingAndUserAndStatus(query, user, "Active");
		
		return ResponseEntity.ok(contact);
	}
}
