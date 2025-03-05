package com.orphy.inpensa_backend.v1.service;

import com.orphy.inpensa_backend.v1.model.dto.CategoryDto;
import com.orphy.inpensa_backend.v1.model.dto.SubCategoryDto;
import com.orphy.inpensa_backend.v1.model.dto.WalletDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class UserDataInitializationService {

    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;
    private final WalletService walletService;
    private final Map<String, List<String>> initialCategoriesAndSubCategories =
            Map.of("Home", List.of("HomeToo"));
    private final Set<Map.Entry<String, Long>> initialWallets =
            Set.of(Map.entry("Basic", 0L));
    public UserDataInitializationService(CategoryService categoryService, SubCategoryService subCategoryService,
                                         WalletService walletService) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
        this.walletService = walletService;
    }

    @Transactional
    public void prepareInitialData() {
        prepareInitialCategories();
        prepareInitialWallet();
    }

    private void prepareInitialCategories() {
        initialCategoriesAndSubCategories.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(subCatVal -> Map.entry(entry.getKey(), subCatVal)))
                .map(entry -> Map.entry(new CategoryDto(entry.getKey()), entry.getValue()))
                .map(entry -> {
                    CategoryDto categoryDto  = entry.getKey();
                    UUID uuid = categoryService.saveCategory(categoryDto);
                    return Map.entry(uuid, entry.getValue());
                }).map(entry -> new SubCategoryDto(entry.getValue(), entry.getKey()))
                .forEach(subCategoryService::saveSubCategory);
    }

    private void prepareInitialWallet() {
        initialWallets.stream()
                .map(entry -> new WalletDto(entry.getKey(), entry.getValue()))
                .forEach(walletService::saveWallet);
    }
}
