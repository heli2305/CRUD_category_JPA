package vn.iotstar.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import vn.iotstar.configs.JPAConfig;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.dao.impl.CategoryDao;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;

class CategoryJpaTest {

    private final ICategoryDao categoryDao = new CategoryDao();
    private final ICategoryService categoryService = new CategoryServiceImpl();

    @AfterAll
    static void tearDownAll() {
        JPAConfig.shutdown();
    }

    @Test
    void testEntityManagerConnection() {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            assertNotNull(em);
            assertTrue(em.isOpen());
        } finally {
            em.close();
        }
    }

    private boolean isCategoriesTableReady() {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            Object result = em.createNativeQuery(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'categories'")
                    .getSingleResult();
            return ((Number) result).intValue() > 0;
        } finally {
            em.close();
        }
    }

    @Test
    void testCategoryCrudAndQueries() throws Exception {
        assumeTrue(isCategoriesTableReady(),
                "Table 'categories' does not exist yet in ShoppingServiceMVC. Run JPA/database.sql in SSMS first.");

        String uniqueName = "JUnitCat_" + (System.currentTimeMillis() % 1_000_000);
        int initialCount = categoryDao.count();

        Category cate = new Category();
        cate.setCategoryname(uniqueName);
        cate.setImages("avatar.png");
        cate.setStatus(1);

        categoryService.insert(cate);
        categoryService.insert(cate);

        Category foundByName = categoryService.findByCategoryname(uniqueName);
        assertNotNull(foundByName);
        assertEquals(uniqueName, foundByName.getCategoryname());
        assertEquals(initialCount + 1, categoryDao.count());

        int createdId = foundByName.getCategoryid();
        try {
            Category foundById = categoryService.findById(createdId);
            assertNotNull(foundById);
            assertEquals(1, foundById.getStatus());

            List<Category> searchResults = categoryService.searchByName("JUnitCat_");
            assertFalse(searchResults.isEmpty());

            List<Category> paged = categoryService.findAll(0, 5);
            assertFalse(paged.isEmpty());

            foundById.setCategoryname(uniqueName + "_Ed");
            foundById.setStatus(0);
            categoryService.update(foundById);

            Category updated = categoryService.findById(createdId);
            assertNotNull(updated);
            assertEquals(uniqueName + "_Ed", updated.getCategoryname());
            assertEquals(0, updated.getStatus());
        } finally {
            categoryDao.delete(createdId);
            assertNull(categoryDao.findById(createdId));
        }
    }
}
