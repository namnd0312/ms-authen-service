package com.namnd.service;


import com.namnd.model.Role;

public interface RoleService {

    void save(Role role);

    Role findByName(String name);

    //Đẩy thay đổi vào DB để query lại
    void flush();
}
