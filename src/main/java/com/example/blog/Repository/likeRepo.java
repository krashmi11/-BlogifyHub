package com.example.blog.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.blog.Model.Blogs;
import com.example.blog.Model.BlogLikes;
import com.example.blog.Model.Users;

public interface likeRepo extends JpaRepository<BlogLikes, Integer> {

    @Query("SELECT l FROM BlogLikes l WHERE l.user = :u AND l.blog = :b")
    BlogLikes findByUserAndBlog(Users u, Blogs b);
}
