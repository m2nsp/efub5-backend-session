package com.practice.blog.post.controller;

import com.practice.blog.post.dto.request.PostCreateRequest;
import com.practice.blog.post.dto.request.PostUpdateRequest;
import com.practice.blog.post.dto.response.PostResponse;
import com.practice.blog.post.dto.response.PostSearchResponseDto;
import com.practice.blog.post.dto.response.PostListResponse;
import com.practice.blog.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    // 게시물 생성
    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody PostCreateRequest request) {
        Long id = postService.createPost(request);
        return ResponseEntity.created(URI.create("/posts/"+id)).build();
    }

    // 게시물 목록 조회
    @GetMapping
    public ResponseEntity<PostListResponse> getAllPosts(){
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<List<PostSearchResponseDto>> searchPost(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "writer", required = false) String writerNickname
    ) {
        return ResponseEntity.ok(postService.searchPost(keyword, writerNickname));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable("id") Long id){
        return ResponseEntity.ok(postService.getPost(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePostContent(@PathVariable("id") Long postId,
                                                  @RequestHeader("Auth-Id") Long accountId,
                                                  @RequestHeader("Auth-Password") String password,
                                                  @RequestBody PostUpdateRequest request) {
        postService.updatePostContent(postId, request, accountId, password);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId,
                                           @RequestHeader("Auth-Id") Long accountId,
                                           @RequestHeader("Auth-Password") String password){
        postService.deletePost(postId, accountId, password);
        return ResponseEntity.noContent().build();
    }
}
