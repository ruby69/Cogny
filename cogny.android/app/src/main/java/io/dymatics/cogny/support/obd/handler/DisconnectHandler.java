package io.dymatics.cogny.support.obd.handler;

import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

import io.dymatics.cogny.support.obd.ObdBean;
import io.dymatics.cogny.support.obd.message.Message;

@EBean(scope = EBean.Scope.Singleton)
public class DisconnectHandler extends Handler<Message> {
    @RootContext Context context;
    @Bean ObdBean obdBean;

    @Override
    protected void doHandle(List<Message> messages) {
        obdBean.disconnect();
    }
}
