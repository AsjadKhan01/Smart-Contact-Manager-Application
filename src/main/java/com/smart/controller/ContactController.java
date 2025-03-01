package com.smart.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class ContactController {

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

	@ModelAttribute
	public void commonDataMethod(Model model, Principal principal) {
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

	@GetMapping("/dashboard")
	public String dashboard(Model model, Principal principal) {
		String userName = principal.getName();
		if (userName.isEmpty()) {
			return "/login";
		}
		User userByUserName = this.userRepository.getUserByUserName(userName);
		int userId = userByUserName.getId();
		List<Contact> inActiveContacts = this.contactService.getInActiveContactsByUser(userId);
		model.addAttribute("inActiveContacts", inActiveContacts);
		model.addAttribute("title", "Dashboard");
		return "/normal/user_dashboard";
	}

	// contact table show
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {

		String userName = principal.getName();
		User userByUserName = this.userRepository.getUserByUserName(userName);
		int userId = userByUserName.getId();
		long countActiveContacts = this.contactRepository.countActiveContactsByUser(userId);
		// show contact page
		// per page =5[n]
		// current page= 0[page]
		Pageable pageable = PageRequest.of(page, 4);
		Page<Contact> contact = this.contactRepository.getActiveContactsByUser(userId, pageable);

		model.addAttribute("contact", contact);
		model.addAttribute("countActiveContacts", countActiveContacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contact.getTotalPages());
		model.addAttribute("title", "Show Contacts");

		return "/normal/show_contacts";
	}

	@PostMapping("/process-contact")
	public String SubmitContact(@RequestParam("name") String name, @RequestParam("nickName") String nick,
			@RequestParam("email") String mail, @RequestParam("phone") String phone, @RequestParam("work") String work,
			@RequestParam("description") String desc, @RequestParam("imageUrl") MultipartFile file, Principal principal,
			Model model, HttpSession session) {

		String currentUserName = principal.getName();
		User user = userRepository.getUserByUserName(currentUserName);

		Contact contact = new Contact();
		contact.setName(name);
		contact.setNickName(nick);
		contact.setEmail(mail);
		contact.setPhone(phone);
		contact.setWork(work);
		contact.setDescription(desc);
		contact.setImageUrl(file.getOriginalFilename());
		contact.setUser(user);
		contact.setStatus("Active");

		if (file.isEmpty()) {
			contact.setImageUrl("defaultUser.jpg");
		}
		try {
			byte[] saveImgInDB = this.saveImgDB.saveImgInDB(file);
			contact.setImage(saveImgInDB);
			this.contactRepository.save(contact);

			session.setAttribute("message", new SmartMessage("Contact Added Successfully.", "alert-success"));
		} catch (Exception e) {
			System.out.println("Message : " + e.getMessage());
			session.setAttribute("message", new SmartMessage("Something Went Wrong!!", "alert-danger"));
		}
		model.addAttribute("contact", contact);
		return "/normal/add_contact";
	}

	// Shoe One Contact Detail
	@GetMapping("/{cId}/show-contacts")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		Optional<Contact> findById = this.contactRepository.findById(cId);
		Contact contact = findById.get();

		// security code for unAthorized user
		String name = principal.getName();
		int id = contact.getUser().getId();
		int findIdByEmail = this.userRepository.findByEmail(name).getId();

		if (id == findIdByEmail) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName() + " View");
		}
		return "/normal/show_contact_detail";
	}

	// Delete Contact Api...
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, HttpSession session, Principal principal) {

		Optional<Contact> findById = this.contactRepository.findById(cId);
		Contact contact = findById.get();
		String name = principal.getName();
		int id = contact.getUser().getId();
		int findIdByEmail = this.userRepository.findByEmail(name).getId();
		if (id == findIdByEmail) {

			this.contactService.updateContactStatus(cId, "InActive");

			session.setAttribute("message",
					new SmartMessage("Your Contact has been Deleted Successfully!!", "alert-warning"));
		}
		return "redirect:/user/show-contacts/0";
	}

	// recyle contact
	@GetMapping("/recylce/{cId}")
	public String recyleContact(@PathVariable("cId") Integer cId, HttpSession httpSession, Principal principal) {

		Optional<Contact> findById = this.contactRepository.findById(cId);
		Contact contact = findById.get();

		String name = principal.getName();
		int id = contact.getUser().getId();
		int findIdByEmail = this.userRepository.findByEmail(name).getId();

		if (id == findIdByEmail) {
			this.contactService.updateContactStatus(cId, "Active");
			httpSession.setAttribute("message",
					new SmartMessage("Your Contact has Recyle Successfully!!", "alert-success"));
		}
		return "redirect:/user/dashboard";
	}

	@GetMapping("/prDeleteCont/{cId}")
	public String ParmanentDeleteContact(@PathVariable("cId") long cid, HttpSession httpSession, Principal principal,
			Model model) {

		Optional<Contact> findById = this.contactRepository.findById((int) cid);
		Contact contact = findById.get();

		String name = principal.getName();
		int id = contact.getUser().getId();
		int findIdByEmail = this.userRepository.findByEmail(name).getId();

		if (id == findIdByEmail) {
			this.contactRepository.deleteById((int) cid);
			httpSession.setAttribute("message",
					new SmartMessage("Your Contact has Recyle Successfully!!", "alert-success"));
		}
		return "redirect:/user/dashboard";
	}

	// open update contact form handler
	@PostMapping("/open-update/{cid}")
	public String openUpdateForm(@PathVariable("cid") Integer cid, Model model) {

		Contact contact = this.contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);
		model.addAttribute("title", "Update Contact");
		return "/normal/update_contact";
	}

	// update contact form handler
	@PostMapping("/process-update-contact")
	public String updateForm(@RequestParam("cId") Integer cId, @RequestParam("name") String name,
			@RequestParam("nickName") String nick, @RequestParam("email") String mail,
			@RequestParam("phone") String phone, @RequestParam("work") String work,
			@RequestParam("description") String desc, @RequestParam("imageUrl") MultipartFile file, Principal principal,
			Model model, HttpSession session) {

		String loginUser = principal.getName();
		User user = this.userRepository.getUserByUserName(loginUser);

		Contact contact = new Contact();
		contact.setcId(cId);
		contact.setName(name);
		contact.setNickName(nick);
		contact.setEmail(mail);
		contact.setPhone(phone);
		contact.setWork(work);
		contact.setDescription(desc);
		contact.setImageUrl(file.getOriginalFilename());
		contact.setUser(user);
		contact.setStatus("Active");

		if (file.isEmpty()) {
			contact.setImageUrl("defaultUser.jpg");
		}
		try {
			byte[] saveImgInDB = this.saveImgDB.saveImgInDB(file);
			contact.setImage(saveImgInDB);
			this.contactRepository.save(contact);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new SmartMessage("Something Went Wrong!!", "alert-danger"));
		}
		model.addAttribute("contact", contact);
		return "redirect:/user/" + cId + "/show-contacts";
	}

}
