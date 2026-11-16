package vn.iotstar.dao;

import vn.iotstar.entity.User;

public interface UserDao {
    User get(String username);
    User findByEmail(String email);
    void insert(User user);
    void update(User user);
    void delete(int id);
    boolean checkExistEmail(String email);
    boolean checkExistUsername(String username);
    boolean checkExistPhone(String phone);
}
