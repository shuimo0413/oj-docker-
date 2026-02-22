package oj.interceptor;

import oj.pojo.entity.Result;
import oj.util.RedisUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Token 拦截器：除 login 和 register 接口外，其他接口需验证 Token
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtils redisUtils;

    // ... existing code ...
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 允许跨域预检请求（OPTIONS 请求直接通过）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 2. 排除登录和注册接口
        String requestURI = request.getRequestURI();
        if ("/user/login".equals(requestURI)
                || "/api/user/login".equals(requestURI)  // 同时排除/api前缀的登录注册接口
                || "/api/user/register".equals(requestURI)
        ) {
            return true;
        }

        // 3. 获取token - 同时支持两种格式：
        // a) Authorization: Bearer <token>
        // b) token: <token>
        String token = null;

        // 先尝试从Authorization header获取
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        }

        // 如果Authorization header不存在或格式不正确，则尝试从token header获取
        if (token == null || token.isEmpty()) {
            token = request.getHeader("token");
        }

        // 4. 验证token是否存在
        if (token == null || token.isEmpty()) {
            returnJson(response, Result.error("未携带Token，请先登录"));
            return false;
        }

        // 5. 验证Token是否存在于Redis
        boolean tokenExists = redisUtils.hasKey(token);
        if (!tokenExists) {
            returnJson(response, Result.error("Token无效或已过期，请重新登录"));
            return false;
        }

        // 6. Token有效，放行请求
        return true;
    }

    /**
     * 向响应中写入 JSON 格式的错误信息
     */
    private void returnJson(HttpServletResponse response, Result result) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSONObject.toJSONString(result));
        writer.flush();
        writer.close();
    }

}