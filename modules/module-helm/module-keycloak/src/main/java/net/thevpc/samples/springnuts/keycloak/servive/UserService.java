/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.eya.securityplatform.security.servive;

import com.eya.securityplatform.security.dto.AuthRequest;
import com.eya.securityplatform.security.dto.UserInfo;
import com.eya.securityplatform.security.dto.AuthResponse;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author aya
 */
public interface UserService {
  
    AuthResponse authenticate(AuthRequest authRequest);

    UserInfo registerUser(String username, String email, String password, List<String> roles);

    Optional<UserInfo> getUserByUsername(String username);

    Optional<UserInfo> getUserById(String userId);

    Optional<UserInfo> getUserByEmail(String email);

    UserInfo updateUser(String userId, UserInfo userInfo);

    boolean deleteUser(String userId);

    boolean hasRole(String userId, String role);

    List<String> getUserRoles(String userId);

    void assignRole(String userId, String role);

    void removeRole(String userId, String role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void setUserEnabled(String userId, boolean enabled);

    boolean changePassword(String userId, String oldPassword, String newPassword);

    List<UserInfo> getAllUsers(int page, int size);

    List<UserInfo> searchUsers(String keyword, int page, int size);
}