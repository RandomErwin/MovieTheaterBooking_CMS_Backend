package com.taishow.service.cms;

import com.taishow.dao.MovieDao;
import com.taishow.dto.Result;
import com.taishow.entity.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MovieReleaseService {
    private static final Logger logger = LoggerFactory.getLogger(MovieReleaseService.class);
    private MovieDao movieDao;
    private CacheService cacheService;

    public MovieReleaseService(MovieDao movieDao, CacheService cacheService) {
        this.movieDao = movieDao;
        this.cacheService = cacheService;
    }

    public Result createMovie(Movie movie){
        Movie movies = movieDao.save(movie);
        clearAllCaches();
        return new Result(200, movie);
    }

    public Result updateMovie(Movie movies){
        Movie movie = movieDao.save(movies);
        clearAllCaches();
        return new Result(200, movie);
    }

    public Result deleteMovie(Integer id){
        movieDao.deleteById(id);
        clearAllCaches();
        return new Result(200, "success");
    }

    public Result getMovie(Integer id){
        Optional<Movie> moviesOptional = movieDao.findById(id);
        if(moviesOptional.isPresent()){
            return  new Result(200, moviesOptional.get());
        }else{
            return new Result(999, "No data");
        }
    }

    // server 返回給前端的資料格式: JSON
    // 當第一次調用 getMovieIsComing 方法時， Spring 會執行該方法並且將結果緩存
    // 緩存的鍵：movieIsComing 值：方法的返回結果 => 後續調用：直接從緩存中返回結果，不再執行方法中的邏輯
    @Cacheable(value = "movieIsComing", key = "'movieIsComing'")
    public Result<List<Movie>> getMoviesIsComing(){
        logger.info("取得即將上映的電影.");
        LocalDate today  = LocalDate.now();
        List<Movie> movies = movieDao.findMovieByReleaseDateAfter(
                Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return new Result<>(200, movies);
    }

    @Cacheable(value = "movieIsPlaying", key = "'movieIsPlaying'")
    public Result<List<Movie>> getMoviesIsPlaying(){
        logger.info("取得現正熱映中的電影.");
        LocalDate today = LocalDate.now();
        List<Movie> movies = movieDao.findMovieByReleaseDateBeforeOrEqualAndIsPlayingTrue(
                Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return new Result<>(200, movies);
    }

    public Result updateMovieIsPlayingById(Integer id){
        movieDao.updateMovieIsPlayingById(id);
        return new Result(200, "success");
    }

    public void clearCaches() {
        cacheService.clearCache("movieIsComing");
        cacheService.clearCache("movieIsPlaying");
    }

    public void clearAllCaches() {
        cacheService.clearAllCaches();
    }
}
