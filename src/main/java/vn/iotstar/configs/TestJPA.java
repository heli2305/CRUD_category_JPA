package vn.iotstar.configs;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.dao.impl.CategoryDao;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Video;

public class TestJPA {

    public static void main(String[] args) {
        try {
            testPersistCategoryAndVideo();
            testCategoryDaoCrud();
            System.out.println("JPA test completed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JPAConfig.shutdown();
        }
    }

    private static void testPersistCategoryAndVideo() {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();

        Category cate = new Category();
        cate.setCategoryname("Iphone");
        cate.setImages("abc.jpg");
        cate.setStatus(1);

        Video video = new Video();
        video.setVideoId("v01");
        video.setTitle("test");
        video.setActive(1);
        video.setCategory(cate);

        try {
            trans.begin();
            Video existingVideo = enma.find(Video.class, video.getVideoId());
            if (existingVideo != null) {
                enma.remove(existingVideo);
            }
            enma.persist(cate);
            enma.persist(video);
            trans.commit();
            System.out.println("Persisted Category id = " + cate.getCategoryid()
                    + " and Video id = " + video.getVideoId());

            trans.begin();
            Video savedVideo = enma.find(Video.class, "v01");
            if (savedVideo != null) {
                enma.remove(savedVideo);
            }
            Category savedCate = enma.find(Category.class, cate.getCategoryid());
            if (savedCate != null) {
                enma.remove(savedCate);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    private static void testCategoryDaoCrud() throws Exception {
        ICategoryDao cateDao = new CategoryDao();
        String testName = "Test JPA Category";

        Category existing = cateDao.findByCategoryname(testName);
        if (existing == null) {
            Category cate = new Category();
            cate.setCategoryname(testName);
            cate.setImages("avatar.png");
            cate.setStatus(1);
            cateDao.insert(cate);
            System.out.println("Inserted category id = " + cate.getCategoryid());
        }

        List<Category> list = cateDao.findAll();
        System.out.println("Total categories: " + cateDao.count());
        for (Category c : list) {
            System.out.println(" - [" + c.getCategoryid() + "] " + c.getCategoryname()
                    + " (status=" + c.getStatus() + ")");
        }

        Category created = cateDao.findByCategoryname(testName);
        if (created != null) {
            created.setStatus(0);
            cateDao.update(created);
            System.out.println("Updated status to " + cateDao.findById(created.getCategoryid()).getStatus());

            cateDao.delete(created.getCategoryid());
            System.out.println("Deleted test category id = " + created.getCategoryid());
        }
    }
}
