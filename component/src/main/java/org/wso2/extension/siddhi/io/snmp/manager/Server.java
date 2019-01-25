package org.wso2.extension.siddhi.io.snmp.manager;

public interface Server {
    void start();
    void stop();
    void resume();
    void pause();
}
