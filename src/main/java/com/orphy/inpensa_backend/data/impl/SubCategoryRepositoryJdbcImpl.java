package com.orphy.inpensa_backend.data.impl;

import com.orphy.inpensa_backend.data.SubCategoryRepository;
import com.orphy.inpensa_backend.exceptions.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.exceptions.data.UnExpectedException;
import com.orphy.inpensa_backend.model.SubCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class SubCategoryRepositoryJdbcImpl implements SubCategoryRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    public SubCategoryRepositoryJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<SubCategory> getAllSubCategories() {
        logger.debug("Get all SubCategories.");
        String SQL_GET_ALL_SUB_CATEGORIES_BY_USER = "SELECT * FROM T_SUB_CATEGORY;";
        return jdbcTemplate.query(SQL_GET_ALL_SUB_CATEGORIES_BY_USER, SUB_CATEGORY_ROW_MAPPER);
    }

    @Override
    public List<SubCategory> getAllSubCategoriesByUser(String userId) {
        logger.debug("Get all SubCategories by User. User id: {}", userId);
        String SQL_GET_ALL_SUB_CATEGORIES_BY_USER = "SELECT * FROM T_SUB_CATEGORY WHERE OWNER_ID = ?;";
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_GET_ALL_SUB_CATEGORIES_BY_USER);
            ps.setString(1, userId);
            return ps;
        }, SUB_CATEGORY_ROW_MAPPER);
    }

    @Override
    public SubCategory getSubCategoryById(UUID subCatId) {
        logger.debug("Get all SubCategory by id. SubCat id: {}", subCatId);
        String SQL_GET_SUB_CATEGORY_ID_AND_BY_USER = "SELECT * FROM T_SUB_CATEGORY WHERE ID = ?;";
        String subCatIdStr = subCatId.toString();
        List<SubCategory> subCategories =  jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_GET_SUB_CATEGORY_ID_AND_BY_USER);
            ps.setString(1, subCatIdStr);
            return ps;
        }, SUB_CATEGORY_ROW_MAPPER);
        return subCategories.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("SubCategory not found."));

    }

    @Override
    public SubCategory getSubCategoryByIdAndUser(UUID subCatId, String userId) {
        logger.debug("Get all SubCategory by id and User. SubCat id: {} User id: {}", subCatId, userId);
        String SQL_GET_SUB_CATEGORY_ID_AND_BY_USER = "SELECT * FROM T_SUB_CATEGORY WHERE ID = ? AND OWNER_ID = ?;";
        String subCatIdStr = subCatId.toString();
        List<SubCategory> subCategories =  jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_GET_SUB_CATEGORY_ID_AND_BY_USER);
            ps.setString(1, subCatIdStr);
            ps.setString(2, userId);
            return ps;
        }, SUB_CATEGORY_ROW_MAPPER);
        return subCategories.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("SubCategory not found."));
    }

    @Override
    public SubCategory saveSubCategory(SubCategory subCategory) {
        logger.debug("Save SubCategory.");
        final String SQL_SAVE_SUB_CATEGORY = "INSERT INTO T_SUB_CATEGORY (SUB_CATEGORY, CATEGORY_ID, OWNER_ID) VALUES (?, ?, ?);";
        String catIdStr = subCategory.categoryId().toString();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_SAVE_SUB_CATEGORY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, subCategory.subCategory());
            ps.setString(2, catIdStr);
            ps.setString(3, subCategory.userId());
            return ps;
        }, keyHolder);

        final String idStr = keyHolder.getKeys().values()
                .stream().findAny()
                .orElseThrow(() -> new UnExpectedException("Error getting generated Id.")).toString();
        final UUID id = UUID.fromString(idStr);
        return subCategory.copyWithId(id);
    }

    @Override
    public void updateSubCategory(SubCategory subCategory) {
        logger.debug("Update SubCategory. SubCat id: {}", subCategory.id());
        final String SQL_UPDATE_SUB_CATEGORY = "UPDATE T_SUB_CATEGORY SET SUB_CATEGORY = ?, CATEGORY_ID = ?, OWNER_ID = ? WHERE ID = ?;";
        String idStr = subCategory.id().toString();
        String catIdStr = subCategory.categoryId().toString();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_UPDATE_SUB_CATEGORY, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, subCategory.subCategory());
            ps.setString(2, catIdStr);
            ps.setString(3, subCategory.userId());
            ps.setString(4, idStr);
            return ps;
        });
        if(rowsAffected != 1) {
            throw new UnExpectedException("Error updating Sub Category with id: " + idStr);
        }
    }

    @Override
    public void deleteSubCategory(UUID subCatId) {

        logger.debug("Delete SubCategory. SubCat id: {}", subCatId);
        final String SQL_DELETE_SUB_CATEGORY = "DELETE FROM T_SUB_CATEGORY WHERE ID = ?;";
        String subCatIdStr = subCatId.toString();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_SUB_CATEGORY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, subCatIdStr);
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error deleting Sub Category with id: " + subCatId);
        }
    }

    private final RowMapper<SubCategory> SUB_CATEGORY_ROW_MAPPER =
            (rs, rowNum) -> {
                String idStr = rs.getString("ID");
                String subCategory = rs.getString("SUB_CATEGORY");
                String categoryId = rs.getString("CATEGORY_ID");
                String ownerIdStr = rs.getString("OWNER_ID");
                UUID id = UUID.fromString(idStr);
                UUID catId = UUID.fromString(categoryId);
                return new SubCategory(id, subCategory, catId, ownerIdStr);
            };
}
