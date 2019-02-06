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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java Doc Comment
 * */
public class SNMPListener implements RunnableFuture {

    private final Logger log = Logger.getLogger(SNMPListener.class);
    private SNMPGetManager snmpGetManager;
    private int requestInterval = 1000;
    private volatile boolean flag = true;

    public SNMPListener(SNMPGetManager snmpGetManager, int requestInterval) {
        this.requestInterval = requestInterval;
        this.snmpGetManager = snmpGetManager;
    }

    @Override
    public void run() {
        while (flag) {
            try {
                snmpGetManager.validateResponseAndNotify(snmpGetManager.send());
            } catch (IOException e) {
                log.info("[server ] IOException! ");
            }
            try {
                Thread.sleep(requestInterval);
            } catch (InterruptedException e) {
                log.info("");
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        flag = false;
        return flag;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

}
