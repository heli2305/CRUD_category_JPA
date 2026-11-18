package vn.iotstar.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import vn.iotstar.configs.JPAConfig;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.CategoryDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.VideoHelper;

class VideoJpaTest {

    private final IVideoService videoService = new VideoServiceImpl();
    private final UserDao userDao = new UserDaoImpl();
    private final ICategoryDao categoryDao = new CategoryDao();

    @AfterAll
    static void tearDownAll() {
        JPAConfig.shutdown();
    }

    private boolean isVideosSchemaReady() {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            Object count = em.createNativeQuery(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                            + "WHERE TABLE_NAME = 'Videos' AND COLUMN_NAME IN ('UserId', 'VideoUrl', 'CreatedDate')")
                    .getSingleResult();
            return ((Number) count).intValue() == 3;
        } finally {
            em.close();
        }
    }

    private User createTestUser(String suffix) {
        User u = new User(
                "vid_" + suffix + "@test.local",
                "viduser_" + suffix,
                "Video User " + suffix,
                "$2a$10$PXHjIrtBimozNAd25WYr1OZzyyegP6wsGVhzhV2mTZbjkMqc1GRPa",
                "avatar.png",
                5,
                "0900" + suffix.substring(0, Math.min(6, suffix.length())),
                new Date(System.currentTimeMillis())
        );
        userDao.insert(u);
        return u;
    }

    @Test
    void testVideoCrudAndViewIncrement() throws Exception {
        assumeTrue(isVideosSchemaReady(), "Videos schema with UserId/VideoUrl/CreatedDate is not ready.");

        String suffix = String.valueOf(System.currentTimeMillis() % 1_000_000);
        User owner = createTestUser(suffix);
        Category cate = new Category();
        cate.setCategoryname("VidCat_" + suffix);
        cate.setImages("avatar.png");
        cate.setStatus(1);
        categoryDao.insert(cate);

        String videoId = "TEST_VID_" + suffix;
        try {
            Video v = new Video();
            v.setVideoId(videoId);
            v.setTitle("Test Video " + suffix);
            v.setDescription("Mo ta test");
            v.setPoster("poster.png");
            v.setVideoUrl("sample.mp4");
            v.setActive(1);
            v.setViews(0);
            v.setCategory(cate);
            v.setUser(owner);
            videoService.insert(v);

            Video found = videoService.findById(videoId);
            assertNotNull(found);
            assertEquals("Test Video " + suffix, found.getTitle());
            assertNotNull(found.getCreatedDate());
            assertNotNull(found.getUser());
            assertEquals(owner.getId(), found.getUser().getId());
            assertNotNull(found.getCategory());
            assertEquals(cate.getCategoryid(), found.getCategory().getCategoryid());

            found.setTitle("Updated Video " + suffix);
            found.setActive(0);
            videoService.update(found);

            Video updated = videoService.findById(videoId);
            assertEquals("Updated Video " + suffix, updated.getTitle());
            assertEquals(0, updated.getActive());

            videoService.incrementViews(videoId);
            Video viewed = videoService.findById(videoId);
            assertEquals(1, viewed.getViews());

            videoService.delete(videoId);
            assertNull(videoService.findById(videoId));
        } finally {
            if (videoService.findById(videoId) != null) {
                videoService.delete(videoId);
            }
            categoryDao.delete(cate.getCategoryid());
            userDao.delete(owner.getId());
        }
    }

    @Test
    void testLatestTenActiveVideos() throws Exception {
        assumeTrue(isVideosSchemaReady(), "Videos schema with UserId/VideoUrl/CreatedDate is not ready.");

        String suffix = String.valueOf((System.currentTimeMillis() + 17) % 1_000_000);
        User owner = createTestUser(suffix);
        List<String> createdIds = new ArrayList<>();
        LocalDateTime futureBase = LocalDateTime.now().plusYears(10);

        try {
            for (int i = 1; i <= 12; i++) {
                Video v = new Video();
                v.setVideoId("LATEST_" + suffix + "_" + i);
                v.setTitle("Latest " + i);
                v.setActive(1);
                v.setViews(0);
                v.setCreatedDate(futureBase.plusMinutes(i));
                v.setUser(owner);
                videoService.insert(v);
                createdIds.add(v.getVideoId());
            }

            Video inactive = new Video();
            inactive.setVideoId("LATEST_INACTIVE_" + suffix);
            inactive.setTitle("Inactive Video");
            inactive.setActive(0);
            inactive.setViews(0);
            inactive.setCreatedDate(futureBase.plusMinutes(99));
            inactive.setUser(owner);
            videoService.insert(inactive);
            createdIds.add(inactive.getVideoId());

            List<Video> latest = videoService.findLatestActive(10);
            assertEquals(10, latest.size());
            assertEquals("LATEST_" + suffix + "_12", latest.get(0).getVideoId());
            assertEquals("LATEST_" + suffix + "_3", latest.get(9).getVideoId());
            assertTrue(latest.stream().noneMatch(v -> v.getVideoId().equals(inactive.getVideoId())));
        } finally {
            for (String id : createdIds) {
                if (videoService.findById(id) != null) {
                    videoService.delete(id);
                }
            }
            userDao.delete(owner.getId());
        }
    }

    @Test
    void testUserVideosPaginationSixPerPage() throws Exception {
        assumeTrue(isVideosSchemaReady(), "Videos schema with UserId/VideoUrl/CreatedDate is not ready.");

        String suffixA = "A" + (System.currentTimeMillis() % 100_000);
        String suffixB = "B" + ((System.currentTimeMillis() + 31) % 100_000);
        User userA = createTestUser(suffixA);
        User userB = createTestUser(suffixB);
        List<String> createdIds = new ArrayList<>();
        LocalDateTime base = LocalDateTime.now();

        try {
            for (int i = 1; i <= 8; i++) {
                Video v = new Video();
                v.setVideoId("PAGE_A_" + suffixA + "_" + i);
                v.setTitle("UserA Video " + i);
                v.setActive(1);
                v.setCreatedDate(base.plusSeconds(i));
                v.setUser(userA);
                videoService.insert(v);
                createdIds.add(v.getVideoId());
            }
            for (int i = 1; i <= 2; i++) {
                Video v = new Video();
                v.setVideoId("PAGE_B_" + suffixB + "_" + i);
                v.setTitle("UserB Video " + i);
                v.setActive(1);
                v.setCreatedDate(base.plusSeconds(i));
                v.setUser(userB);
                videoService.insert(v);
                createdIds.add(v.getVideoId());
            }

            assertEquals(8, videoService.countByUserId(userA.getId()));
            assertEquals(2, videoService.countByUserId(userB.getId()));

            List<Video> page1 = videoService.findByUserId(userA.getId(), 0, 6);
            List<Video> page2 = videoService.findByUserId(userA.getId(), 1, 6);
            assertEquals(6, page1.size());
            assertEquals(2, page2.size());
            assertTrue(page1.stream().allMatch(v -> v.getUser().getId() == userA.getId()));
            assertTrue(page2.stream().allMatch(v -> v.getUser().getId() == userA.getId()));
        } finally {
            for (String id : createdIds) {
                if (videoService.findById(id) != null) {
                    videoService.delete(id);
                }
            }
            userDao.delete(userA.getId());
            userDao.delete(userB.getId());
        }
    }
}
