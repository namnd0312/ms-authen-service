package com.namnd.controller;

import com.namnd.Enum.ErrorCode;
import com.namnd.dto.mapper.RegisterDtoMapper;
import com.namnd.dto.request.RegisterDto;
import com.namnd.dto.response.JwtResponseDto;
import com.namnd.model.Role;
import com.namnd.model.User;
import com.namnd.service.RoleService;
import com.namnd.service.UserService;
import com.namnd.service.impl.JwtService;
import com.namnd.utils.ResponseApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RegisterDtoMapper registerDtoMapper;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateTokenLogin(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByUserName(user.getUsername()).get();
        return ResponseEntity.ok(new ResponseApi<>().ok(new JwtResponseDto(currentUser.getId(), jwt, userDetails.getUsername(), currentUser.getFullName(), userDetails.getAuthorities())));
    }

    @PostMapping("/register")
    public ResponseApi<?> registerUser(@RequestBody RegisterDto registerDto) {
        if(userService.existsByUsername(registerDto.getUsername())) {
            return new ResponseApi<>().error(ErrorCode.INVALID_INPUT.getCode(), "Fail -> Username is already taken!");
        }

        Set<Role> roles = registerDto.getRoles();

        for (Role role: roles) {
            if(roleService.findByName(role.getName()) == null){
                roleService.save(role);
                roleService.flush();
            }else {
                role.setId(roleService.findByName(role.getName()).getId());
            }
        }

        Optional<User> user = this.userService.findByUserName(registerDto.getUsername());

        if(user.isPresent()){
            return new ResponseApi<>().error(ErrorCode.INVALID_INPUT);
        }
        User user1 = registerDtoMapper.toEntity(registerDto);
        userService.save(user1);

        return new ResponseApi<>().ok();
    }
}
