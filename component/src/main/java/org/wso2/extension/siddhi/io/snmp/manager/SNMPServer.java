package org.wso2.extension.siddhi.io.snmp.manager;

import java.io.IOException;

/**
 * Java Doc Comment
 * */
public class SNMPServer implements Runnable, Server {
    private SNMPGetManager snmpGetManager;
    private int requestInterval = 5000;
    private boolean started = false;

    public void setRequestInterval(int requestInterval) {
        this.requestInterval = requestInterval;
    }

    public SNMPServer(int requestInterval, SNMPGetManager snmpGetManager) {
        this.requestInterval = requestInterval;
        this.snmpGetManager = snmpGetManager;
    }

    public int getRequestInterval() {
        return this.requestInterval;
    }

    public void setSnmpGetManager(SNMPGetManager snmpGetManager) {
        this.snmpGetManager = snmpGetManager;
    }

    public SNMPManager getSnmpManager() {
        return this.snmpGetManager;
    }


    @Override
    public void run() {
        started = true;
        startServer();
    }

    @Override
    public void startServer() {
        //snmpGetManager.getManagerConfig().getPdu();
        while (started) {
            try {
                snmpGetManager.validateResponseAndNotify(snmpGetManager.send());
            } catch (IOException e) {

            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public void stopServer() {
        started = false;
    }

    @Override
    public void resumeServer() {
        started = true;
        startServer();
    }

    @Override
    public void pauseServer() {
        started = false;
    }
}
