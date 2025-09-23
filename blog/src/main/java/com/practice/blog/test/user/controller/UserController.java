package com.practice.blog.test.user.controller;

import com.practice.blog.test.user.dto.UserRequestDTO;
import com.practice.blog.test.user.entity.Role;
import com.practice.blog.test.user.entity.User;
import com.practice.blog.test.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 회원 생성
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        User savedUser = userService.save(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }



    // 회원 조회 (id)
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    // 회원 삭제 (id, role)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @RequestParam String role) {
        Role userRole = Role.valueOf(role); // String → Enum 변환
        User requester = new User();
        requester.setRole(userRole);

        userService.delete(id, requester);
        return ResponseEntity.noContent().build();
    }
}
