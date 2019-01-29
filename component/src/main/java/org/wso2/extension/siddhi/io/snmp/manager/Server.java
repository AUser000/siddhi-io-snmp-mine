package org.wso2.extension.siddhi.io.snmp.manager;

/**
  * Java doc comment
  * */
public interface Server {
    void startServer();
    void stopServer();
    void resumeServer();
    void pauseServer();
}
