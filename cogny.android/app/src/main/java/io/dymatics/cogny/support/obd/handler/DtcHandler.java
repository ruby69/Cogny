package io.dymatics.cogny.support.obd.handler;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.List;

import io.dymatics.cogny.service.CognyBean;
import io.dymatics.cogny.support.obd.message.DtcMessage;

@EBean(scope = EBean.Scope.Singleton)
public class DtcHandler extends Handler<DtcMessage> {
    @Bean CognyBean cognyBean;

    @Override
    protected void doHandle(List<DtcMessage> messages) {
        for (DtcMessage message : messages) {
            if (message.hasDtc()) {
                cognyBean.recordDtcRaws(message.getDtcRaws());
            }
        }
    }
}
