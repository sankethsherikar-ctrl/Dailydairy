package com.example.dailydairy.dairy.Controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.dailydairy.dairy.model.DiaryNote;
import com.example.dailydairy.dairy.model.User;
import com.example.dailydairy.dairy.repositories.DiaryNoteRepository;
import com.example.dailydairy.dairy.repositories.UserRepository;

@Controller
@RequestMapping("/notes")
public class DiaryNoteController {

    @Autowired
    private DiaryNoteRepository noteRepo;

    @Autowired
    private UserRepository userRepo;

    // ✅ Redirect root /notes to /notes/page
    @GetMapping
    public String redirectToNewPage() {
        return "redirect:/notes/page";
    }

    // ✅ Load main dashboard with notes and username
    @GetMapping("/page")
    public String notesPage(Model model, Principal principal) {
        Optional<User> optionalUser = userRepo.findByUsername(principal.getName());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<DiaryNote> notes = noteRepo.findByUserOrderByDateDesc(user);
            model.addAttribute("notes", notes);
            model.addAttribute("username", user.getUsername());  // ✅ Fix for showing name
        } else {
            throw new RuntimeException("User not found");
        }
        return "notes-page";
    }

    // ✅ API for showing selected note via JavaScript
    @GetMapping("/api/notes/{id}")
    @ResponseBody
    public Map<String, Object> getNoteDetails(@PathVariable Long id, Principal principal) {
        Optional<DiaryNote> optionalNote = noteRepo.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (optionalNote.isPresent()) {
            DiaryNote note = optionalNote.get();
            if (note.getUser().getUsername().equals(principal.getName())) {
                response.put("title", note.getTitle());
                response.put("date", note.getDate().toString());
                response.put("content", note.getContent());
                response.put("image", Base64.getEncoder().encodeToString(note.getImage()));
            } else {
                response.put("error", "Unauthorized access");
            }
        } else {
            response.put("error", "Note not found");
        }

        return response;
    }

    // ✅ Show add-note form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("note", new DiaryNote());
        return "add-note";
    }

    // ✅ Save a new note, enforcing one per day per user
    @PostMapping("/add")
    public String saveNote(@ModelAttribute DiaryNote note,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           Authentication auth,
                           Model model) throws IOException {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        LocalDate today = note.getDate();

        // Ensure only one note per user per day
        if (noteRepo.findByUserAndDate(user, today).isPresent()) {
            model.addAttribute("error", "You already added a note for this date.");
            return "add-note";
        }

        note.setUser(user);
        note.setImage(imageFile.getBytes());
        noteRepo.save(note);

        return "redirect:/notes/page";
    }
}
