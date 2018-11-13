package io.dymatics.cogny.staff.service;

import io.dymatics.cogny.domain.model.Sms;

public interface ExternalApiService {

    String inviteLink(String code);

    Sms.Status sendSms(String title, String message, String cellNumber);

}
