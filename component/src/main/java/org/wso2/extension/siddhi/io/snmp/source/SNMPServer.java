/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.extension.siddhi.io.snmp.source;

import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.snmp.util.SNMPManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class will help to make set request
 * continuously once withing request interval time
 * in a different thread
 */
public class SNMPServer implements Runnable {
    private static final Logger log = Logger.getLogger(SNMPServer.class);
    private SNMPManager snmpManager;
    private int requestInterval;
    private boolean running = false;
    private ExecutorService executorService;

    public SNMPServer() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void setManager(SNMPManager snmpManager) {
        this.snmpManager = snmpManager;
    }

    public void setRequestInterval(int requestInterval) {
        this.requestInterval = requestInterval;
    }

    public synchronized void start() {
        if (!isRunning()) {
            running = true;
            Future<?> thread = executorService.submit(this);
        }
    }

    @Override
    public void run() {
        while (isRunning()) {
            try {
                snmpManager.getRequestValidateAndReturn();
                Thread.sleep(requestInterval);
            } catch (IOException | InterruptedException e) {
                log.error("Error in sending request" + e);
            }
        }
    }

    public synchronized void stop() {
        if (isRunning()) {
            running = false;
        }
    }

    private synchronized boolean isRunning() {
        return running;
    }

}
