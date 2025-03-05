package com.orphy.inpensa_backend.v1.data;

import com.orphy.inpensa_backend.v1.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {

    public List<User> getAllUsers();

    public User getUserById(String userId);

    public User getUserByEmail(String email);

    public User saveUser(User user);

    public void updateUser(User user);

    public void deleteUser(String userId);
}
