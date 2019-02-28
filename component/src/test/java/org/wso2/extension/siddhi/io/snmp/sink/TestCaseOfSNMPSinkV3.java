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
package org.wso2.extension.siddhi.io.snmp.sink;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.extension.siddhi.io.snmp.sink.exceptions.SNMPSinkRuntimeException;
import org.wso2.extension.siddhi.io.snmp.utils.AdvancedCommandProcessor;
import org.wso2.extension.siddhi.io.snmp.utils.Agent;
import org.wso2.extension.siddhi.io.snmp.utils.EventHolder;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.query.api.exception.SiddhiAppValidationException;

import java.io.IOException;

public class TestCaseOfSNMPSinkV3 {

    private static final Logger log = Logger.getLogger(TestCaseOfSNMPSinkV3.class);
    private String port = "2019";
    private String ip = "127.0.0.1";
    private Agent agent;
    private EventHolder eventHolder;

    @BeforeClass
    public void startAgent() throws IOException {

        AdvancedCommandProcessor processor = new AdvancedCommandProcessor();
        eventHolder = new EventHolder();
        processor.setEventListener(eventHolder);
        agent = new Agent(processor);
        agent.start(ip, port);
        log.info("agent started.");
    }

    @AfterClass
    public void stopAgent() {

        agent.stop();
    }

    @BeforeMethod
    public void clearHolder() {

        eventHolder.clear();
    }

