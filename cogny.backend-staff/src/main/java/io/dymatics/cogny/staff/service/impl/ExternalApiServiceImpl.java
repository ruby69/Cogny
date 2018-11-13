package io.dymatics.cogny.staff.service.impl;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.dymatics.cogny.domain.model.Sms;
import io.dymatics.cogny.staff.service.ExternalApiService;

@Service
public class ExternalApiServiceImpl implements ExternalApiService {
    @Value("${external.invite.url}") private String inviteUrl;
    @Value("${external.sms.key}") private String smsKey;
    @Value("${external.sms.url}") private String smsUrl;
    @Autowired private RestTemplate restTemplate;

    private URI smsUri;

    @PostConstruct
    public void postConstruct() {
        try {
            smsUri = new URI(smsUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private HttpEntity<?> getEntity(Object params) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return params != null ? new HttpEntity<>(params, httpHeaders) : new HttpEntity<>(httpHeaders);
    }

    private <T> T execute(URI uri, HttpMethod method, Object params, Class<T> clazz) {
        ResponseEntity<T> response = restTemplate.exchange(uri, method, getEntity(params), clazz);
        return response != null ? response.getBody() : null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String inviteLink(String code) {
        return inviteUrl + code;
    }

    @Override
    public Sms.Status sendSms(String title, String message, String cellNumber) {
        try {
            return execute(smsUri, HttpMethod.POST, getSmsBody(smsKey, title, message, cellNumber), Sms.Status.class);
        } catch (Exception e) {
            return new Sms.Status("999");
        }
    }

    private MultiValueMap<String, String> getSmsBody(String key, String title, String message, String recipients) {
        try {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("title", Base64.encodeBase64String(title.getBytes("euc-kr")));
            map.add("message", Base64.encodeBase64String(message.getBytes("euc-kr")));
            map.add("recipients", recipients);
            map.add("sender", Sms.SENDER);
            map.add("username", Sms.USERNAME);
            map.add("key", key);
            map.add("type", "java");
            return map;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
