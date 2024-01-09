package com.example.blog.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.Model.Comments;
import com.example.blog.Model.Users;

public interface commentRepo extends JpaRepository<Comments, Integer> {

    Page<Comments> findByBlog_bid(Integer bid, Pageable pageable);

    Users findUserByCid(Integer cid);
}
