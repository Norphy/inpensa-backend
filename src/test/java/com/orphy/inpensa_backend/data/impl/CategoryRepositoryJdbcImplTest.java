package com.orphy.inpensa_backend.data.impl;

import com.orphy.inpensa_backend.exceptions.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.exceptions.data.UnExpectedException;
import com.orphy.inpensa_backend.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Sql({"/data/schema.sql", "/data/category/test-data.sql"})
class CategoryRepositoryJdbcImplTest {

    private final String CATEGORY_ONE = "Home";
    private final String CATEGORY_TWO = "Food";
    private final String USER_ID = "6b246024-59b1-4716-b583-9a0c4d0e5191";
    private final String USER_ID_TWO = "6b246024-59b1-4716-b583-9a0c4d0e5111";
    private final UUID CATEGORY_ID = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5192");
    private final UUID CATEGORY_ID_TWO = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5112");


    private final CategoryRepositoryJdbcImpl categoryRepositoryJdbc;

    @Autowired
    public CategoryRepositoryJdbcImplTest(JdbcTemplate jdbcTemplate) {
        categoryRepositoryJdbc = new CategoryRepositoryJdbcImpl(jdbcTemplate);
    }

    @Test
    void getAllCategoriesByUser_HappyPath() {
        //Arrange
        final Category expectedCategory = getExpectedCategoryWithId(CATEGORY_ID, CATEGORY_ONE, USER_ID);

        //Act
        List<Category> categoryList = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);

        //Assert
        assertEquals(1, categoryList.size());
        Category actualCategory = categoryList.getFirst();
        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    void getAllCategoriesByUser_Two_HappyPath() {
        //Arrange
        final Category expectedCategory = getExpectedCategoryWithId(CATEGORY_ID_TWO, CATEGORY_TWO, USER_ID_TWO);

        //Act
        List<Category> categoryList = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID_TWO);

