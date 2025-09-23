package com.practice.blog.test.user.service;

import com.practice.blog.test.user.dto.UserRequestDTO;
import com.practice.blog.test.user.entity.User;
import com.practice.blog.test.user.entity.Role;
import com.practice.blog.test.user.repository.UserRepository;
import com.practice.blog.test.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;


import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private Validator validator;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        testUser = User.builder()
                .id(1L)
                .name("김이화")
                .email("efub@test.com")
                .role(Role.USER)
                .build();
    }

    // 중복 이메일 방지
    @Test
    void 중복된_이메일이면_예외발생() {
        //given
        UserRequestDTO dto = new UserRequestDTO("홍길동", "efub@test.com");
        given(userRepository.existsByEmail(dto.getEmail())).willReturn(true);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> userService.save(dto));
    }




    // 권한 검사
    @Test
    void 관리자가_아니면_삭제_불가() {
        //given
        User 일반유저 = testUser;

        //when & then
        assertThrows(IllegalArgumentException.class,
                () -> userService.delete(일반유저.getId(), 일반유저));
    }




    // 입력 유효성 검증 (이메일 형식)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "not-an-email"})
    void 잘못된_이메일형식이면_검증에러(String invalidEmail) {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("홍길동")
                .email(invalidEmail)
                .build();
        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }





    // 정상 조회
    @Test
    void 아이디로_사용자조회() {
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

        User result = userService.findById(1L);

        assertEquals("김이화", result.getName());
        assertEquals("efub@test.com", result.getEmail());
    }



}
