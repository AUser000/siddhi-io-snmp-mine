package org.wso2.extension.siddhi.io.snmp.manager;

import org.apache.log4j.Logger;

import java.io.IOException;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Java Doc Comment
 * */
public class SNMPServer extends Thread implements Server {
    Logger log = Logger.getLogger(SNMPServer.class);
    private static SNMPGetManager snmpGetManager;
    private static int requestInterval = 5000;
    private boolean started = true;
    private static SNMPServer server = new SNMPServer();

    private SNMPServer() {}

    public static SNMPServer getInstance(int interval, SNMPGetManager manager) {
        requestInterval = interval;
        snmpGetManager = manager;
        return server;
    }

    public static SNMPServer getInstance() {
        return server;
    }

    public static void setRequestInterval(int ri) {
        requestInterval = ri;
    }

    public int getRequestInterval() {
        return this.requestInterval;
    }

    public static void setSnmpGetManager(SNMPGetManager manager) {
        snmpGetManager = manager;
    }

    public SNMPManager getSnmpManager() {
        return this.snmpGetManager;
    }


    @Override
    public void run() {
        startServer();
    }

    @Override
    public synchronized void startServer() {
        //snmpGetManager.getManagerConfig().getPdu();
        while (started != false) {
            try {
                snmpGetManager.validateResponseAndNotify(snmpGetManager.send());
            } catch (IOException e) {

            }
            try {
                Thread.sleep(requestInterval);
            } catch (InterruptedException e) {

            }
        }
        log.info("im getting out");
    }

    @Override
    public synchronized void stopServer() {
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