        //Assert
        assertEquals(1, categoryList.size());
        Category actualCategory = categoryList.getFirst();
        assertEquals(expectedCategory, actualCategory);
    }
    
    @Test
    void getAllCategories_Admin_HappyPath() {
        //Arrange
        final Category expectedCategory = getExpectedCategoryWithId(CATEGORY_ID, CATEGORY_ONE, USER_ID);
        final Category expectedCategoryTwo = getExpectedCategoryWithId(CATEGORY_ID_TWO, CATEGORY_TWO, USER_ID_TWO);

        //Act
        List<Category> categoryList = categoryRepositoryJdbc.getAllCategories();

        //Assert
        assertEquals(2, categoryList.size());
        assertTrue(categoryList.contains(expectedCategory));
        assertTrue(categoryList.contains(expectedCategoryTwo));
    }

    @Test
    void getAllCategoriesByUser_UnHappyPath() {

        //Arrange
        final String NON_EXISTENT_ID = "6b111111-59b1-4716-b583-9a0c4d0e5191";

        //Act
        List<Category> categoryList = categoryRepositoryJdbc.getAllCategoriesByUser(NON_EXISTENT_ID);

        //Assert
        assertEquals(0, categoryList.size());
    }

    @Test
    void getCategoryByIdAndUser_HappyPath() {

        //Arrange
        final Category expectedCategory = getExpectedCategoryWithId(CATEGORY_ID, CATEGORY_ONE, USER_ID);

        //Act
        Category actualCategory = categoryRepositoryJdbc.getCategoryByIdAndUser(CATEGORY_ID, USER_ID);

        //Assert
        assertNotNull(actualCategory);
        assertEquals(expectedCategory, actualCategory);
    }


    @Test
    void getCategoryById_Admin_HappyPath() {

        //Arrange
        final Category expectedCategory = getExpectedCategoryWithId(CATEGORY_ID, CATEGORY_ONE, USER_ID);

        //Act
        Category actualCategory = categoryRepositoryJdbc.getCategoryById(CATEGORY_ID);

        //Assert
        assertNotNull(actualCategory);
        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    void getCategoryByIdAndUser_UnHappyPath() {
        //Arrange
        final String NON_EXISTENT_USER_ID = "6b111111-59b1-4716-b583-9a0c4d0e5191";
        final UUID NON_EXISTENT_ID = UUID.fromString("6b111111-59b1-4716-b583-9a0c4d0e5191");

        //Act && Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryRepositoryJdbc.getCategoryByIdAndUser(NON_EXISTENT_ID, NON_EXISTENT_USER_ID));
    }

    @Test
    void saveCategory_HappyPath() {
        //Arrange
        final Category saveCategory = getExpectedCategoryWithoutId();

        //Act
        final List<Category> listOfCategoriesBefore = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);
        final Category actualCategory = categoryRepositoryJdbc.saveCategory(saveCategory);
        final Category savedCategory = saveCategory.getCopyWithId(actualCategory.id());
        final List<Category> listOfCategoriesAfter = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);

        //Assert
        assertEquals(savedCategory, actualCategory);
        assertEquals(1, listOfCategoriesBefore.size());
        assertEquals(2, listOfCategoriesAfter.size());

    }

    @Test
    void saveCategory_UnHappyPath() {
        //Arrange
        final Category invalidCategory = getInvalidCategory();

        //Act
        final List<Category> listOfCategoriesBefore = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);
        assertThrows(DataIntegrityViolationException.class, () -> categoryRepositoryJdbc.saveCategory(invalidCategory));
        final List<Category> listOfCategoriesAfter = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);

        //Assert
        assertEquals(1, listOfCategoriesBefore.size());
        assertEquals(1, listOfCategoriesAfter.size());

    }

    @Test
    void updateCategory_HappyPath() {
        //Arrange
        final String expectedOriCategory = CATEGORY_ONE;
        final String expectedChangedCategory = "New-Category";
        final Category oriCategory = getExpectedCategoryWithId(CATEGORY_ID, expectedChangedCategory, USER_ID);

        //Act
        final Category categoryBefore = categoryRepositoryJdbc.getCategoryByIdAndUser(CATEGORY_ID, USER_ID);
        final List<Category> listOfCategoriesBefore = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);
        categoryRepositoryJdbc.updateCategory(oriCategory);
        final Category categoryAfter = categoryRepositoryJdbc.getCategoryByIdAndUser(CATEGORY_ID, USER_ID);
        final List<Category> listOfCategoriesAfter = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);

        //Assert
        assertEquals(expectedOriCategory, categoryBefore.category());
        assertEquals(CATEGORY_ID, categoryBefore.id());
        assertEquals(expectedChangedCategory, categoryAfter.category());
        assertEquals(CATEGORY_ID, categoryAfter.id());
        assertEquals(1, listOfCategoriesBefore.size());
        assertEquals(1, listOfCategoriesAfter.size());
    }

    @Test
    void updateCategory_UnHappyPath() {
        //Arrange
        final String expectedChangedCategory = "New-Category";
        final UUID NON_EXISTENT_ID = UUID.fromString("6b111111-59b1-4716-b583-9a0c4d0e5191");
        final Category oriCategory = getExpectedCategoryWithId(NON_EXISTENT_ID, expectedChangedCategory, USER_ID);

        //Act && Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryRepositoryJdbc.getCategoryByIdAndUser(NON_EXISTENT_ID, USER_ID));
        final List<Category> listOfCategoriesBefore = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);
        assertThrows(UnExpectedException.class, () -> categoryRepositoryJdbc.updateCategory(oriCategory));
        assertThrows(ResourceNotFoundException.class, () -> categoryRepositoryJdbc.getCategoryByIdAndUser(NON_EXISTENT_ID, USER_ID));
        final List<Category> listOfCategoriesAfter = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);

        //Assert
        assertEquals(1, listOfCategoriesBefore.size());
        assertEquals(1, listOfCategoriesAfter.size());
    }

    @Test
    void deleteCategory_HappyPath() {

        //Arrange

        //Act
        final List<Category> listOfCategoriesBefore = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);
        final Category categoryBefore = categoryRepositoryJdbc.getCategoryByIdAndUser(CATEGORY_ID, USER_ID);
        categoryRepositoryJdbc.deleteCategory(CATEGORY_ID);
        final List<Category> listOfCategoriesAfter= categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);
        assertThrows(ResourceNotFoundException.class, () -> categoryRepositoryJdbc.getCategoryByIdAndUser(CATEGORY_ID, USER_ID));

        //Assert
        assertNotNull(categoryBefore);
        assertEquals(1, listOfCategoriesBefore.size());
        assertEquals(0, listOfCategoriesAfter.size());
    }

    @Test
    void deleteCategory_UnHappyPath() {

        //Arrange
        final UUID nonExistentId = UUID.fromString("5b111111-59b1-4716-b583-9a0c4d0e5191");

        //Act
        final List<Category> listOfCategoriesBefore = categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);
        assertThrows(UnExpectedException.class, () -> categoryRepositoryJdbc.deleteCategory(nonExistentId));
        final List<Category> listOfCategoriesAfter= categoryRepositoryJdbc.getAllCategoriesByUser(USER_ID);

        //Assert
        assertEquals(1, listOfCategoriesBefore.size());
        assertEquals(1, listOfCategoriesAfter.size());
    }

    private Category getInvalidCategory() {

        final String NON_EXISTENT_ID = "Non_Existent";
        return new Category(null, "Utilities", NON_EXISTENT_ID);
    }

    private Category getExpectedCategoryWithoutId() {
        return new Category(null, "Utilities", USER_ID);
    }

    private Category getExpectedCategoryWithId(UUID id, String category, String userId) {
        return new Category(id, category, userId);
    }
}