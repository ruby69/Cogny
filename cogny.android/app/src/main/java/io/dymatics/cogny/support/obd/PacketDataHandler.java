package io.dymatics.cogny.support.obd;

import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.HashMap;
import java.util.Map;

import io.dymatics.cogny.support.obd.handler.DisconnectHandler;
import io.dymatics.cogny.support.obd.handler.DtcHandler;
import io.dymatics.cogny.support.obd.handler.Handleable;
import io.dymatics.cogny.support.obd.handler.LiveHandler;
import io.dymatics.cogny.support.obd.handler.ObdInfoHandler;
import io.dymatics.cogny.support.obd.handler.UnTransHandler;

@EBean(scope = EBean.Scope.Singleton)
public class PacketDataHandler extends PacketHandler {
    @RootContext Context context;

    @Bean LiveHandler liveHandler;
    @Bean UnTransHandler unTransHandler;
    @Bean DtcHandler dtcHandler;
    @Bean ObdInfoHandler obdInfoHandler;
    @Bean DisconnectHandler disconnectHandler;

    private Map<Protocol.Cmd.Read, Handleable> map = new HashMap<>();

    @AfterInject
    void afterInject() {
        map.put(Protocol.Cmd.Read.LIVE, liveHandler);
        map.put(Protocol.Cmd.Read.UNTRANS, unTransHandler);
        map.put(Protocol.Cmd.Read.DTC, dtcHandler);
        map.put(Protocol.Cmd.Read.OBD, obdInfoHandler);
        map.put(Protocol.Cmd.Read.DISCONNECT, disconnectHandler);
    }

    protected void doInitialize() {
        for(Handleable handler : map.values()) {
            handler.initialize();
        }
    }

    protected void doHandle(Packet packet) {
        Handleable handler = map.get(packet.getCmd());
        if (handler != null) {
            handler.handle(packet.getMessages());
        }
    }

}
