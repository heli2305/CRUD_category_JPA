package vn.iotstar.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import vn.iotstar.configs.JPAConfig;
import vn.iotstar.dao.UserDao;
import vn.iotstar.entity.User;

public class UserDaoImpl implements UserDao {

    @Override
    public User get(String username) {
        if (username == null) {
            return null;
        }
        EntityManager em = JPAConfig.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.userName = :username", User.class);
            query.setParameter("username", username);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) {
            return null;
        }
        EntityManager em = JPAConfig.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public void insert(User user) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(user);
            trans.commit();
        } catch (RuntimeException e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw new IllegalStateException("Insert user failed", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(User user) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(user);
            trans.commit();
        } catch (RuntimeException e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw new IllegalStateException("Update user failed", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int id) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
            }
            trans.commit();
        } catch (RuntimeException e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw new IllegalStateException("Delete user failed", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean checkExistEmail(String email) {
        return existsByField("email", email);
    }

    @Override
    public boolean checkExistUsername(String username) {
        return existsByField("userName", username);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return existsByField("phone", phone);
    }

    private boolean existsByField(String fieldName, String value) {
        if (value == null) {
            return false;
        }
        EntityManager em = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT COUNT(u) FROM User u WHERE u." + fieldName + " = :value";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("value", value);
            Long count = query.getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }
}
