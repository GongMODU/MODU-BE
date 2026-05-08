package com.gong.modu.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 관리자용 API에 접근할 때 X-ADMIN-KEY 헤더 값을 검사하는 필터
// /api/youtube/admin/** 경로에 대해서만 동작
public class AdminApiKeyFilter extends OncePerRequestFilter  {

    // 클라이언트가 보내야 하는 헤더 이름
    private static final String ADMIN_KEY_HEADER = "X-ADMIN-KEY";

    // application.properties에서 읽어온 관리자 키
    @Value("${admin.youtube.key}")
    private String adminYoutubeKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // /api/youtube/admin/** 경로가 아니면 이 필터 적용 X
        return !uri.startsWith("/api/youtube/admin/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestAdminKey = request.getHeader(ADMIN_KEY_HEADER);

        // 헤더가 없거나, 서버에 저장된 관리자 키와 다르면 차단
        if (requestAdminKey == null || !requestAdminKey.equals(adminYoutubeKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {
                      "message": "Invalid admin key"
                    }
                    """);
            return;
        }

        // 키가 맞으면 다음 필터/컨트롤러로 요청 넘김
        filterChain.doFilter(request, response);
    }
}
