package com.practice.blog.post.repository;

import com.practice.blog.account.entity.QAccount;
import com.practice.blog.post.domain.Post;
import com.practice.blog.post.domain.QPost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> search(String keyword, String writerNickname) {
        QPost post = QPost.post;
        QAccount account = QAccount.account;

        BooleanBuilder builder = new BooleanBuilder();

        if(writerNickname != null && !writerNickname.isBlank()){
            builder.and(post.writer.nickname.eq(writerNickname));
        }

        if(keyword != null && !keyword.isBlank()){
            builder.and(post.title.contains(keyword)
                    .or(post.content.containsIgnoreCase(keyword)));
        }

        return queryFactory.selectFrom(post)
                .join(post.writer, account).fetchJoin()
                .where(builder)
                .orderBy(post.createdAt.desc())
                .fetch();
    }
}
