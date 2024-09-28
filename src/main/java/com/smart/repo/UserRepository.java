package com.smart.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.User;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer>{

	@Query("select u from User u where u.email = :email")
	public User getUserByUserName(@Param("email") String email);
	
	@Query("select u from User u where u.enabled = false")
	public List<User> getInActiveUsers();
	
	public User findByEmail(String email);
	
	public User findByName(String name);
	
	//count users
	@Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countActiveUser();
	@Query("SELECT COUNT(u) FROM User u WHERE u.enabled = false")
    long countInActiveUser();
	
	//update Password
	@Modifying
	@Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
	void updateUserPassword(@Param("id") Integer id ,@Param("password") String newPassword);
	
	//delete user as inactive
	@Modifying
	@Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :id")
	void deleteUserAsDisable(@Param("id") Integer id, @Param("enabled") boolean b );
}
