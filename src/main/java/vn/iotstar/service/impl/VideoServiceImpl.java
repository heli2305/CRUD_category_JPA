package vn.iotstar.service.impl;

import java.util.List;

import vn.iotstar.dao.IVideoDao;
import vn.iotstar.dao.impl.VideoDaoImpl;
import vn.iotstar.entity.Video;
import vn.iotstar.service.IVideoService;

public class VideoServiceImpl implements IVideoService {

    private final IVideoDao videoDao;

    public VideoServiceImpl() {
        this(new VideoDaoImpl());
    }

    public VideoServiceImpl(IVideoDao videoDao) {
        this.videoDao = videoDao;
    }

    @Override
    public void insert(Video video) {
        videoDao.insert(video);
    }

    @Override
    public void update(Video video) {
        videoDao.update(video);
    }

    @Override
    public void delete(String videoId) throws Exception {
        videoDao.delete(videoId);
    }

    @Override
    public Video findById(String videoId) {
        return videoDao.findById(videoId);
    }

    @Override
    public List<Video> findAll() {
        return videoDao.findAll();
    }

    @Override
    public List<Video> findLatestActive(int limit) {
        return videoDao.findLatestActive(limit);
    }

    @Override
    public List<Video> findByUserId(int userId, int page, int pageSize) {
        return videoDao.findByUserId(userId, page, pageSize);
    }

    @Override
    public int countByUserId(int userId) {
        return videoDao.countByUserId(userId);
    }

    @Override
    public int count() {
        return videoDao.count();
    }

    @Override
    public void incrementViews(String videoId) {
        videoDao.incrementViews(videoId);
    }
}
