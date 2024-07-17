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
    private RedisTemplate<String, Object> redisTemplate;

    public MovieReleaseService(MovieDao movieDao) {
        this.movieDao = movieDao;
    }

    public void clearAllCaches() {
        logger.info("Clearing all caches in Redis");
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    public Result createMovie(Movie movie){
        Movie movies = movieDao.save(movie);
        return new Result(200, movie);
    }

    public Result updateMovie(Movie movies){
        Movie movie = movieDao.save(movies);
        return new Result(200, movie);
    }

    public Result deleteMovie(Integer id){
        movieDao.deleteById(id);
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
    @Cacheable(value = "movieIsComing", key = "'movieIsComing'")
    public Result<List<Movie>> getMoviesIsComing(){
        logger.info("Fetching movies that are coming soon.");
        LocalDate today  = LocalDate.now();
        List<Movie> movies = movieDao.findMovieByReleaseDateAfter(
                Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return new Result<>(200, movies);
    }

    @Cacheable(value = "movieIsPlaying", key = "'movieIsPlaying'")
    public Result<List<Movie>> getMoviesIsPlaying(){
        logger.info("Fetching movies that are currently playing.");
        LocalDate today = LocalDate.now();
        List<Movie> movies = movieDao.findMovieByReleaseDateBeforeOrEqualAndIsPlayingTrue(
                Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return new Result<>(200, movies);
    }

    public Result updateMovieIsPlayingById(Integer id){
        movieDao.updateMovieIsPlayingById(id);
        return new Result(200, "success");
    }
}
