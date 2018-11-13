package io.dymatics.cogny.support;

import java.util.regex.Pattern;

public interface StringUtils {
    Pattern passPattern = Pattern.compile("^(?=.*\\d)(?=.*[a-zA-Z]).{6,20}$");
    Pattern namePattern = Pattern.compile("^[a-zA-Z가-힣]{2,10}$");
    Pattern alphaKorPattern = Pattern.compile("^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$");
    Pattern emailPattern = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

    static String extractNumber(String value) {
        if(value != null && value.trim().length() > 0) {
            return value.replaceAll("^[0-9]", "");
        } else {
            return null;
        }
    }

    static boolean validateEmail(String value) {
        return emailPattern.matcher(value).matches();
    }

    static boolean validatePass(String value) {
        return passPattern.matcher(value).matches();
    }

    static boolean validatePassConfirm(String value, String compareValue) {
        return compareValue.equals(value);
    }

    static boolean validateName(String value) {
        return namePattern.matcher(value).matches();
    }

    static boolean matchAlphaKor(CharSequence value) {
        return alphaKorPattern.matcher(value).matches();
    }
}
