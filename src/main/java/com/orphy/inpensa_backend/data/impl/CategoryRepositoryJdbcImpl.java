package com.orphy.inpensa_backend.data.impl;

import com.orphy.inpensa_backend.data.CategoryRepository;
import com.orphy.inpensa_backend.exceptions.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.exceptions.data.UnExpectedException;
import com.orphy.inpensa_backend.model.Category;
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

public class CategoryRepositoryJdbcImpl implements CategoryRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepositoryJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Category> getAllCategories() {

        logger.debug("Get all categories.");
        final String SQL_GET_ALL_CATEGORIES_BY_USER = "SELECT * FROM T_CATEGORY;";
        return  jdbcTemplate.query(SQL_GET_ALL_CATEGORIES_BY_USER, CATEGORY_ROW_MAPPER);
    }

    @Override
    public List<Category> getAllCategoriesByUser(String userId) {
        logger.debug("Get all categories for user id: {}", userId);
        final String SQL_GET_ALL_CATEGORIES_BY_USER = "SELECT * FROM T_CATEGORY WHERE OWNER_ID = ?;";
        return  jdbcTemplate.query(conn -> {
            PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL_CATEGORIES_BY_USER);
            ps.setString(1, userId);
            return ps;
        }, CATEGORY_ROW_MAPPER);
    }

    @Override
    public Category getCategoryById(UUID catId) {
        logger.debug("Get category with id: {}", catId);
        final String SQL_GET_ALL_CATEGORIES_BY_USER = "SELECT * FROM T_CATEGORY WHERE ID = ?;";
        List<Category> listOfCategories = jdbcTemplate.query(conn -> {
            PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL_CATEGORIES_BY_USER);
            String catIdStr = catId.toString();
            ps.setString(1, catIdStr);
            return ps;
        }, CATEGORY_ROW_MAPPER);
        return listOfCategories.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    }

    @Override
    public Category getCategoryByIdAndUser(UUID catId, String userId) {
        logger.debug("Get category with id: {} and user id: {}", catId, userId);
        final String SQL_GET_ALL_CATEGORIES_BY_USER = "SELECT * FROM T_CATEGORY WHERE ID = ? AND OWNER_ID = ?;";
        List<Category> listOfCategories = jdbcTemplate.query(conn -> {
            PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL_CATEGORIES_BY_USER);
            String catIdStr = catId.toString();
            ps.setString(1, catIdStr);
            ps.setString(2, userId);
            return ps;
        }, CATEGORY_ROW_MAPPER);
        return listOfCategories.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public Category saveCategory(Category category) {
        logger.debug("Save new Category. User id: {}", category.userId());
        final String SQL_SAVE_CATEGORY = "INSERT INTO T_CATEGORY (CATEGORY, OWNER_ID) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_SAVE_CATEGORY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.category());
            ps.setString(2, category.userId());
            return ps;
        }, keyHolder);

        final String idStr = keyHolder.getKeys().values()
                .stream().findAny()
                .orElseThrow(() -> new UnExpectedException("Error getting generated Id.")).toString();
        final UUID id = UUID.fromString(idStr);
        return category.getCopyWithId(id);
    }

    @Override
    public void updateCategory(Category category) {

        logger.debug("Update Category. Cat id: {} User id: {}", category.id(), category.userId());
        final String SQL_UPDATE_CATEGORY = "UPDATE T_CATEGORY SET CATEGORY = ?, OWNER_ID = ? WHERE ID = ?;";
        String idStr = category.id().toString();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_UPDATE_CATEGORY);
            ps.setString(1, category.category());
            ps.setString(2, category.userId());
            ps.setString(3, idStr);
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error updating Category with id: " + idStr);
        }
    }

    @Override
    public void deleteCategory(UUID catId) {

        logger.debug("Delete Category. Cat id: {}", catId);
        final String SQL_DELETE_CATEGORY = "DELETE FROM T_CATEGORY WHERE ID = ?;";
        String idStr = catId.toString();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_CATEGORY);
            ps.setString(1, idStr);
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error deleting Category with id: " + idStr);
        }
    }

    private final RowMapper<Category> CATEGORY_ROW_MAPPER =
            (rs, rowNum) -> {
                String idStr = rs.getString("ID");
                String category = rs.getString("CATEGORY");
                String ownerIdStr = rs.getString("OWNER_ID");
                UUID id = UUID.fromString(idStr);
                return new Category(id, category, ownerIdStr);
            };
}
