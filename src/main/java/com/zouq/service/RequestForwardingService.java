package com.zouq.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
public class RequestForwardingService {

    private final RestTemplate restTemplate = new  RestTemplate();
    /**
     * 将请求转发到真实服务
     *
     * @param baseUrl     如 http://localhost:9001
     * @param path        如 /api/v1/users
     * @param originalReq 原始请求（用于获取 headers）
     * @param requestBody 请求体（可能为 null）
     * @return 真实服务的响应
     */
    public ResponseEntity<String> forward(
            String baseUrl,
            String path,
            HttpServletRequest originalReq,
            String requestBody) {

        try {
            // 构造目标 URL
            String targetUrl = baseUrl + path;
            if (originalReq.getQueryString() != null) {
                targetUrl += "?" + originalReq.getQueryString();
            }

            // 复制请求头
            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = originalReq.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                headers.add(key, originalReq.getHeader(key));
            }

            // 设置请求方法和体
            HttpMethod method = HttpMethod.valueOf(originalReq.getMethod());
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);
            return response;

        } catch (Exception e) {
            String errorMsg = "Forward failed: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + errorMsg + "\"}");
        }
    }
}
