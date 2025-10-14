package com.practice.blog.post.service;

import com.practice.blog.account.entity.Account;
import com.practice.blog.account.service.AccountService;
import com.practice.blog.post.domain.Post;
import com.practice.blog.post.dto.response.PostResponse;
import com.practice.blog.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

//    1. 일단 작성하기
//    @Test
//    void PostService_생성_성공() {
//        AccountService accountService = mock(AccountService.class);
//        PostRepository postRepository = mock(PostRepository.class);
//
//        PostService postService = new PostService(postRepository, accountService);
//
//        assertNotNull(postService);
//    }

//  2. @Mock 이용
    @Mock
    AccountService accountService;
    @Mock
    PostRepository postRepository;

    @Test
    void PostService_생성_성공() {
        PostService postService = new PostService(postRepository, accountService);

        assertNotNull(postService);
    }

//  3. Mock 객체 stubbing
    @Test
    void PostService_생성_성공(@Mock PostRepository postRepository, @Mock AccountService accountService) {
        Account account = new Account("efub@example.com", "password", "efub");
        account.setAccountId(1L);

        when(accountService.findByAccountId(1L)).thenReturn(account);   //stubbing

        PostService postService = new PostService(postRepository, accountService);

        Account foundAccount = accountService.findByAccountId(1L);

        assertEquals(1L, foundAccount.getAccountId());
    }

    @Test
    void createPost_작성자조회성공_정상생성() {
        //given
        Account account = new Account("efub@example.com", "testpw", "efub");
        account.setAccountId(1L);

        when(accountService.findByAccountId(1L)).thenReturn(account);

        //when
        Account found = accountService.findByAccountId(1L);

        //then
        assertEquals(1L, found.getAccountId());
    }

    @Test
    void deletePost_중_postRepository_delete에서_예외_doThrow() {
        //given
        Account account = new Account("efub@example.com", "testpw", "efub");
        account.setAccountId(1L);

        Post post = new Post("제목", "내용", account);
        post.setId(10L);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(accountService.findByAccountId(1L)).thenReturn(account);

        //doThrow: void 메서드 delete(...)가 호출되면 예외 발생
        doThrow(new IllegalArgumentException("삭제 실패"))
                .when(postRepository).delete(any(Post.class));

        PostService postService = new PostService(postRepository, accountService);

        //then
        assertThrows(IllegalArgumentException.class,
                () -> postService.deletePost(10L, 1L, "testpw"));
    }

    @Test
    void findByAccountId_2번째호출_RuntimeException() {
        //given
        Account a1 = new Account("efub1@example.com", "testpw1", "efub1"); a1.setAccountId(1L);
        Account a2 = new Account("efub2@example.com", "testpw2", "efub2"); a2.setAccountId(2L);

        // any()로 파라미터 매칭 + thenReturn -> thenThrow -> thenReturn
        when(accountService.findByAccountId(any()))
                .thenReturn(a1)                                    // 첫 번째 호출
                .thenThrow(new RuntimeException("두 번째 실패"))     // 두 번째 호출
                .thenReturn(a2);                                  //세 번쨰 호출

        PostService postService = new PostService(postRepository, accountService);

        // 1) 첫 호출 - a1 반환
        Account first = accountService.findByAccountId(111L);
        assertEquals(1L, first.getAccountId());

        // 2) 두 번째 호출 - 예외 발생
        assertThrows(RuntimeException.class,
                () -> accountService.findByAccountId(222L));

        // 3) 세 번째 호출 : a2 반환
        Account third = accountService.findByAccountId(333L);
        assertEquals(2L, third.getAccountId());
    }

    @Test
    void getPost_조회수증가_호출검증() {
        //given
        Account writer = new Account("efub@example.com", "testpw", "efub" );
        Post post = new Post("제목", "내용", writer);
        post.setId(5L);

        when(postRepository.findById(5L)).thenReturn(Optional.of(post));
        PostService postService = new PostService(postRepository, accountService);

        //when
        PostResponse res = postService.getPost(5L);

        //then
        assertNotNull(res);
        verify(postRepository).increaseViewCount(5L);       // 정확히 한 번 호출됐는지
        verify(postRepository, times(1)).findById(5L);      //호출 횟수까지 검증
        verifyNoMoreInteractions(postRepository);                  // 그 외 상호작용 없음
    }

}