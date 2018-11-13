package io.dymatics.cogny.support.obd.handler;

import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.dymatics.cogny.event.obd.ObdInfo;
import io.dymatics.cogny.support.obd.message.ObdInfoMessage;

@EBean(scope = EBean.Scope.Singleton)
public class ObdInfoHandler extends Handler<ObdInfoMessage> {
    private boolean first = true;

    @Override
    public void initialize() {
        first = true;
    }

    @Override
    protected void doHandle(List<ObdInfoMessage> messages) {
        if (first) {
            first = false;
            if (!messages.isEmpty()) {
                EventBus.getDefault().post(new ObdInfo(messages.get(messages.size() - 1)));
            }
        }
    }
}
