package com.example.blog.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.Model.Users;
import com.example.blog.Repository.userRepo;

@Service
public class UserService {

    @Autowired
    private userRepo ur;

    public Users findByUsernameInTransaction(String email) throws UsernameNotFoundException {
        Users user = ur.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        return user;
    }

    @Transactional
    public void updateCurrentUserDetails(Users updatedUser) {
        ur.save(updatedUser);
    }
}
