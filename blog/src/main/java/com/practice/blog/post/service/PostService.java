package com.practice.blog.post.service;

import com.practice.blog.account.entity.Account;
import com.practice.blog.account.service.AccountService;
import com.practice.blog.global.exception.BlogException;
import com.practice.blog.global.exception.ExceptionCode;
import com.practice.blog.post.domain.Post;
import com.practice.blog.post.dto.summary.PostSummary;
import com.practice.blog.post.dto.request.PostCreateRequest;
import com.practice.blog.post.dto.request.PostUpdateRequest;
import com.practice.blog.post.dto.response.PostResponse;
import com.practice.blog.post.dto.response.PostListResponse;
import com.practice.blog.post.dto.response.PostSearchResponseDto;
import com.practice.blog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AccountService accountService;

    @Transactional
    public Long createPost(PostCreateRequest postCreateRequest) {
        Long accountId = postCreateRequest.accountId();
        Account writerAccount = accountService.findByAccountId(accountId);
        Post newPost = postCreateRequest.toEntity(writerAccount);
        postRepository.save(newPost);
        return newPost.getId();
    }

    @Transactional
    public PostResponse getPost(Long postId) {
        postRepository.increaseViewCount(postId);
        Post post = findByPostId(postId);
        return PostResponse.from(post);
    }

    @Transactional(readOnly = true)
    public PostListResponse getAllPosts() {
        List<PostSummary> postSummaries = postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(PostSummary::from).toList();
        return new PostListResponse(postSummaries, postRepository.count());
    }

    // 검색
    @Transactional(readOnly = true)
    public List<PostSearchResponseDto> searchPost(String keyword, String writerNickname) {
        return postRepository.search(keyword, writerNickname).stream()
                .map(PostSearchResponseDto::new)
                .toList();
    }

    @Transactional
    public void updatePostContent(Long postId, PostUpdateRequest request, Long accountId, String password) {
        Post post = findByPostId(postId);
        Account account = accountService.findByAccountId(accountId);
        authorizePostWriter(post, account, password);
        post.changeContent(request.content());
    }

    @Transactional
    public void deletePost(Long postId, Long accountId, String password) {
        Post post = findByPostId(postId);
        Account account = accountService.findByAccountId(accountId);
        authorizePostWriter(post, account, password);
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()-> new BlogException(ExceptionCode.POST_NOT_FOUND));
    }

    private void authorizePostWriter(Post post, Account account, String password) {
        if(!post.getWriter().equals(account) || !post.getWriter().getPassword().equals(password)) {
            throw new BlogException(ExceptionCode.POST_ACCOUNT_MISMATCH);
        }
    }

}
