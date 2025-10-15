package com.practice.blog.global.handler;

import com.practice.blog.account.repository.AccountsRepository;
import com.practice.blog.account.entity.Account;
import com.practice.blog.global.jwt.TokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AccountsRepository accountsRepository;
    private final TokenProvider tokenProvider;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        //OAuth2 인증된 사용자 정보 가져오기
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        //사용자 속성에서 이메일 추출
        String email = (String) oAuth2User.getAttributes().get("email");

        // email을 통해 데이터베이스에서 User 엔티티 조회
        Account account = accountsRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 email를 가진 Account를 찾을 수 없습니다. email="+email));

        // AccessToken, RefreshToken 발급
        String accessToken = tokenProvider.createAccessToken(account);
        String refreshToken = tokenProvider.createRefreshToken(account);

        // 리프레시토큰 저장
        tokenProvider.saveRefreshToken(account.getAccountId(), refreshToken);

        // JSON형식 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // AccessToken, RefreshToken 토큰 전달
        String jsonResponse = String.format("{\"accessToken\":\"$s\", \"refreshToken\":\"%s\"}", accessToken, refreshToken);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
