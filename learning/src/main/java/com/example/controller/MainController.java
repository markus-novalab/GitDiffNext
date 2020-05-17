package com.example.controller;

import com.example.entities.MergeRequest;
import com.example.entities.Message;
import com.example.entities.User;
import com.example.repos.MergeRequestRepo;
import com.example.repos.MessageRepo;
import com.example.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


@Controller
public class MainController {
    
    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private MergeRequestRepo mergeRequestRepo;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {

        return "greeting";
    }

    @GetMapping("/review")
    public String review(Map<String, Object> model) {
        Iterable<MergeRequest> requestList = mergeRequestRepo.findAll();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User currentUser = userRepo.findByUsername(username);
        for(MergeRequest request : requestList) {
            if(request.isReviewer(currentUser)) {
                model.put("request", request);
            }
        }
        return "review";
    }

    @PostMapping("/review")
    public String addRequest(@RequestParam String url) {
        if(!url.matches("https?:\\/\\/(www\\.)?" +
                "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")) {
            System.out.println("Bad url!");
            return "/review";
        }
        GitApi gitApi = new GitApi(url);
        MergeRequest mergeRequest = gitApi.GetPR();
        mergeRequestRepo.save(mergeRequest);
        return "review";
    }

    @GetMapping("/main")
    public String main(Map<String, Object> model) {
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag,
            Map<String, Object> model) {
        Message message = new Message(text, tag, user);
        messageRepo.save(message);

        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "main";
    }

    @PostMapping("filter")
    public String filter(@RequestParam String filter,
                         Map<String, Object> model) {
        Iterable<Message> messages;
        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        } else {
            messages = messageRepo.findAll();
        }
        model.put("messages", messages);
        return "main";
    }
}

