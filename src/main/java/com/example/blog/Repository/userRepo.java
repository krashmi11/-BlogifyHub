package com.example.blog.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.Model.Users;

public interface userRepo extends JpaRepository<Users, Integer> {

    public Users getUserByEmail(String email);
}
