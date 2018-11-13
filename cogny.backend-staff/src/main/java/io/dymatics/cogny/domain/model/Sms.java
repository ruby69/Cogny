package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Sms {
    public static final String SENDER = "02xxxxxxx";
    public static final String USERNAME = "helloworld";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status {
        private String status;

        public String getStatusCode() {
            return status;
        }

        public String getResponseCode() {
            return "0".equals(status) ? "200" : "-1";
        }

        /*
         * response의 실패
         *  {"status":101}
         */

        /*
         * response 성공
         * {"status":0}
         *  성공 코드번호.
         */

        /*
        ** status code
            0   : 정상발송
            100 : POST validation 실패
            101 : sender 유효한 번호가 아님
            102 : recipient 유효한 번호가 아님
            103 : api key or user is invalid
            104 : recipient count = 0
            105 : message length = 0, message length >= 2000, title length >= 20
            106 : message validation 실패
            107 : 이미지 업로드 실패
            108 : 이미지 갯수 초과
            109 : return_url이 없음
            110 : 이미지 용량 300kb 초과
            111 : 이미지 확장자 오류
            205 : 잔액부족
            999 : Internal Error.
        **
        */
    }
}
