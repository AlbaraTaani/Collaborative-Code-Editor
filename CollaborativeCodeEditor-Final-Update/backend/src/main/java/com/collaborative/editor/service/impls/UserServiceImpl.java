package com.collaborative.editor.service.impls;


import com.collaborative.editor.service.interfaces.UserService;
import com.collaborative.editor.util.JwtUtil;
import com.collaborative.editor.repository.UserRepository;
import com.collaborative.editor.exception.UserNotFoundException;
import com.collaborative.editor.model.User;
import com.collaborative.editor.util.EmailValidator;
import com.collaborative.editor.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("UserServiceImpl")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public void createUser(User user) {

        EmailValidator.validateEmail(user.getEmail());
        PasswordValidator.validatePassword(user.getPassword(), user.getUsername(), user.getEmail());

        if (userRepository.findOneByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Authentication authentication) throws UsernameNotFoundException {
        String email = jwtUtil.getEmail(authentication);
        Optional<User> user = userRepository.findOneByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        try {
            return userRepository.findOneByUsername(username);
        } catch (UserNotFoundException e) {
            throw new RuntimeException("User not found with username: " + username);
        }
    }

}
