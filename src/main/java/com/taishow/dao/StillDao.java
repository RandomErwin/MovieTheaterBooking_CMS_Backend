package com.taishow.dao;

import com.taishow.entity.Stills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StillDao extends JpaRepository<Stills, Integer> {

    @Query("SELECT s.stills FROM Stills s WHERE s.movieId = ?1")
    List<String> findStillsByMovieId(Integer movieId);
}
