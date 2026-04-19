package com.stu.helloserver.service.impl;

import com.stu.helloserver.dto.ChatRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl {

    private final RestTemplate restTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${qwen.server.url:http://127.0.0.1:5050}")
    private String qwenServerUrl;

    public ChatServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.restTemplate = new RestTemplate();
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String chat(ChatRequestDTO requestDTO) {
        String sessionId = requestDTO.getSessionId() != null ? requestDTO.getSessionId() : "default";
        String message = requestDTO.getMessage();
        String redisKey = "chat:session:" + sessionId;

        List<String> records = stringRedisTemplate.opsForList().range(redisKey, 0, -1);

        String prompt;
        if (records != null && !records.isEmpty()) {
            String historyText = String.join("\n", records);
            prompt = historyText + "\n当前问题：" + message;
        } else {
            prompt = message;
        }

        String answer = callQwen(prompt);

        String recordText = "用户：" + message + "\n助手：" + answer;
        stringRedisTemplate.opsForList().rightPush(redisKey, recordText);

        Long size = stringRedisTemplate.opsForList().size(redisKey);
        if (size != null && size > 3) {
            stringRedisTemplate.opsForList().trim(redisKey, size - 3, size - 1);
        }

        return answer;
    }

    private String callQwen(String message) {
        String url = qwenServerUrl + "/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("message", message);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            Map<String, String> response = restTemplate.postForObject(url, request, Map.class);
            if (response != null && response.containsKey("answer")) {
                return response.get("answer");
            }
            return "AI 服务返回异常";
        } catch (Exception e) {
            return "AI 服务暂不可用，请确认已启动 qwen_server.py: " + e.getMessage();
        }
    }
}