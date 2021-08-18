package com.shopme.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.user.RoleRepository;
import com.shopme.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

	@Autowired
	private RoleRepository repo;
	
	@Test
	public void testCreateFirstRole(){
		Role roleAdmin = new Role("Admin","manage everything");
		Role savedRole = repo.save(roleAdmin);
		assertThat(savedRole.getId()).isGreaterThan(0);
		
	}
	@Test
	public void testCreateRestRole(){
		Role roleSalesperson = new Role("Salesperson","manage product price, customer, shipping, orders and sales rpeort");

		Role roleEditor = new Role("Editor","manage categories, brands, products, articles and menus");

		Role roleShipper = new Role("Shipper","view products, view orders, update order status");

		Role roleAssistant = new Role("Assistant","manage quetions and review");

		repo.saveAll(List.of(roleSalesperson, roleEditor, roleShipper,roleAssistant));

	}

}
