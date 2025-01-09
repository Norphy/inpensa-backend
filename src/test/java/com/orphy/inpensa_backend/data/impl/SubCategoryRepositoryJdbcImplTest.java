package com.orphy.inpensa_backend.data.impl;

import com.orphy.inpensa_backend.exceptions.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.exceptions.data.UnExpectedException;
import com.orphy.inpensa_backend.model.SubCategory;
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
@Sql({"/data/schema.sql", "/data/subcategory/test-data.sql"})
class SubCategoryRepositoryJdbcImplTest {

    private static final String USER_ID = "6b246024-59b1-4716-b583-9a0c4d0e5191";
    private static final String USER_ID_TWO = "6b246024-59b1-4716-b583-9a0c4d0e5111";
    private static final UUID CATEGORY_ID = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5192");
    private static final UUID CATEGORY_ID_TWO = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5112");
    private static final UUID SUB_CATEGORY_ID = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5193");
    private static final UUID SUB_CATEGORY_ID_TWO = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5113");
    private static final String EXPECTED_SUB_CAT = "Cleaning";
    private static final String EXPECTED_SUB_CAT_TWO = "Restaurants";

    private final SubCategoryRepositoryJdbcImpl subCategoryRepositoryJdbc;

    @Autowired
    public SubCategoryRepositoryJdbcImplTest(JdbcTemplate jdbcTemplate) {
        subCategoryRepositoryJdbc = new SubCategoryRepositoryJdbcImpl(jdbcTemplate);
    }

    @Test
    void getAllSubCategoriesByUser_HappyPath() {

        //Arrange
        final SubCategory expectedCategory = getExpectedCategoryWithId(SUB_CATEGORY_ID, EXPECTED_SUB_CAT, CATEGORY_ID, USER_ID);

        //Act
        List<SubCategory> subCategoryList = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);

