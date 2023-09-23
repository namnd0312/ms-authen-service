package com.namnd.dto.mapper;


import com.namnd.dto.request.RegisterDto;
import com.namnd.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RegisterDtoMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User toEntity(RegisterDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();

        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return user;
    }
}
