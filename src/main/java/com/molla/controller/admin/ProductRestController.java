package com.molla.controller.admin;

import com.molla.service.ProductService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/products")
public class ProductRestController {

	private final ProductService service;

	public ProductRestController(ProductService service) {
		this.service = service;
	}

	@PostMapping("/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
		return service.checkUnique(id, name);
	}	
}