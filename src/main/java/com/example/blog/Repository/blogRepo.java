package com.example.blog.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.blog.Model.Blogs;
import com.example.blog.Model.Users;

public interface blogRepo extends JpaRepository<Blogs, Integer> {

    public Page<Blogs> findByUserId(int userId, Pageable pageable);

    public List<Blogs> findByUserId(int userId);

    @Query("SELECT b.user FROM Blogs b WHERE b.bid = :bid")
    public Users findUserByBlogBid(@Param("bid") int bid);
}
