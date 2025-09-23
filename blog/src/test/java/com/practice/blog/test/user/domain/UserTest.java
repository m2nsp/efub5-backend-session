package com.practice.blog.test.user.domain;

import com.practice.blog.test.user.entity.Role;
import com.practice.blog.test.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("김이화")
                .email("efub@test.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void 유저_생성_정상작동() {
        assertNotNull(user);
        assertEquals("김이화",  user.getName());
        assertEquals("efub@test.com", user.getEmail());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void 닉네임_변경가능() {
        user.changeName("홍길동");
        assertEquals("홍길동", user.getName());

    }

    @Test
    void 권한_변경가능() {
        user.changeRole(Role.ADMIN);
        assertEquals(Role.ADMIN, user.getRole());
    }
}
