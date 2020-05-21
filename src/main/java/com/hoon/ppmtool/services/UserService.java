package com.hoon.ppmtool.services;

import com.hoon.ppmtool.domain.User;
import com.hoon.ppmtool.exeptions.UsernameExistsException;
import com.hoon.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(User newUser) {
        try {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            newUser.setConfirmpassword("");     // clear confirm password
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new UsernameExistsException("Username[" + newUser.getUsername() + "] already exists.");
        }
    }

}
