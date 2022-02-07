package com.molla.controller.admin;

import com.molla.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/users")
public class UserRestController {
	
	private final UserService service;

	public UserRestController(UserService service) {
		this.service = service;
	}

	@PostMapping("/check_email")
	public String checkDuplicateEmail(@Param("id") Integer id,
									  @RequestParam("email") String email) {
		log.debug("UserRestController | Check duplicate email: " + email);

		String result = service.isEmailUnique(id, email) ? "OK" : "Duplicated";
		log.debug("UserRestController | Check duplicate email | email: " + email + " is: " + result);

		return result;
	}
}
