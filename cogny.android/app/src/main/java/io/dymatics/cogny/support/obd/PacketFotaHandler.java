package io.dymatics.cogny.support.obd;

import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.dymatics.cogny.event.obd.ObdAction;
import io.dymatics.cogny.event.obd.ObdFotaFin;

@EBean(scope = EBean.Scope.Singleton)
public class PacketFotaHandler extends PacketHandler {
    @RootContext Context context;
    @Bean ObdBean obdBean;

    private List<ObdAction> fotaActions;

    @AfterInject
    void afterInject() {
    }

    protected void doInitialize() {
    }

    protected void doHandle(Packet packet) {
        Protocol.Cmd.Read cmd = packet.getCmd();
        if (cmd.isAck()) {
            continueFota();
        } else if (cmd.isDisconnect()) {
            obdBean.disconnect();
            EventBus.getDefault().post(new ObdFotaFin());
        }
    }

    public void startFota(List<ObdAction> fotaActions){
        this.fotaActions = fotaActions;
        continueFota();
    }

    private void continueFota() {
        if (!fotaActions.isEmpty()) {
            obdBean.writeAsChunk(fotaActions.remove(0).getCmdBytes());
        }
    }
}
