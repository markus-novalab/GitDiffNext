package com.diffreviewer.controller;

import com.diffreviewer.entities.ListTask;
import com.diffreviewer.entities.User;
import com.diffreviewer.repos.ListTaskRepo;
import com.diffreviewer.repos.MergeRequestRepo;
import com.diffreviewer.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    private final UserRepo userRepo;

    private final ListTaskRepo listTaskRepo;

    private final MergeRequestRepo mergeRequestRepo;

    @Autowired
    public UserController(UserRepo userRepo, ListTaskRepo listTaskRepo, MergeRequestRepo mergeRequestRepo) {
        this.userRepo = userRepo;
        this.listTaskRepo = listTaskRepo;
        this.mergeRequestRepo = mergeRequestRepo;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("curse", "example");
        model.addAttribute("login", "example58");
        model.addAttribute("role", "admin");

        model.addAttribute("requests", mergeRequestRepo.findAll());
        return "profile-admin";
    }

    @GetMapping("/admin-panel")
    public String adminPanel(Model model) {
        Iterable<User> users = userRepo.findAll();
        Iterable<ListTask> tasks = listTaskRepo.findAll();

        model.addAttribute("users", users);
        model.addAttribute("tasks", tasks);

        return "admin-panel";
    }


    @PostMapping("/deleteTask")
    public String deleteTaskRow(@ModelAttribute("ListTaskModel") ListTask toDeleteTask) {
        listTaskRepo.delete(toDeleteTask);

        return "admin-panel";
    }

    @RequestMapping(value = "/insertTask", method = RequestMethod.POST)
    public String insertTaskRow(@ModelAttribute("ListTaskModel") ListTask insertTask) {
        listTaskRepo.save(insertTask);

        return "admin-panel";
    }

    @RequestMapping(value = "/changeTask", method = RequestMethod.POST)
    public String changeTaskRow(@ModelAttribute("ListTaskModel") ListTask toChangeTask) {
        List<ListTask> tasks = new ArrayList<>();
        listTaskRepo.findAll().forEach(tasks::add);

        for (ListTask task : tasks) {
            if (task.getName() == toChangeTask.getName() ||
                    task.getPrevious() == toChangeTask.getPrevious() ||
                    task.getTaskLevel() == toChangeTask.getTaskLevel()) {
                toChangeTask.setId(task.getId());
                break;
            }
        }

        listTaskRepo.save(toChangeTask);

        return "admin-panel";
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public String deleteUserRow(@ModelAttribute("ListTaskModel") User toDeleteUser) {
        userRepo.delete(toDeleteUser);

        return "admin-panel";
    }

    @RequestMapping(value = "/insertUser", method = RequestMethod.POST)
    public String insertUserRow(@ModelAttribute("ListTaskModel") User insertUser) {
        userRepo.save(insertUser);

        return "admin-panel";
    }

    @RequestMapping(value = "/changeUser", method = RequestMethod.POST)
    public String changeUserRow(@ModelAttribute("ListTaskModel") User toChangeUser) {
        List<User> users = new ArrayList<>(userRepo.findAll());

        for (User user : users) {
            if (user.getUsername().equals(toChangeUser.getUsername()) ||
                    user.getPassword().equals(toChangeUser.getPassword())) {
                toChangeUser.setId(user.getId());
                break;
            }
        }

        userRepo.save(toChangeUser);

        return "admin-panel";
    }
}
