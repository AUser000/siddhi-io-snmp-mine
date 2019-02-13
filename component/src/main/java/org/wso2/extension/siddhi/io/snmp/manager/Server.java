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
package org.wso2.extension.siddhi.io.snmp.manager;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Java Doc Comment
 * */
public class Server implements Runnable {

    private static final Logger log = Logger.getLogger(Server.class);
    private SNMPGetManager snmpGetManager;
    private int requestInterval = 1000;
    private volatile boolean running = false;
    ExecutorService executorService;
    private Future<?> thread;

    public Server(SNMPGetManager snmpGetManager, int requestInterval) {
        this.requestInterval = requestInterval;
        this.snmpGetManager = snmpGetManager;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void start() {
        if (!running) {
            running = true;
            this.thread = executorService.submit(this);
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(requestInterval);
                snmpGetManager.validateResponseAndNotify(snmpGetManager.send());
            } catch (IOException | InterruptedException e) {
                log.error("Error in sending request" + e);
            }
        }
    }

    public void stop() {
        if (running) {
            running = false;
        }
    }
}
