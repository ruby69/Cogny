package io.dymatics.cogny.support.obd.handler;

import org.androidannotations.annotations.EBean;

import java.util.List;

import io.dymatics.cogny.support.obd.message.SensorMessage;

@EBean(scope = EBean.Scope.Singleton)
public class UnTransHandler extends Handler<SensorMessage> {

    @Override
    protected void doHandle(List<SensorMessage> messages) {
//        for (SensorMessage message : messages) {
//            if (cognyService != null) {
//                cognyService.recordSensingLog(message.getValueString());
//            }
//            EventBus.getDefault().post(new OnMessage(message));
//        }
    }
}
