package com.alex.inventory.controller;

import com.alex.inventory.dto.UserDto;
import com.alex.inventory.entity.Role;
import com.alex.inventory.entity.User;
import com.alex.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inventory/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "users/users";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        List<String> roles = List.of("ROLE_ALL", "ROLE_MODIFY", "ROLE_VIEW");
        model.addAttribute("allRoles", roles);
        return "users/register";
    }

    @PostMapping("/save")
    public String processAddForm(@ModelAttribute("userDto") UserDto userDto) {
        userService.save(userDto);
        return "redirect:/inventory/users";
    }

    @PostMapping("/update")
    public String processUpdateForm(@ModelAttribute("userDto") UserDto userDto) {
        userService.update(userDto);
        return "redirect:/inventory/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        System.out.println("Editing user with ID: " + id);
        User user = userService.findById(id);
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();
        userDto.setRoles(roles);

        model.addAttribute("userDto", userDto);
        List<String> allRoles = List.of("ROLE_ALL", "ROLE_MODIFY", "ROLE_VIEW");
        model.addAttribute("allRoles", allRoles);
        return "users/register";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/inventory/users";
    }
}
