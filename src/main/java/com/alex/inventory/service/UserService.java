package com.alex.inventory.service;

import com.alex.inventory.dto.UserDto;
import com.alex.inventory.entity.Role;
import com.alex.inventory.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);
    void save(UserDto userDto);
    void update(UserDto userDto);
    List<User> findAllUsers();
    User findById(Long id);
    void deleteById(Long id);
    Role findRoleByName(String name);

}
