package com.smart.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	// pagination...
	// currentPage
	// contact per page = 5
	@Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND c.status = 'Active'")
	public Page<Contact> getActiveContactsByUser(@Param("userId") int userId, Pageable pageable);
	
	//get recycle contacts
	@Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND c.status = 'InActive'")
	public List<Contact> getInActiveContactsByUser(@Param("userId") int userId);

	// contacts number
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.user.id = :userId AND c.status = 'Active'")
    long countActiveContactsByUser(@Param("userId") int userId);
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.user.id = :userId AND c.status = 'InActive'")
    long countInActiveContactsByUser(@Param("userId") int userId);

	// search
	public List<Contact> findByNameContainingAndUserAndStatus(String name, User user,String status);

	// delete as inactive contact
	@Modifying
	@Query("UPDATE Contact c SET c.status = :status WHERE c.cId = :cId")
	void updateStatusById(@Param("cId") Integer cId, @Param("status") String status);

}
