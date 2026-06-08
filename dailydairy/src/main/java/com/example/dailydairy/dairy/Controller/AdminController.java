package com.example.dailydairy.dairy.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dailydairy.dairy.model.Admin;
import com.example.dailydairy.dairy.model.User;
import com.example.dailydairy.dairy.repositories.DiaryNoteRepository;
import com.example.dailydairy.dairy.repositories.UserRepository;
import com.example.dailydairy.dairy.service.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DiaryNoteRepository noteRepo;

    // ✅ Show admin login form
    @GetMapping("/login")
    public String showLoginForm() {
        return "admin-login";
    }

    // ✅ Handle admin login with name or email
    @PostMapping("/login")
    public String processLogin(@RequestParam String identifier,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        Optional<Admin> optionalAdmin = adminService.findByEmailOrName(identifier);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
           // if (passwordEncoder.matches(password, admin.getPassword())){ // It will work when admin is created through web page 
           if (password.equals(admin.getPassword())){ // It will work when we create admin using MYSQL
                session.setAttribute("admin", admin);  // store full object if needed
                return "redirect:/admin/dashboard";
            }
        }
        model.addAttribute("error", "Invalid credentials");
        return "admin-login";
    }

    // ✅ Admin Dashboard – Lists all users with note count
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) return "redirect:/admin/login";

        List<User> users = userRepo.findAll();
        Map<String, Long> userNoteCount = new TreeMap<>();

        for (User user : users) {
            long count = noteRepo.countByUser(user);
            userNoteCount.put(user.getUsername(), count);
        }

        model.addAttribute("adminName", admin.getName());
        model.addAttribute("userNotes", userNoteCount);

        return "admin-dashboard";
    }

    // ✅ Show form to add a new admin
    @GetMapping("/add")
    public String showAddAdminForm(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/admin/login";

        model.addAttribute("admin", new Admin());
        return "add-admin";
    }

    // ✅ Save new admin to DB (check for duplicates)
    @PostMapping("/add")
    public String addAdmin(@ModelAttribute Admin admin, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null)
            return "redirect:/admin/login";

        if (admin.getEmail() == null || admin.getPassword() == null || admin.getName() == null) {
            model.addAttribute("error", "All fields are required");
            return "add-admin";
        }

        Optional<Admin> existing = adminService.findByEmailOrName(admin.getEmail());
        if (existing.isPresent()) {
            model.addAttribute("error", "Admin with this email or name already exists");
            return "add-admin";
        }

        adminService.save(admin);
        return "redirect:/admin/dashboard";
    }

    // ✅ Admin logout — redirect to user login page
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login"; // 🔁 Redirect to main user login page
    }
}
