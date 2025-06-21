package com.collaborative.editor.service.impls;

import com.collaborative.editor.repository.UserRepository;
import com.collaborative.editor.exception.UserNotFoundException;
import com.collaborative.editor.model.User;
import com.collaborative.editor.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EditorUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepo.findOneByEmail(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with email '" + username + "' not found");
        }
        return new CustomUserDetails(user.get());
    }

}
