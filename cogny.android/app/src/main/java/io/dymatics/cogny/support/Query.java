package io.dymatics.cogny.support;

public interface Query<T> {
    T execute() throws Exception;
}