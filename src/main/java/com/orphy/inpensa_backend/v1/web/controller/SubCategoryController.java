package com.orphy.inpensa_backend.v1.web.controller;

import com.orphy.inpensa_backend.v1.model.SubCategory;
import com.orphy.inpensa_backend.v1.model.dto.SubCategoryDto;
import com.orphy.inpensa_backend.v1.service.SubCategoryService;
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
@RequestMapping("/v1/subcategories")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @GetMapping("/admin/all")
    public List<SubCategory> getAllSubCategories() {
        return subCategoryService.getAllSubCategories();
    }

    @GetMapping("/admin/user/{userId}")
    public List<SubCategory> getAllSubCategoriesByUser(@PathVariable String userId) {
        return subCategoryService.getAllSubCategoriesByUser(userId);
    }

    @GetMapping
    public List<SubCategory> getSubCategoriesCurrentUser(@Parameter(hidden = true) SecurityContext context) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) context.getAuthentication();
        String userId = jwtAuthenticationToken.getName();
        return subCategoryService.getAllSubCategoriesByUser(userId);
    }

    @GetMapping("/{id}")
    public SubCategory getSubCategoryById(@PathVariable String id) {
        UUID uuid = Util.tryOrElse(() -> UUID.fromString(id),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        return subCategoryService.getSubCategoryById(uuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createSubCategory(@RequestBody SubCategoryDto subCategoryDto) {
        UUID id = subCategoryService.saveSubCategory(subCategoryDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/admin/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createSubCategoryForUser(@RequestBody SubCategoryDto subCategoryDto, String userId)  {
        UUID id = subCategoryService.saveSubCategory(userId, subCategoryDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/subcategories/{id}")
                .buildAndExpand(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateSubCategory(@RequestBody SubCategory subCategory) {
        subCategoryService.update(subCategory);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteSubCategory(@PathVariable String id) {
        UUID uuid = Util.tryOrElse(() -> UUID.fromString(id),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        subCategoryService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
