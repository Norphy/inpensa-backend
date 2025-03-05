package com.orphy.inpensa_backend.v1.service;

import com.orphy.inpensa_backend.v1.data.UserRepository;
import com.orphy.inpensa_backend.v1.model.User;
import com.orphy.inpensa_backend.v1.util.Util;
import com.orphy.inpensa_backend.v1.util.security.annotations.IsAdminRead;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDataInitializationService userDataInitializationService;

    public UserService(UserRepository userRepository,
                       UserDataInitializationService userDataInitializationService) {
        this.userRepository = userRepository;
        this.userDataInitializationService = userDataInitializationService;
    }

    @IsAdminRead
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @PreAuthorize("@authz.isAdminReadOrCurrentUser(authentication, #userId)")
    public User getUserById(String userId) {
        return userRepository.getUserById(userId);
    }

    @PostAuthorize("@authz.isAdminReadOrCurrentUser(authentication, #returnObject.id)")
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @PreAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, #user.id)")
    @PostAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, #returnObject)")
    @SuppressWarnings("UnusedReturnValue")
    public String saveUser(User user) {

        return Util.tryOrElseWithOpt(
                //If user exists update and return existing user
                () -> userRepository.getUserById(user.id()))
                .map(foundUser -> {
                    userRepository.updateUser(user);
                    return foundUser;
                })
                //Else save user and return saved user
                .orElseGet(() -> {
                    //TODO what happens when email already exists: error in DB
                    User savedUser = userRepository.saveUser(user);
                    userDataInitializationService.prepareInitialData();
                    return savedUser;
                }).id();
    }

    @PreAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, #userId)")
    public void delete(String userId) {
        userRepository.deleteUser(userId);
    }
}