        //Assert
        assertEquals(1, subCategoryList.size());
        SubCategory actualSubCategory = subCategoryList.getFirst();
        assertEquals(expectedCategory, actualSubCategory);
    }

    @Test
    void getAllSubCategoriesByUser_Two_HappyPath() {

        //Arrange
        final SubCategory expectedCategory = getExpectedCategoryWithId(SUB_CATEGORY_ID_TWO, EXPECTED_SUB_CAT_TWO, CATEGORY_ID_TWO, USER_ID_TWO);

        //Act
        List<SubCategory> subCategoryList = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID_TWO);

        //Assert
        assertEquals(1, subCategoryList.size());
        SubCategory actualSubCategory = subCategoryList.getFirst();
        assertEquals(expectedCategory, actualSubCategory);
    }

    @Test
    void getAllSubCategories_Admin_HappyPath() {

        //Arrange
        final SubCategory expectedCategory = getExpectedCategoryWithId(SUB_CATEGORY_ID, EXPECTED_SUB_CAT, CATEGORY_ID, USER_ID);
        final SubCategory expectedCategoryTwo = getExpectedCategoryWithId(SUB_CATEGORY_ID_TWO, EXPECTED_SUB_CAT_TWO, CATEGORY_ID_TWO, USER_ID_TWO);

        //Act
        List<SubCategory> subCategoryList = subCategoryRepositoryJdbc.getAllSubCategories();

        //Assert
        assertEquals(2, subCategoryList.size());
        assertTrue(subCategoryList.contains(expectedCategory));
        assertTrue(subCategoryList.contains(expectedCategoryTwo));
    }

    @Test
    void getAllSubCategoriesByUser_UnHappyPath() {

        //Arrange
        final String NON_EXISTENT_ID = "6b111111-59b1-4716-b583-9a0c4d0e5191";

        //Act
        List<SubCategory>  subCategoryList = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(NON_EXISTENT_ID);

        //Assert
        assertEquals(0, subCategoryList.size());
    }


    @Test
    void getSubCategoryByIdAndByUser_HappyPath() {

        //Arrange
        final SubCategory expectedCategory = getExpectedCategoryWithId(SUB_CATEGORY_ID, EXPECTED_SUB_CAT, CATEGORY_ID, USER_ID);

        //Act
        SubCategory subCategory = subCategoryRepositoryJdbc.getSubCategoryByIdAndUser(SUB_CATEGORY_ID, USER_ID);

        //Assert
        assertNotNull(subCategory);
        assertEquals(expectedCategory, subCategory);
    }

    @Test
    void getSubCategoryById_Admin_HappyPath() {

        //Arrange
        final SubCategory expectedCategory = getExpectedCategoryWithId(SUB_CATEGORY_ID, EXPECTED_SUB_CAT, CATEGORY_ID, USER_ID);

        //Act
        SubCategory subCategory = subCategoryRepositoryJdbc.getSubCategoryById(SUB_CATEGORY_ID);

        //Assert
        assertNotNull(subCategory);
        assertEquals(expectedCategory, subCategory);
    }

    @Test
    void getSubCategoryByIdAndByUser_UnHappyPath() {

        //Arrange
        final UUID NON_EXISTENT_ID = UUID.fromString("6b111111-59b1-4716-b583-9a0c4d0e5191");
        final SubCategory expectedCategory = getExpectedCategoryWithId(SUB_CATEGORY_ID, EXPECTED_SUB_CAT, CATEGORY_ID, USER_ID);

        //Act && Assert
        assertThrows(ResourceNotFoundException.class, () -> subCategoryRepositoryJdbc.getSubCategoryByIdAndUser(NON_EXISTENT_ID, USER_ID));
    }

    @Test
    void saveSubCategory_HappyPath() {
        //Arrange
        final String newSubCat = "NewSubCat";
        final SubCategory saveSubCategory = getExpectedCategoryWithoutId(newSubCat, CATEGORY_ID, USER_ID);

        //Act
        final List<SubCategory> subCategoryListBefore = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);
        final SubCategory actualSubCategory = subCategoryRepositoryJdbc.saveSubCategory(saveSubCategory);
        final List<SubCategory> subCategoryListAfter = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);

        SubCategory expectedSubCategory = saveSubCategory.copyWithId(actualSubCategory.id());

        //Assert
        assertEquals(1, subCategoryListBefore.size());
        assertEquals(expectedSubCategory, actualSubCategory);
        assertEquals(newSubCat, actualSubCategory.subCategory());
        assertEquals(2, subCategoryListAfter.size());
    }

    @Test
    void saveSubCategory_UnHappyPath() {
        //Arrange
        final SubCategory invalidSubCat = getInvalidSubCategory();

        //Act
        final List<SubCategory> subCategoryListBefore = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);
        assertThrows(DataIntegrityViolationException.class, () -> subCategoryRepositoryJdbc.saveSubCategory(invalidSubCat));
        final List<SubCategory> subCategoryListAfter = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);

        //Assert
        assertEquals(1, subCategoryListBefore.size());
        assertEquals(1, subCategoryListAfter.size());
    }

    @Test
    void updateSubCategory_HappyPath() {

        //Arrange
        final String expectedUpdatedSubCat = "Random-new-sub-cat";
        final SubCategory updateSubCategory = getExpectedCategoryWithId(SUB_CATEGORY_ID, expectedUpdatedSubCat, CATEGORY_ID, USER_ID);

        //Act
        final List<SubCategory> subCategoryListBefore = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);
        final SubCategory subCategoryBefore = subCategoryRepositoryJdbc.getSubCategoryByIdAndUser(SUB_CATEGORY_ID, USER_ID);
        subCategoryRepositoryJdbc.updateSubCategory(updateSubCategory);
        final SubCategory subCategoryAfter = subCategoryRepositoryJdbc.getSubCategoryByIdAndUser(SUB_CATEGORY_ID, USER_ID);
        final List<SubCategory> subCategoryListAfter = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);

        //Assert
        assertEquals(1, subCategoryListBefore.size());
        assertEquals(SUB_CATEGORY_ID, subCategoryBefore.id());
        assertEquals(EXPECTED_SUB_CAT, subCategoryBefore.subCategory());
        assertEquals(1, subCategoryListAfter.size());
        assertEquals(SUB_CATEGORY_ID, subCategoryAfter.id());
        assertEquals(expectedUpdatedSubCat, subCategoryAfter.subCategory());
    }

    @Test
    void updateSubCategory_UnHappyPath() {

        //Arrange
        final String expectedUpdatedSubCat = "New-Sub-Category";
        final UUID NON_EXISTENT_ID = UUID.fromString("6b111111-59b1-4716-b583-9a0c4d0e5191");
        final SubCategory updateSubCategory = getExpectedCategoryWithId(NON_EXISTENT_ID, expectedUpdatedSubCat, CATEGORY_ID, USER_ID);

        //Act
        final List<SubCategory> subCategoryListBefore = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);
        assertThrows(UnExpectedException.class, () -> subCategoryRepositoryJdbc.updateSubCategory(updateSubCategory));
        final List<SubCategory> subCategoryListAfter = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);

        //Assert
        assertEquals(1, subCategoryListBefore.size());
        assertEquals(1, subCategoryListAfter.size());
    }

    @Test
    void deleteSubCategory_HappyPath() {

        //Arrange

        //Act
        final List<SubCategory> subCategoryListBefore = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);
        final SubCategory subCategoryBefore = subCategoryRepositoryJdbc.getSubCategoryByIdAndUser(SUB_CATEGORY_ID, USER_ID);
        subCategoryRepositoryJdbc.deleteSubCategory(SUB_CATEGORY_ID);
        assertThrows(ResourceNotFoundException.class, () -> subCategoryRepositoryJdbc.getSubCategoryByIdAndUser(SUB_CATEGORY_ID, USER_ID));
        final List<SubCategory> subCategoryListAfter = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);

        //Assert
        assertEquals(1, subCategoryListBefore.size());
        assertNotNull(subCategoryBefore);
        assertEquals(SUB_CATEGORY_ID, subCategoryBefore.id());
        assertEquals(0, subCategoryListAfter.size());
    }

    @Test
    void deleteSubCategory_UnHappyPath() {

        //Arrange
        final UUID NON_EXISTENT_ID = UUID.fromString("6b111111-59b1-4716-b583-9a0c4d0e5191");

        //Act
        assertThrows(ResourceNotFoundException.class, () -> subCategoryRepositoryJdbc.getSubCategoryByIdAndUser(NON_EXISTENT_ID, USER_ID));
        assertThrows(UnExpectedException.class, () -> subCategoryRepositoryJdbc.deleteSubCategory(NON_EXISTENT_ID));
        assertThrows(ResourceNotFoundException.class, () -> subCategoryRepositoryJdbc.getSubCategoryByIdAndUser(NON_EXISTENT_ID, USER_ID));
        final List<SubCategory> subCategoryListAfter = subCategoryRepositoryJdbc.getAllSubCategoriesByUser(USER_ID);

        //Assert
        assertEquals(1, subCategoryListAfter.size());
    }

    private SubCategory getInvalidSubCategory() {
        UUID nonExistentId = UUID.fromString("6b111111-59b1-4716-b583-9a0c4d0e5192");
        return new SubCategory(null, "RandomSubCat", nonExistentId, USER_ID);
    }

    private SubCategory getExpectedCategoryWithoutId(String subCat, UUID catId, String userId) {
        return new SubCategory(null, subCat, catId, userId);
    }

    private SubCategory getExpectedCategoryWithId(UUID id, String subCat, UUID catId, String userId) {
        return new SubCategory(id, subCat, catId, userId);
    }
}