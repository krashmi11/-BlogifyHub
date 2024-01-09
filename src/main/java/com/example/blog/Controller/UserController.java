package com.example.blog.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.Helpper.helper;
import com.example.blog.Model.BlogLikes;
import com.example.blog.Model.Blogs;
import com.example.blog.Model.Comments;
import com.example.blog.Model.Users;
import com.example.blog.Repository.blogRepo;
import com.example.blog.Repository.commentRepo;
import com.example.blog.Repository.likeRepo;
import com.example.blog.Repository.userRepo;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private userRepo ur;

    @Autowired
    private blogRepo br;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private commentRepo cr;

    @Autowired
    private likeRepo lr;

    Blogs b = new Blogs();

    @ModelAttribute
    public void addCommon(Model m, Principal p) {
        String username = p.getName();
        Users user = this.ur.getUserByEmail(username);
        m.addAttribute("user", user);

    }

    @GetMapping("/index/{page}")
    public String userHome(Model m, @PathVariable("page") Integer page) {
        m.addAttribute("title", "Home");
        Pageable pageable = PageRequest.of(page, 3);
        Page<Blogs> blogs = this.br.findAll(pageable);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", blogs.getTotalPages());
        m.addAttribute("blogs", blogs);
        return "User/Home";
    }

    @GetMapping("/view-blogs/{bid}")
    public String view(Model m, @PathVariable("bid") Integer bid, Principal p) {
        m.addAttribute("title", "view-blog");
        Blogs blogs = this.br.findById(bid).get();
        Users u = this.ur.getUserByEmail(p.getName());
        BlogLikes like1 = this.lr.findByUserAndBlog(u, blogs);
        m.addAttribute("like1", like1);
        m.addAttribute("blogs", blogs);
        return "User/view_blog";
    }

    @PostMapping("/view-my-blogs-content/{bid}")
    public String view_my_blog(Model m, @PathVariable("bid") Integer bid, Principal p) {
        m.addAttribute("title", "view-blog");
        Blogs blogs = this.br.findById(bid).get();
        Users u = this.ur.getUserByEmail(p.getName());
        BlogLikes like1 = this.lr.findByUserAndBlog(u, blogs);
        m.addAttribute("like1", like1);
        m.addAttribute("blogs", blogs);
        return "User/view_blog";
    }

    @GetMapping("/write")
    public String write(Model m, HttpSession session) {
        m.addAttribute("blog", new Blogs());
        return "User/write_blog";
    }

    @PostMapping("/do-write")
    public String my_blog(@ModelAttribute("blog") Blogs blog, Model m, Principal p,
            @RequestParam("blogImage") MultipartFile file, HttpSession session) {
        m.addAttribute("title", "writing..");
        try {
            if (file.isEmpty()) {
                blog.setBimage("Blogger1.jpg");
            }
            if (!file.isEmpty()) {
                blog.setBimage(file.getOriginalFilename());

                // Define the absolute path for the image storage directory
                String uploadDirectory = "D:\\SpringBoot\\blogImages";

                // Create the directory if it doesn't exist
                File uploadDir = new File(uploadDirectory);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Save the file to the specified directory
                Path path = Paths.get(uploadDirectory + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image uploaded");
            }
            blog.setUser(this.ur.getUserByEmail(p.getName()));
            this.br.save(blog);

        } catch (Exception e) {
            session.setAttribute("message", new helper("Something went wrong!!" + e.getMessage(), "alert-danger"));
        }

        return "User/blogpage";
    }

    @PostMapping("/process-blog/{bid}")
    public String process_blog(@ModelAttribute("blog") Blogs blog, @PathVariable("bid") Integer bid, Model m,
            HttpSession session) {
        m.addAttribute("title", "process-blog");
        Blogs b = this.br.findById(bid).get();
        b.setContent(blog.getContent());
        b.setBname(blog.getBname());
        this.br.save(b);
        session.setAttribute("message", new helper("Blog saved successfully", "alert-success"));
        m.addAttribute("blog", new Blogs());
        return "User/write_blog";
    }

    @GetMapping("/view-my-blog/{page}")
    public String view_my_blog(@PathVariable("page") Integer page, Model m, Principal p) {
        m.addAttribute("title", "Blog-My-blogs");
        Users user = this.ur.getUserByEmail(p.getName());
        Pageable pageable = PageRequest.of(page, 3);
        Page<Blogs> blogs = this.br.findByUserId(user.getId(), pageable);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", blogs.getTotalPages());
        m.addAttribute("blog", blogs);
        return "User/my_blog";
    }

    @GetMapping("/delete/{bid}")
    public String delete_blog(Principal p, @PathVariable("bid") Integer bid, HttpSession session) {
        Users user = this.ur.getUserByEmail(p.getName());
        Blogs blog = this.br.findById(bid).get();
        if (user.getId() == blog.getUser().getId()) {
            blog.setUser(null);
            this.br.delete(blog);
            System.out.println("Blog deleted successfully");
            session.setAttribute("message", new helper("Blog deleted successfully!", "alert-success"));
        } else {
            session.setAttribute("message", new helper("Something went wrong!!", "alert-danger"));

        }
        return "redirect:/user/view-my-blog/0";
    }

    @PostMapping("/update_blog/{bid}")
    public String update_blog(Model m, @PathVariable("bid") Integer bid) {
        Blogs blog = this.br.findById(bid).get();
        m.addAttribute("blog", blog);
        m.addAttribute("title", "update blog");
        return "User/update_blog";
    }

    @GetMapping("/my_profile/{id}")
    public String my_profile(Model m, @PathVariable("id") Integer id, Principal p) {
        m.addAttribute("title", "My_Profile");
        List<Blogs> al = this.br.findByUserId(id);
        Users u = this.ur.findById(id).get();
        int count = 0;
        int comm = 0;
        m.addAttribute("bsize", al.size());
        for (Blogs b : u.getBlogs()) {
            count += b.getUserlikes().size();
            comm += b.getComments().size();
        }
        m.addAttribute("likes", count);
        m.addAttribute("getComment", comm);

        return "User/profile";
    }

    @GetMapping("/upload-picture/{id}")
    public String upload_pic(Model m, @PathVariable("id") Integer id, Principal p) {
        Users user = this.ur.findById(id).get();
        if (user.equals(this.ur.getUserByEmail(p.getName()))) {
            m.addAttribute("title", "upload-picture");
            m.addAttribute("user", user);
            return "User/photo_upload";
        }
        return "User/my_profile";

    }

    // USER PROFILE UPDATION
    @PostMapping("/process-upload-picture")
    public String process_upload_pic(@ModelAttribute("updated") Users updatedUser, Principal p,
            @RequestParam("profileImage") MultipartFile file, @RequestParam("id") Integer id,
            HttpSession session) {
        String username = p.getName();
        Users currentUser = this.ur.getUserByEmail(username);

        try {
            if (file.isEmpty()) {
                System.out.println("Image is already there");
            }
            if (!file.isEmpty()) {
                currentUser.setImageurl(file.getOriginalFilename());

                // Define the absolute path for the image storage directory
                String uploadDirectory = "D:\\SpringBoot\\blogImages";

                // Create the directory if it doesn't exist
                File uploadDir = new File(uploadDirectory);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                File oldFile = new File(uploadDirectory, currentUser.getImageurl());
                if (oldFile.exists() && !oldFile.getName().equals("default.png")) {
                    boolean deleted = oldFile.delete();
                    if (!deleted) {
                        // Handle deletion failure if necessary
                        System.out.println("Failed to delete the old file: " +
                                oldFile.getAbsolutePath());
                    }
                    System.out.println("Old file deleted!!");
                }

                // Save the file to the specified directory
                Path path = Paths.get(uploadDirectory + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image uploaded");

                session.setAttribute("message", new helper("Image Uploaded!!", "alert-success"));
            }

            currentUser.setUname(updatedUser.getUname());
            currentUser.setDOB(updatedUser.getDOB());
            currentUser.setMobile(updatedUser.getMobile());
            currentUser.setUserdesc(updatedUser.getUserdesc());

            this.ur.save(currentUser);

        } catch (Exception e) {
            session.setAttribute("message", new helper("Something went wrong!!", "alert-danger"));
        }

        return "redirect:/user/my_profile/" + id;
    }

    @GetMapping("/view-user-profile/{id}")
    public String user_profiles(Model m, @PathVariable("id") Integer id, Principal p) {
        m.addAttribute("title", "User_Profile");
        Users u = this.ur.findById(id).get();
        m.addAttribute("u", u);
        List<Blogs> al = this.br.findByUserId(id);
        m.addAttribute("bsize", al.size());
        return "User/currentuser_profiles";
    }

    @GetMapping("/setting")
    public String my_setting() {
        return "User/setting";
    }

    @GetMapping("/post-comment/{bid}/{page}")
    public String createComment(Model m, @PathVariable("bid") Integer bid, @PathVariable("page") Integer page) {
        m.addAttribute("comment", new Comments());
        m.addAttribute("bid", bid);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Comments> commentList = this.cr.findByBlog_bid(bid, pageable);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", commentList.getTotalPages());
        m.addAttribute("commentList", commentList);
        for (Comments c : commentList) {
            System.out.println(c.getComment());
            System.out.println();
        }
        return "User/createComment";
    }

    @PostMapping("/post-comment/{bid}/{cid}")
    public String process_comment(@PathVariable("bid") Integer bid, @PathVariable("cid") Integer cid,
            @ModelAttribute("comment") Comments com, Principal p, HttpSession session) {
        try {
            Comments c = new Comments();
            c.setComment(com.getComment());
            Users u = this.ur.getUserByEmail(p.getName());
            c.setUser(u);
            c.setBlog(this.br.findById(bid).get());
            this.cr.save(c);
            System.out.println("post successfully");
            session.setAttribute("message", new helper("comment posted!!", "alert-success"));
        } catch (Exception e) {
            session.setAttribute("message", new helper("Something Went Wrong!!" + e.getMessage(), "alert-danger"));
        }

        return "redirect:/user/post-comment/{bid}/0";
    }

    @GetMapping("/delete-comment/{bid}/{cid}")
    public String deleteComment(Model m, Principal p, @PathVariable("cid") Integer cid, HttpSession session)
            throws UsernameNotFoundException {
        Users current = this.ur.getUserByEmail(p.getName());

        // Check if current is present and not null
        if (current != null) {
            Comments c = this.cr.findById(cid).get();
            System.out.println(c.getComment() + " " + c.getUser().getUname());
            // Check if the current user is the owner of the comment
            if (current.equals(c.getUser())) {
                this.cr.delete(c);
                System.out.println("Deleted Successfully");
                session.setAttribute("message", new helper("Comment deleted successfully!!", "alert-success"));
            } else {
                session.setAttribute("message", new helper("Invalid user!!", "alert-danger"));
            }
        } else {
            session.setAttribute("message", new helper("User not found!!", "alert-danger"));
        }

        return "redirect:/user/post-comment/{bid}/0";
    }

    @GetMapping("/update-blog-image/{bid}")
    public String blog_pic_update(
            @PathVariable("bid") Integer bid, Principal p, Model m) {
        Users u = this.ur.getUserByEmail(p.getName());
        if (u.equals(this.br.findUserByBlogBid(bid))) {
            Blogs blog = this.br.findById(bid).get();
            m.addAttribute("blog", blog);
            return "User/blog_pic_update";
        }

        return "redirect:/user/view-my-blog/0";
    }

    @PostMapping("/process-blog-update-image/{bid}")
    public String blog_pic_update_process(@PathVariable("bid") Integer bid,
            @RequestParam("profileImage") MultipartFile file, HttpSession session, Principal p) throws IOException {
        Blogs oldblog = this.br.findById(bid).get();

        if (file.isEmpty()) {
            System.out.println("Already image is there");
        }
        if (!file.isEmpty()) {
            String uploadDirectory = "D:\\SpringBoot\\blogImages";

            // Create the directory if it doesn't exist
            File uploadDir = new File(uploadDirectory);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            File oldFile = new File(uploadDirectory, oldblog.getBimage());
            if (oldFile.exists() && !oldFile.getName().equals("Blogger1.jpg")) {
                boolean deleted = oldFile.delete();
                if (!deleted) {
                    // Handle deletion failure if necessary
                    System.out.println("Failed to delete the old file: " +
                            oldFile.getAbsolutePath());
                }
                System.out.println("Old file deleted!!");
            }
            // Save the file to the specified directory
            Path path = Paths.get(uploadDirectory + File.separator + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            oldblog.setBimage(file.getOriginalFilename());
            this.br.save(oldblog);
            System.out.println("Image uploaded");
            session.setAttribute("message", new helper("Image uploaded successfully", "alert-success"));
        }

        return "redirect:/user/view-my-blog/0";
    }

    @PostMapping("/change-password")
    public String change_Password(@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass,
            Model m, Principal p, HttpSession session) {
        Users user = this.ur.getUserByEmail(p.getName());
        if (bCryptPasswordEncoder.matches(oldpass, user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(newpass));
            this.ur.save(user);
            session.setAttribute("message", new helper("Password Changed successfully", "alert-success"));
            return "redirect:/user/index/0";
        } else {
            session.setAttribute("message", new helper("Please enter correct old Password", "alert-danger"));
            return "redirect:/user/setting";
        }
    }
}
