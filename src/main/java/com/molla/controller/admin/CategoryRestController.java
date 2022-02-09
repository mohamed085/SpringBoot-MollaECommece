package com.molla.controller.admin;

import com.molla.service.CategoryService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/categories")
public class CategoryRestController {

	private final CategoryService service;

	public CategoryRestController(CategoryService service) {
		this.service = service;
	}

	@PostMapping("/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name,
							  @Param("alias") String alias) {
		return service.checkUnique(id, name, alias);
	}

}
