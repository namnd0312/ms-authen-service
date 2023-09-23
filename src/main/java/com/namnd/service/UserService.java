package com.namnd.service;



import com.namnd.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    void save(User user);

    Optional<User> findByUserName(String userName);

    Boolean existsByUsername(String userName);
}
