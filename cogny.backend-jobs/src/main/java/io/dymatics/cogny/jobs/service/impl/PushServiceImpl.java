package io.dymatics.cogny.jobs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import io.dymatics.cogny.domain.model.MobileDevice.UserMobileDevice;
import io.dymatics.cogny.domain.persist.UserMobileDeviceRepository;
import io.dymatics.cogny.jobs.service.PushService;

@Service
public class PushServiceImpl implements PushService {

    @Autowired private UserMobileDeviceRepository userMobileDeviceRepository;

    public void noti(Long userMobileDeviceNo, String body) {
        if (userMobileDeviceNo != null) {
            UserMobileDevice userMobileDevice = userMobileDeviceRepository.findByNo(userMobileDeviceNo);
            if (userMobileDevice != null) {
                AndroidNotification noti = AndroidNotification.builder().setSound("default").setBody(body).build();
                AndroidConfig androidConfig = AndroidConfig.builder()
                        .setTtl(0L)
                        .setPriority(AndroidConfig.Priority.NORMAL)
                        .setNotification(noti)
                        .build();

                Message message = Message.builder().setToken(userMobileDevice.getFcmToken()).setAndroidConfig(androidConfig).build();
                ApiFuture<String> sendAsync = FirebaseMessaging.getInstance().sendAsync(message);
                try {
                    sendAsync.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 전송실패
                }
            }
        }
    }

}
