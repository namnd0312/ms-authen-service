package com.namnd.service.impl;

import com.namnd.model.User;
import com.namnd.model.UserPrinciple;
import com.namnd.repository.UserRepository;
import com.namnd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public Boolean existsByUsername(String userName) {
        return userRepository.existsByUsername(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByUsername(username);

        if(!user.isPresent()){
            throw new UsernameNotFoundException(username);
        }

        return UserPrinciple.build(user.get());
    }
}
