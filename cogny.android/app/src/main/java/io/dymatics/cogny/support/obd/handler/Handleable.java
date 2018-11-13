package io.dymatics.cogny.support.obd.handler;

import java.util.List;

public interface Handleable<M> {

    void initialize();

    void handle(List<M> messages);

}
