package com.practice.blog.post.repository;

import com.practice.blog.post.domain.Post;

import java.util.List;

//QueryDSL 전용 커스텀 리포지토리 인터페이스
//search 메서드로 동적 검색 기능 정의
public interface CustomPostRepository {
    List<Post> search(String keyword, String writerNickname);
}
