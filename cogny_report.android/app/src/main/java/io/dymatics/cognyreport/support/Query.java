package io.dymatics.cognyreport.support;

public interface Query<T> {
    T execute() throws Exception;
}