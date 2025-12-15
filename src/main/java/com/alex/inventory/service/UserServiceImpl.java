package com.alex.inventory.service;

import com.alex.inventory.dao.RoleRepository;
import com.alex.inventory.dao.UserRepository;
import com.alex.inventory.dto.UserDto;
import com.alex.inventory.entity.Role;
import com.alex.inventory.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void save(UserDto userDto) {
        // Create new user
        User existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("User already exists");
        }

        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (newUser.getRoles() == null) {
            newUser.setRoles(new java.util.ArrayList<>());
        }

        if (userDto.getRoles() != null) {
            for (String roleName : userDto.getRoles()) {
                Role role = roleRepository.findByName(roleName);
                if (role == null) {
                    throw new IllegalArgumentException("Role not found: " + roleName);
                }
                newUser.getRoles().add(role);
            }
        }

        userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void update(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId()).orElse(null);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found with ID: " + userDto.getId());
        }

        // Update username
        existingUser.setUsername(userDto.getUsername());
        // Update password only if a new password is provided
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        // Clear existing roles and set new roles
        existingUser.getRoles().clear();
        if (userDto.getRoles() != null) {
            for (String roleName : userDto.getRoles()) {
                Role role = roleRepository.findByName(roleName);
                if (role != null) {
                    existingUser.getRoles().add(role);
                }
            }
        }

        userRepository.save(existingUser);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Role findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }


    private List<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