    @Test
    public void snmpVersion3BasicSink() throws InterruptedException {

        log.info("-----------------------------------------------");
        log.info("       SNMP Version 3 Basic Sink Test Case     ");
        log.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('snmpSink') \n" +
                "\n" +
                "@Sink(type='snmp',\n" +
                "@map(type='keyvalue', @payload('1.3.6.1.2.1.1.4.0' = 'value')),\n" +
                "host = '" + ip + "',\n" +
                "version = 'v3',\n" +
                "agent.port = '" + port + "',\n" +
                "priv.password = 'privpass',\n" +
                "auth.protocol = 'AUTHSHA',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "auth.password = 'authpass',\n" +
                "priv.password = 'privpass',\n" +
                "security.lvl = 'AUTH_PRIV',\n" +
                "user.name = 'agent5', \n" +
                "retries = '5')\n" +
                "define stream outputStream(value string);";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        InputHandler inputStream = executionPlanRuntime.getInputHandler("outputStream");
        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {

                for (Event event : events) {
                    log.info(event.toString());
                }
            }
        });

        log.info(" Siddhi manager started ");
        executionPlanRuntime.start();

        inputStream.send(new Object[]{"mail@wso2.com"});
        inputStream.send(new Object[]{"mail@wso2.com"});

        Thread.sleep(1000);

        Assert.assertTrue(eventHolder.assertDataContent("mail@wso2.com", 0));

        log.info(" siddhi manager shutting down ");
        siddhiManager.shutdown();
    }

    @Test
    public void snmpVersion3TCPSink() throws InterruptedException {

        log.info("-----------------------------------------------");
        log.info("       SNMP Version 3 TCP Sink Test Case     ");
        log.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('snmpSink') \n" +
                "\n" +
                "@Sink(type='snmp',\n" +
                "@map(type='keyvalue', @payload('1.3.6.1.2.1.1.4.0' = 'value')),\n" +
                "host = '" + ip + "',\n" +
                "version = 'v3',\n" +
                "transport.protocol = 'tcp',\n" +
                "agent.port = '" + port + "',\n" +
                "priv.password = 'privpass',\n" +
                "auth.protocol = 'AUTHSHA',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "auth.password = 'authpass',\n" +
                "priv.password = 'privpass',\n" +
                "security.lvl = 'AUTH_PRIV',\n" +
                "user.name = 'agent5', \n" +
                "retries = '5')\n" +
                "define stream outputStream(value string);";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        InputHandler inputStream = executionPlanRuntime.getInputHandler("outputStream");
        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {

                for (Event event : events) {
                    log.info(event.toString());
                }
            }
        });

        log.info(" Siddhi manager started ");
        executionPlanRuntime.start();

        inputStream.send(new Object[]{"mail@wso2.com"});
        inputStream.send(new Object[]{"mail@wso2.com"});

        Thread.sleep(1000);

        Assert.assertTrue(eventHolder.assertDataContent("mail@wso2.com", 0));

        log.info(" siddhi manager shutting down ");
        siddhiManager.shutdown();
    }

    // giving invalid security.lvl string
    @Test(expectedExceptions = SiddhiAppValidationException.class)
    public void snmpVersion3SinkException() {

        log.info("-----------------------------------------------");
        log.info("       SNMP Version 3 Sink Test Case           ");
        log.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('snmpSink') \n" +
                "\n" +
                "@Sink(type='snmp',\n" +
                "@map(type='keyvalue', @payload('1.3.6.1.2.1.1.4.0' = 'value')),\n" +
                "host = '" + ip + "',\n" +
                "version = 'v3',\n" +
                "agent.port = '" + port + "',\n" +
                "priv.password = 'privpass',\n" +
                "auth.protocol = 'AUTHSHA',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "auth.password = 'authpass',\n" +
                "priv.password = 'privpass',\n" +
                "security.lvl = 'AUTH_PRI',\n" +  // should be AUTH_PRIV
                "user.name = 'agent5', \n" +
                "retries = '1')\n" +
                "define stream outputStream(value string);";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        InputHandler inputStream = executionPlanRuntime.getInputHandler("outputStream");
        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {

                for (Event event : events) {
                    log.info(event.toString());
                }
            }
        });

        log.info(" Siddhi manager started ");
        executionPlanRuntime.start();
        log.info(" siddhi manager shutting down ");
        siddhiManager.shutdown();
    }

    // if the map annotation is not include in the siddhi app
    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void snmpVersion3SinkCreationException() throws Exception {

        log.info("-----------------------------------------------");
        log.info("       SNMP Version 3 Sink Test Case           ");
        log.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('snmpSink') \n" +
                "\n" +
                "@Sink(type='snmp',\n" +
                "host = '" + ip + "',\n" +
                "version = 'v3',\n" +
                "agent.port = '" + port + "',\n" +
                "priv.password = 'privpass',\n" +
                "auth.protocol = 'AUTHSHA',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "auth.password = 'authpass',\n" +
                "priv.password = 'privpass',\n" +
                "security.lvl = 'AUTH_PRIV',\n" +  // should be AUTH_PRIV
                "user.name = 'agent5', \n" +
                "retries = '1')\n" +
                "define stream outputStream(value string);";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        InputHandler inputStream = executionPlanRuntime.getInputHandler("outputStream");
        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {

                for (Event event : events) {
                    log.info(event.toString());
                }
            }
        });

        log.info(" Siddhi manager started ");
        executionPlanRuntime.start();
        log.info(" siddhi manager shutting down ");
        siddhiManager.shutdown();
    }

    // expected fail // invalid port //TODO ->
    @Test(enabled = false)
    public void snmpVersion3SinkValidationException() {

        log.info("-----------------------------------------------");
        log.info("       SNMP Version 3 Sink Test Case           ");
        log.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('snmpSink') \n" +
                "\n" +
                "@Sink(type='snmp',\n" +
                "@map(type='keyvalue', @payload('1.3.6.1.2.1.1.4.0' = 'value')),\n" +
                "host = '" + ip + "',\n" +
                "version = 'v3',\n" +
                "agent.port = '" + 1000 + "',\n" +
                "priv.password = 'privpass',\n" +
                "auth.protocol = 'AUTHSHA',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "auth.password = 'authpass',\n" +
                "priv.password = 'privpass',\n" +
                "security.lvl = 'AUTH_PRIV',\n" +
                "user.name = 'agent5', \n" +
                "retries = '1')\n" +
                "define stream outputStream(value string);";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        InputHandler inputStream = executionPlanRuntime.getInputHandler("outputStream");
        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {

                for (Event event : events) {
                    log.info(event.toString());
                }
            }
        });


        try {
            inputStream.send(new Object[]{"mail@wso2.com"});
        } catch (SNMPSinkRuntimeException ex) {

        } catch (InterruptedException e) {

        }

        log.info(" siddhi manager shutting down ");
        siddhiManager.shutdown();
    }


}
