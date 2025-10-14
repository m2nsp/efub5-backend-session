package com.practice.blog.post.controller;

import com.practice.blog.account.entity.Account;
import com.practice.blog.account.repository.AccountsRepository;
import com.practice.blog.post.domain.Post;
import com.practice.blog.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PostControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountsRepository accountsRepository;
    @Autowired PostRepository postRepository;

    @BeforeEach
    void seed() {
        Account account = new Account("efub@example.com", "testpw", "efub");
        accountsRepository.save(account);
    }

    @Test
    @DisplayName("POST /posts → 201, Location 헤더 & H2에 실제 저장")
    void createPost_and_persist() throws Exception {
        // given
        String body = """
            {"title":"제목", "content":"내용은다섯글자이상","accountId":1}    
        """;

        // when
        MvcResult res = mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("^/posts/\\d+$")))
                .andReturn();

        // then
        String location = res.getResponse().getHeader("Location");
        long id = Long.parseLong(java.net.URI.create(location).getPath().replace("/posts/", ""));
        assertTrue(postRepository.findById(id).isPresent());

    }

    @Test
    @DisplayName("GET /posts/{id} → 200 & 응답 필드 검증")
    void getPost_200() throws Exception {
        // given
        Account account = accountsRepository.findAll().get(0);
        Post post = new Post("제목", "내용은다섯글자이상", account);
        post = postRepository.save(post);

        // when then
        mockMvc.perform(get("/posts/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"));
    }
}
