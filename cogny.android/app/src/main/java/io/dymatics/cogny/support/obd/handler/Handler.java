package io.dymatics.cogny.support.obd.handler;

import java.util.List;

public abstract class Handler<M> implements Handleable<M> {

    protected abstract void doHandle(List<M> messages);

    @Override
    public void initialize() {

    }

    @Override
    public void handle(List<M> messages) {
        doHandle(messages);
    }
}
