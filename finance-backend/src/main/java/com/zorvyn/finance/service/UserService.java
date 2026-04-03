package com.zorvyn.finance.service;

import com.zorvyn.finance.common.exception.BadRequestException;
import com.zorvyn.finance.common.exception.ResourceNotFoundException;
import com.zorvyn.finance.dto.request.UpdateRoleRequest;
import com.zorvyn.finance.dto.request.UpdateStatusRequest;
import com.zorvyn.finance.dto.response.UserResponse;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
        return UserResponse.from(user);
    }
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.from(user);
    }
    @Transactional
    public UserResponse updateRole(UUID id, UpdateRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
        user.setRole(request.getRole());
        return UserResponse.from(userRepository.save(user));
    }
    @Transactional
    public UserResponse updateStatus(UUID id, UpdateStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
        user.setIsActive(request.getIsActive());
        return UserResponse.from(userRepository.save(user));
    }
}