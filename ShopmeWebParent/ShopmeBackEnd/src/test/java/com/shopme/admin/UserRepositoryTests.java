package com.shopme.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	public UserRepository repo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateNewUserWithOneRoles() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userNamHM = new User("name@codejava.net", "password", "Raman", "Malik");
		userNamHM.addRole(roleAdmin);
		User savedUser = repo.save(userNamHM);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateNewUserWithTwoRoles() {
		User userRavi = new User("raman@gmail.com", "password", "Raman", "Malik");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);

		userRavi.addRole(roleEditor);
		userRavi.addRole(roleAssistant);
		User savedUser = repo.save(userRavi);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllUsers() {
		Iterable<User> listUser = repo.findAll();
		listUser.forEach(user -> System.out.println(user));

	}

	@Test
	public void testGetUserById() {
		User userNam = repo.findById(1).get();
		System.out.println(userNam);
		assertThat(userNam).isNotNull();
	}

	@Test
	public void TestUpdateUserDetails() {
		User userNam = repo.findById(1).get();
		userNam.setEnabled(true);
		repo.save(userNam);
	}

	@Test
	public void TestUpdateUserRole() {
		User userRavi = repo.findById(2).get();
		Role roleEditor = new Role(2);
		Role roleSalesperson = new Role(4);
		userRavi.getRoles().remove(roleEditor);
		userRavi.addRole(roleSalesperson);
		repo.save(userRavi);

	}
	
	@Test
	public void testDeleteUser(){
	Integer userId=3;
	repo.deleteById(userId);
	}

	@Test
	public void testCounById(){
	Integer id = 1;
	Long countById = repo.countById(id);
	assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testGetUserByEmail(){
		String email ="malik@raman";
		User user = repo.getUserByEmail(email);
		assertThat(user).isNotNull();
		
		}

}

