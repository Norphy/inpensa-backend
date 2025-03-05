package com.orphy.inpensa_backend.v1.web.controller;

import com.orphy.inpensa_backend.v1.model.Category;
import com.orphy.inpensa_backend.v1.model.dto.CategoryDto;
import com.orphy.inpensa_backend.v1.service.CategoryService;
import com.orphy.inpensa_backend.v1.util.Util;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //TODO authorize only admin
    @GetMapping("/admin/all")
    public List<Category> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/admin/user/{userId}")
    public List<Category> getAllByUser(@PathVariable String userId) {
        return categoryService.getCategoriesByUser(userId);
    }

    @GetMapping
    public List<Category> getAllByCurrentUser(@Parameter(hidden = true) SecurityContext securityContext) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) securityContext.getAuthentication();
        String userId = jwtAuthenticationToken.getName();
        return categoryService.getCategoriesByUser(userId);
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable String id) {
        UUID uuid = Util.tryOrElse(() -> UUID.fromString(id),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        return categoryService.getCategoryById(uuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createCategory(@RequestBody CategoryDto categoryDto) {
        UUID uuid = categoryService.saveCategory(categoryDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(uuid).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/admin/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createCategoryForUser(@RequestBody CategoryDto categoryDto,
                                                      @PathVariable String userId) {
        UUID uuid = categoryService.saveCategory(categoryDto, userId);
        URI uri = ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path("/categories/{id}")
                .buildAndExpand(uuid).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        UUID uuid = Util.tryOrElse(() -> UUID.fromString(id),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        categoryService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
