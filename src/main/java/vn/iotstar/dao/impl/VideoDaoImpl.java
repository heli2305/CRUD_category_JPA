package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import vn.iotstar.configs.JPAConfig;
import vn.iotstar.dao.IVideoDao;
import vn.iotstar.entity.Video;

public class VideoDaoImpl implements IVideoDao {

    @Override
    public void insert(Video video) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(video);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Video video) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(video);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(String videoId) throws Exception {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Video video = em.find(Video.class, videoId);
            if (video != null) {
                em.remove(video);
            } else {
                throw new Exception("Không tìm thấy video: " + videoId);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Video findById(String videoId) {
        if (videoId == null || videoId.isBlank()) {
            return null;
        }
        EntityManager em = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT v FROM Video v LEFT JOIN FETCH v.category LEFT JOIN FETCH v.user WHERE v.videoId = :videoId";
            TypedQuery<Video> query = em.createQuery(jpql, Video.class);
            query.setParameter("videoId", videoId);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video> findAll() {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT v FROM Video v LEFT JOIN FETCH v.category LEFT JOIN FETCH v.user "
                    + "ORDER BY v.createdDate DESC, v.videoId DESC";
            TypedQuery<Video> query = em.createQuery(jpql, Video.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video> findLatestActive(int limit) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT v FROM Video v LEFT JOIN FETCH v.category LEFT JOIN FETCH v.user "
                    + "WHERE v.active = 1 ORDER BY v.createdDate DESC, v.videoId DESC";
            TypedQuery<Video> query = em.createQuery(jpql, Video.class);
            query.setMaxResults(Math.max(1, limit));
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video> findByUserId(int userId, int page, int pageSize) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT v FROM Video v LEFT JOIN FETCH v.category LEFT JOIN FETCH v.user "
                    + "WHERE v.user.id = :userId ORDER BY v.createdDate DESC, v.videoId DESC";
            TypedQuery<Video> query = em.createQuery(jpql, Video.class);
            query.setParameter("userId", userId);
            query.setFirstResult(Math.max(0, page) * Math.max(1, pageSize));
            query.setMaxResults(Math.max(1, pageSize));
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int countByUserId(int userId) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT count(v) FROM Video v WHERE v.user.id = :userId";
            Query query = em.createQuery(jpql);
            query.setParameter("userId", userId);
            return ((Long) query.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    @Override
    public int count() {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT count(v) FROM Video v";
            Query query = em.createQuery(jpql);
            return ((Long) query.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    @Override
    public void incrementViews(String videoId) {
        if (videoId == null || videoId.isBlank()) {
            return;
        }
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Query query = em.createQuery("UPDATE Video v SET v.views = v.views + 1 WHERE v.videoId = :videoId");
            query.setParameter("videoId", videoId);
            query.executeUpdate();
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
