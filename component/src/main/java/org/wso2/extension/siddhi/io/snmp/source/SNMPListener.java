package org.wso2.extension.siddhi.io.snmp.source;

import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.snmp.util.SNMPManager;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;

import java.io.IOException;
import java.util.Map;

/**
 * SNMP Listener thread
 **/
public class SNMPListener implements Runnable {

    private boolean kiled = false;

    private Logger log = Logger.getLogger(SNMPListener.class);
    private SNMPManager manager;
    private SourceEventListener sourceEventListener;

    public SNMPListener(SNMPManager manager, SourceEventListener sourceEventListener) {
        this.manager = manager;
        this.sourceEventListener = sourceEventListener;
    }

    @Override
    public void run() {
        if (!isKilled()) {
            try {
                Map<String, String> map = manager.getRequestValidateAndReturn();
                sourceEventListener.onEvent(map, null);
            } catch (IOException e) {
                log.info("exception IO");
            }
        }
    }

    public synchronized boolean isKilled() {
        return kiled;
    }

    public synchronized void kill() {
        if (!isKilled()) {
            kiled = true;
        }
    }
}
