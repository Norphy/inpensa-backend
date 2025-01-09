package com.orphy.inpensa_backend.data;

import com.orphy.inpensa_backend.model.User;

import java.util.UUID;

public interface UserRepository {

    public User saveUser(User user);

    public User getUser(String id);

    public void delete(User user);
}
