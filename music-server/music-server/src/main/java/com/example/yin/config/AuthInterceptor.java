package com.example.yin.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

public class AuthInterceptor implements HandlerInterceptor {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    // 无需登录即可访问的路径
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/user/login/status",
            "/user/add",
            "/admin/login/status",
            "/singer",
            "/singer/name/detail",
            "/singer/sex/detail",
            "/song",
            "/song/detail",
            "/song/singer/detail",
            "/song/singerName/detail",
            "/song/name/detail",
            "/songList",
            "/songList/title/detail",
            "/songList/likeTitle/detail",
            "/songList/style/detail",
            "/listSong",
            "/listSong/detail",
            "/comment",
            "/comment/song/detail",
            "/comment/songList/detail",
            "/rank",
            "/recommend/**",
            "/img/**",
            "/song/**",
            "/favicon.ico",
            "/error"
    );

    // 仅管理员可访问的路径
    private static final List<String> ADMIN_PATHS = Arrays.asList(
            "/user",
            "/user/delete",
            "/song/add",
            "/song/delete",
            "/song/update",
            "/song/img/update",
            "/song/url/update",
            "/singer/add",
            "/singer/delete",
            "/singer/update",
            "/singer/avatar/update",
            "/songList/add",
            "/songList/delete",
            "/songList/update",
            "/songList/img/update",
            "/ListSong/add",
            "/ListSong/delete",
            "/listSong/update",
            "/comment/delete",
            "/comment/update"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 CORS 预检
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        if (matchAny(PUBLIC_PATHS, path)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        boolean isAdmin = session != null && session.getAttribute("name") != null;
        boolean isLogin = session != null && session.getAttribute("username") != null;

        if (matchAny(ADMIN_PATHS, path)) {
            if (isAdmin) {
                return true;
            }
            return reject(response, HttpServletResponse.SC_FORBIDDEN, "需要管理员权限");
        }

        // 默认 fail-closed：未匹配公开路径的一律要求登录
        if (!isLogin && !isAdmin) {
            return reject(response, HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
        }
        return true;
    }

    private boolean matchAny(List<String> patterns, String path) {
        for (String pattern : patterns) {
            if (MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private boolean reject(HttpServletResponse response, int status, String msg) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        JSONObject json = new JSONObject();
        json.put("code", 0);
        json.put("msg", msg);
        response.getWriter().write(json.toJSONString());
        return false;
    }
}
