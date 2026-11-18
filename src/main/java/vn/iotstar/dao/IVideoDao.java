package vn.iotstar.dao;

import java.util.List;

import vn.iotstar.entity.Video;

public interface IVideoDao {

    void insert(Video video);

    void update(Video video);

    void delete(String videoId) throws Exception;

    Video findById(String videoId);

    List<Video> findAll();

    List<Video> findLatestActive(int limit);

    List<Video> findByUserId(int userId, int page, int pageSize);

    int countByUserId(int userId);

    int count();

    void incrementViews(String videoId);
}
