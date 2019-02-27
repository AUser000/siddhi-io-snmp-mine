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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.extension.siddhi.io.snmp.utils.AdvancedCommandProcessor;
import org.wso2.extension.siddhi.io.snmp.utils.Agent;
import org.wso2.extension.siddhi.io.snmp.utils.EventHolder;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.core.util.SiddhiTestHelper;
import org.wso2.siddhi.query.api.exception.SiddhiAppValidationException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCaseOfSNMPSourceV3 {
    private static final Logger LOG  = Logger.getLogger(TestCaseOfSNMPSourceV3.class);
    private AtomicInteger eventCount = new AtomicInteger(0);
    private AtomicBoolean eventArrived = new AtomicBoolean(false);
    private String port = "2019";
    private String ip = "127.0.0.1";
    private Agent agent;
    private EventHolder eventHolder;
    private AdvancedCommandProcessor advancedCommandProcessor;
    private int sleepTime = 3000;
    private int timeout = 3000;

    @BeforeClass
    public void startAgent() throws IOException, InterruptedException {
        LOG.info("agent starting.. ");
        Thread.sleep(1500);
        eventHolder = new EventHolder(1);
        advancedCommandProcessor = new AdvancedCommandProcessor();
        advancedCommandProcessor.setEventListener(eventHolder);
        agent = new Agent(advancedCommandProcessor);
        agent.start(ip, port);
    }

    @AfterClass
    public void stopAgent() {
        agent.stop();
        LOG.info("agent stopped ");
    }

    @BeforeMethod
    public void init() {
        eventCount.set(0);
        eventArrived.set(false);
    }

    @Test
    public void snmpVersion3TestWithAllValues() throws InterruptedException {
        LOG.info("-----------------------------------------------");
        LOG.info("     SNMP Version 3 Basic Source Test Case     ");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();

        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v3',\n" +
                "timeout = '100',\n" +
                "request.interval = '500',\n" +
                "agent.port = '" + port + "',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                "auth.protocol = 'AUTHMD5',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "priv.password = 'privpass',\n" +
                "auth.password = 'authpass',\n" +
                "security.lvl = 'AUTH_PRIV',\n" +
                "user.name = 'agent5') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    eventCount.getAndIncrement();
                    eventArrived.set(true);
                }
            }
        });

        executionPlanRuntime.start();
        SiddhiTestHelper.waitForEvents(sleepTime, 5, eventCount, timeout);
        Assert.assertTrue(eventArrived.get());

        LOG.info("Siddhi manager shutting down");
        siddhiManager.shutdown();
        LOG.info("Siddhi manager down");
    }

    @Test
    public void snmpVersion3SecLevel() throws InterruptedException {
        LOG.info("-----------------------------------------------");
        LOG.info("SNMP Version 3 Source Test Case Sec Lvl AUTH_NOPRIV");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();

        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v3',\n" +
                "timeout = '100',\n" +
                "request.interval = '500',\n" +
                "agent.port = '" + port + "',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                "auth.protocol = 'AUTHSHA',\n" +
                "auth.password = 'SHAAuthPassword',\n" +
                "security.lvl = 'AUTH_NOPRIV',\n" +
                "user.name = 'SHA') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    eventCount.getAndIncrement();
                    eventArrived.set(true);
                }
            }
        });

        executionPlanRuntime.start();
        SiddhiTestHelper.waitForEvents(sleepTime, 5, eventCount, timeout);
        Assert.assertTrue(eventArrived.get());

        LOG.info("[TestCaseOfSNMPSource.class] Siddhi manager shutting down");
        siddhiManager.shutdown();
    }

    @Test
    public void snmpVersion3UDP() throws InterruptedException {
        LOG.info("-----------------------------------------------");
        LOG.info("    SNMP Version 3 Source Test Case for UDP    ");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v3',\n" +
                "timeout = '100',\n" +
                "transport.protocol = 'udp',\n" +
                "request.interval = '500',\n" +
                "agent.port = '" + port + "',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                "auth.protocol = 'AUTHSHA',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "priv.password = 'privpass',\n" +
                "auth.password = 'authpass',\n" +
                "security.lvl = 'AUTH_PRIV',\n" +
                "user.name = 'agent5') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    eventCount.getAndIncrement();
                    eventArrived.set(true);
                }
            }
        });

        executionPlanRuntime.start();
        SiddhiTestHelper.waitForEvents(sleepTime, 5, eventCount, timeout);
        Assert.assertTrue(eventArrived.get());

        LOG.info("Siddhi manager shutting down");
        siddhiManager.shutdown();
    }

    @Test
    public void snmpVersion3AuthPriv() throws InterruptedException {
        LOG.info("-----------------------------------------------");
        LOG.info("SNMP Version 3 Source Test Case Sec Lvl 3 Agent5");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();

        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v3',\n" +
                "timeout = '100',\n" +
                "request.interval = '500',\n" +
                "agent.port = '" + port + "',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                "auth.protocol = 'AUTHSHA',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "priv.password = 'privpass',\n" +
                "auth.password = 'authpass',\n" +
                "security.lvl = 'AUTH_PRIV',\n" +
                "user.name = 'agent5') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    eventCount.getAndIncrement();
                    eventArrived.set(true);
                }
            }
        });

        executionPlanRuntime.start();
        SiddhiTestHelper.waitForEvents(sleepTime, 5, eventCount, timeout);
        Assert.assertTrue(eventArrived.get());

        LOG.info("[TestCaseOfSNMPSource.class] Siddhi manager shutting down");
        siddhiManager.shutdown();
    }

    @Test
    public void snmpVersion3AuthPriv2() throws InterruptedException {
        LOG.info("-----------------------------------------------");
        LOG.info("SNMP Version 3 Source Test Case Sec Lvl 3 Agent5");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();

        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v3',\n" +
                "timeout = '100',\n" +
                "request.interval = '500',\n" +
                "agent.port = '" + port + "',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                "auth.protocol = 'AUTHMD5',\n" +
                "priv.protocol = 'PRIVAES256',\n" +
                "priv.password = 'MD5AES256PrivPassword',\n" +
                "auth.password = 'MD5AES256AuthPassword',\n" +
                "security.lvl = 'AUTH_PRIV',\n" +
                "user.name = 'MD5AES256') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    eventCount.getAndIncrement();
                    eventArrived.set(true);
                }
            }
        });

        executionPlanRuntime.start();
        SiddhiTestHelper.waitForEvents(sleepTime, 5, eventCount, timeout);
        Assert.assertTrue(eventArrived.get());

        LOG.info("[TestCaseOfSNMPSource.class] Siddhi manager shutting down");
        siddhiManager.shutdown();
    }
    @Test(expectedExceptions = SiddhiAppValidationException.class)
    public void snmpValidationTest() { // sec lvl
        LOG.info("------------------------------------------------");
        LOG.info("SNMP Version 3 Source Test Case Sec Lvl 3 Agent5");
        LOG.info("------------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();

        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v3',\n" +
                "timeout = '100',\n" +
                "request.interval = '500',\n" +
                "agent.port = '" + port + "',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                "auth.protocol = 'AUTHSHA',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "priv.password = 'privpass',\n" +
                "auth.password = 'authpass',\n" +
                "security.lvl = 'AUTH_PRI',\n" +
                "user.name = 'agent5') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    //log.info(event.toString());
                    eventCount.getAndIncrement();
                    eventArrived.set(true);
                }
            }
        });

        executionPlanRuntime.start();
        LOG.info("[TestCaseOfSNMPSource.class] Siddhi manager shutting down");
        siddhiManager.shutdown();
    }

    @Test(expectedExceptions = SiddhiAppValidationException.class)
    public void snmpRuntimeTest() { // for priv protocol
        LOG.info("------------------------------------------------");
        LOG.info("SNMP Version 3 Source Test Case Sec Lvl 3 Agent5");
        LOG.info("------------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();

        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v3',\n" +
                "timeout = '100',\n" +
                "request.interval = '500',\n" +
                "agent.port = '" + 200 + "',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                "auth.protocol = 'AUTHSHA',\n" +
                "priv.protocol = 'PRIVDE',\n" +
                "priv.password = 'privpass',\n" +
                "auth.password = 'authpass',\n" +
                "security.lvl = 'AUTH_PRIV',\n" +
                "user.name = 'agent5') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    eventCount.getAndIncrement();
                    eventArrived.set(true);
                }
            }
        });

        executionPlanRuntime.start();
        LOG.info("[TestCaseOfSNMPSource.class] Siddhi manager shutting down");
        siddhiManager.shutdown();
    }

}
