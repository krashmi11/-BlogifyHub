package com.example.blog.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.blog.Helpper.helper;
import com.example.blog.Model.BlogLikes;
import com.example.blog.Model.Blogs;
import com.example.blog.Model.Users;
import com.example.blog.Repository.blogRepo;
import com.example.blog.Repository.likeRepo;
import com.example.blog.Repository.userRepo;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class LikeCommentController {

    @Autowired
    private userRepo ur;

    @Autowired
    private blogRepo br;

    @Autowired
    private likeRepo lr;

    @ModelAttribute
    public void addCommon(Model m, Principal p) {
        String username = p.getName();
        Users user = this.ur.getUserByEmail(username);
        m.addAttribute("user", user);

    }

    @PostMapping("/like/{bid}")
    public String get_like(Principal p, @PathVariable("bid") Integer bid, Model m, HttpSession session) {
        Users u = this.ur.getUserByEmail(p.getName());
        Blogs b = this.br.findById(bid).get();
        m.addAttribute("blogs", b);
        BlogLikes like1 = this.lr.findByUserAndBlog(u, b);
        if (like1 != null) {
            this.lr.delete(like1);
            System.out.println("Liked deleed successfully");
            System.out.println();
            session.setAttribute("message", new helper("Like removed🥺🥺", "alert-danger"));

        } else {
            BlogLikes l1 = new BlogLikes();
            l1.setUser(u);
            l1.setBlog(b);
            this.lr.save(l1);
            System.out.println("Blog liked successfully!!");
            System.out.println();
            session.setAttribute("message", new helper("Liked successfully😍😍!!!", "alert-success"));
        }
        return "redirect:/user/view-blogs/" + bid;
    }

}
