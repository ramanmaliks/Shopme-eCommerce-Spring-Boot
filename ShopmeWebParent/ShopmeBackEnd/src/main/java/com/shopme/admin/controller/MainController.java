package com.shopme.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.common.entity.User;

@Controller
public class MainController {
	@GetMapping ("")
	public String viewHomePage(){
		return"index";
	 }
	
@GetMapping("/login")
public String viewLoginPage() {
	return "login";
}
}
