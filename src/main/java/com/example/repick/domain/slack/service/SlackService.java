package com.example.repick.domain.slack.service;

import com.example.repick.domain.announcement.dto.PostAnnouncement;
import com.example.repick.domain.announcement.service.AnnouncementService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service @RequiredArgsConstructor
public class SlackService {

    @Value("${slack.api.token}")
    private String slackApiToken;

    @Value("${slack.validate.token}")
    private String slackValidateToken;

    private final AnnouncementService announcementService;

    public ResponseEntity<String> postSubmit(Map<String, Object> payload) {
        System.out.println("Payload: " + payload.toString());

        JsonObject jsonObject = JsonParser.parseString(payload.get("payload").toString()).getAsJsonObject();
        String token = jsonObject.get("token").getAsString();

        if (!slackValidateToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String type = jsonObject.get("type").getAsString();

        if ("view_submission".equals(type)) {
            JsonObject viewObject = jsonObject.get("view").getAsJsonObject();
            JsonObject stateObject = viewObject.get("state").getAsJsonObject();
            JsonObject valuesObject = stateObject.get("values").getAsJsonObject();

            String title = valuesObject.get("EdCne").getAsJsonObject().get("TMyBQ").getAsJsonObject().get("value").getAsString();
            String content = valuesObject.get("+23aM").getAsJsonObject().get("Ag9wf").getAsJsonObject().get("value").getAsString();

            announcementService.createAnnouncement(new PostAnnouncement(title, content));

            return null;
        } else {
            String triggerId = jsonObject.get("trigger_id").getAsString();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-type", "application/json");
            headers.add("Authorization", "Bearer " + slackApiToken);

            String modalJson = "{"
                    + "\"type\": \"modal\","
                    + "\"submit\": {"
                    + "\"type\": \"plain_text\","
                    + "\"text\": \"등록\","
                    + "\"emoji\": true"
                    + "},"
                    + "\"close\": {"
                    + "\"type\": \"plain_text\","
                    + "\"text\": \"취소\","
                    + "\"emoji\": true"
                    + "},"
                    + "\"title\": {"
                    + "\"type\": \"plain_text\","
                    + "\"text\": \"공지사항 등록\","
                    + "\"emoji\": true"
                    + "},"
                    + "\"blocks\": ["
                    + "{"
                    + "\"type\": \"section\","
                    + "\"text\": {"
                    + "\"type\": \"plain_text\","
                    + "\"text\": \":wave: 안녕하세요, 데이캐럿 관리 봇입니다!\\n\\n공지사항을 등록하고, 모든 유저에게 푸시 알림을 보내는 서비스입니다.\","
                    + "\"emoji\": true"
                    + "}"
                    + "},"
                    + "{"
                    + "\"type\": \"divider\""
                    + "},"
                    + "{"
                    + "\"type\": \"input\","
                    + "\"label\": {"
                    + "\"type\": \"plain_text\","
                    + "\"text\": \"공지사항 제목을 등록해주세요.\","
                    + "\"emoji\": true"
                    + "},"
                    + "\"element\": {"
                    + "\"type\": \"plain_text_input\","
                    + "\"multiline\": false"
                    + "}"
                    + "},"
                    + "{"
                    + "\"type\": \"input\","
                    + "\"label\": {"
                    + "\"type\": \"plain_text\","
                    + "\"text\": \"공지사항 내용을 등록해주세요.\","
                    + "\"emoji\": true"
                    + "},"
                    + "\"element\": {"
                    + "\"type\": \"plain_text_input\","
                    + "\"multiline\": true"
                    + "}"
                    + "}"
                    + "]"
                    + "}";

            String requestBody = String.format("{ \"trigger_id\": \"%s\", \"view\": %s }", triggerId, modalJson);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.postForEntity("https://slack.com/api/views.open", request, String.class);
        }
    }

}
