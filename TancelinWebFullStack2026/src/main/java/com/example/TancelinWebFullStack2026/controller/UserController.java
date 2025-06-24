package com.example.TancelinWebFullStack2026.controller;

import com.example.TancelinWebFullStack2026.dto.BaseResponseDto;
import com.example.TancelinWebFullStack2026.dto.UserLoginDto;
import com.example.TancelinWebFullStack2026.model.User;
import com.example.TancelinWebFullStack2026.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public BaseResponseDto registerUser(@RequestBody User user) {
        if(userService.createUser(user)) {
            return new BaseResponseDto("success");
        } else {
            return new BaseResponseDto("failed");
        }
    }

    @PostMapping("/auth/login")
    public BaseResponseDto loginUser(@RequestBody UserLoginDto userLoginDto) {
        if(userService.checkUserNameExists(userLoginDto.getEmail())) {
            if(userService.verifyUser(userLoginDto.getEmail(), userLoginDto.getPassword())) {
                Map<String, String> data = new HashMap<>();
                data.put("token", userService.generateToken(userLoginDto.getEmail(), userLoginDto.getPassword()));
                return new BaseResponseDto("success", data);
            } else {
                return new BaseResponseDto("password failed");
            }
        } else {
            return new BaseResponseDto("user not exist");
        }
    }

    @GetMapping("/users/{id}")
    public BaseResponseDto getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> new BaseResponseDto("success", user))
                .orElse(new BaseResponseDto("user not found"));
    }

    @GetMapping("/users")
    public BaseResponseDto getAllUsers() {
        return new BaseResponseDto("success", userService.findAll());
    }

    @PutMapping("/users/{id}")
    public BaseResponseDto updateUser(@PathVariable Long id, @RequestBody User user) {
        if(userService.updateUser(id, user)) {
            return new BaseResponseDto("success");
        } else {
            return new BaseResponseDto("user not found");
        }
    }

    @DeleteMapping("/users/{id}")
    public BaseResponseDto deleteUser(@PathVariable Long id) {
        if(userService.deleteUser(id)) {
            return new BaseResponseDto("success");
        } else {
            return new BaseResponseDto("user not found");
        }
    }
}
