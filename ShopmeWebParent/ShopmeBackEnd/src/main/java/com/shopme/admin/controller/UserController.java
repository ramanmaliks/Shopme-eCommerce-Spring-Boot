package com.shopme.admin.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.export.UserCSVExporter;
import com.shopme.admin.export.UserExcelExporter;
import com.shopme.admin.export.UserPDFExporter;
import com.shopme.admin.fileUtil.FileUploadUtil;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserService service;

	/*
	 * @GetMapping("/users") public String listAll(Model model) { List<User>
	 * listUsers = service.listAll(); model.addAttribute("listUsers", listUsers);
	 * return "users"; }
	 */
	@GetMapping("/users")
	public String listFirstPage (Model model) {
		return listByPage(1, model,"firstName","asc",null);
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum")int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sirtDir") String sortDir,
			@Param("keyword") String keyword) {
		
		
		Page<User> page = service.listByPage(pageNum, sortField, sortDir,keyword );
		List<User> listUser = page.getContent();
		/*
		 * System.out.println("Sort Field:"+ sortField);
		 * System.ot.println("Sort ORder" + sortDir);
		 * System.out.println("Pagenum = "+ pageNum);
		 * System.out.println("Total Element = "+ page.getTotalElements());
		 * System.out.println("Total Pages= "+ page.getTotalPages());
		 */
		long startCount = (pageNum -1)* UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE -1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listUsers", listUser);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "users";
		
	}
	
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		User user = new User();
		List<Role> listRoles = service.listRoles();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,@RequestParam("image") MultipartFile multipartFile) throws IOException{
	//	System.out.println(user);
	//	System.out.println("Image" + multipartFile.getOriginalFilename());
		if (!multipartFile.isEmpty()) {
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		user.setPhotos(fileName);
		User savedUser = service.save(user);
		String uploadDir= "user-photos/"+ savedUser.getId();
		
		FileUploadUtil.cleanDir(uploadDir);
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
				service.save(user);
		}
		
			redirectAttributes.addFlashAttribute("message", "The user has been added successfully");
			
			return getRedirectURLtoAffectedUser(user);
	}

	private String getRedirectURLtoAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword="+ firstPartOfEmail;
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			User user = service.get(id);
			List<Role> listRoles = service.listRoles();

			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User (" + user.getEmail() + ")");
			model.addAttribute("listRoles", listRoles);

			return "user_form";
		} catch (UsernameNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/users";
		}

	}

	@GetMapping("users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			User savedUser = service.get(id);
			String uploadDir= "user-photos/"+ savedUser.getId();
	
			FileUploadUtil.cleanDir(uploadDir);
			service.delete(id);

			redirectAttributes.addFlashAttribute("message", "Id" + id + " successfully deleted");

		} catch (UsernameNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/users";
		}
		return "redirect:/users";
	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {
		service.updateUserEnableStatus(id, enabled);
		String status = enabled ? "enabled" : " diasbled" ;
		String message = " The user Id" + id  + " has been " + status;
		redirectAttributes.addFlashAttribute("message",message);
		return "redirect:/users";
	}

	@GetMapping("users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserCSVExporter exporter = new UserCSVExporter();
		exporter.export(listUsers, response);
		
	}
	@GetMapping("users/export/xls")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, response);
		
	}
	@GetMapping("users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserPDFExporter exporter = new UserPDFExporter();
		exporter.export(listUsers, response);
		
	}
}
