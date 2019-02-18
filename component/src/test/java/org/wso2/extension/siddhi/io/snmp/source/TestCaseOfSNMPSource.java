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
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCaseOfSNMPSource {
    private static final Logger LOG  = Logger.getLogger(TestCaseOfSNMPSource.class);
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
    public void startAgent() throws IOException {
        LOG.info("agent starting.. ");
        eventHolder = new EventHolder(1);
        advancedCommandProcessor = new AdvancedCommandProcessor();
        advancedCommandProcessor.setEventListener(eventHolder);
        agent = new Agent(advancedCommandProcessor);
        agent.start(ip, port);
    }

    @AfterClass
    public void stopAgent() {
        agent.stop();
        LOG.info("agent stopped1");
    }

    @BeforeMethod
    public void init() {
        eventCount.set(0);
        eventArrived.set(false);
    }

    @Test
    public void snmpVersion1Source() throws InterruptedException, TimeoutException, IOException {
        LOG.info("-----------------------------------------------");
        LOG.info("    SNMP Version 1 Basic Source Test Case      ");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();

        String siddhiApp = "@App:name('test') \n" +

                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v1',\n" +
                "agent.port = '" + port + "',\n" +
                "request.interval = '500',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                "community = 'public') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    //log.info(event);
                    eventCount.getAndIncrement();
                    eventArrived.set(true);
                }
            }
        });

        executionPlanRuntime.start();
        SiddhiTestHelper.waitForEvents(sleepTime, 5, eventCount, timeout);
        Assert.assertTrue(eventArrived.get());


        LOG.info("[TestCaseOfSNMPSource.class] Siddhi shutting down");
        siddhiManager.shutdown();
    }

    @Test
    public void snmpVersion2Source() throws InterruptedException, TimeoutException, IOException {
        LOG.info("-----------------------------------------------");
        LOG.info("     SNMP Version 2 Basic Source Test Case     ");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();

        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v2c',\n" +
                "agent.port = '" + port + "',\n" +
                "request.interval = '500',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                "community = 'public') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    //log.info(event);
                    eventCount.getAndIncrement();
                    eventArrived.set(true);
                }
            }
        });

        executionPlanRuntime.start();
        SiddhiTestHelper.waitForEvents(sleepTime, 5, eventCount, timeout);
        Assert.assertTrue(eventArrived.get());

        LOG.info(" Siddhi shutting down");
        siddhiManager.shutdown();
    }

    @Test
    public void snmpVersion3TestWithAllValues() throws InterruptedException, TimeoutException, IOException {
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
                    //log.info(event.toString());
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


    // TODO -> configure Agent! for AuthNoPriv
    @Test
    public void snmpVersion3User0001() throws InterruptedException, TimeoutException, IOException {
        LOG.info("-----------------------------------------------");
        LOG.info("   SNMP Version 3 Source Test Case Sec Lvl 2   ");
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
                "user.name = 'user001') \n" +
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
        SiddhiTestHelper.waitForEvents(sleepTime, 5, eventCount, timeout);
        Assert.assertTrue(eventArrived.get());

        LOG.info("[TestCaseOfSNMPSource.class] Siddhi manager shutting down");
        siddhiManager.shutdown();
    }

    @Test
    public void snmpVersion3TCP() throws InterruptedException, TimeoutException, IOException {
        LOG.info("-----------------------------------------------");
        LOG.info("    SNMP Version 3 Source Test Case for TCP    ");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v3',\n" +
                "timeout = '100',\n" +
                "istcp = 'true',\n" +
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
                    //log.info(event.toString());
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
    public void snmpVersion2TCP() throws InterruptedException, TimeoutException, IOException {
        LOG.info("-----------------------------------------------");
        LOG.info("    SNMP Version 2 Source Test Case for TCP    ");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='" + ip + "',\n" +
                "version = 'v2c',\n" +
                "timeout = '500',\n" +
                "istcp = 'false',\n" +
                "community = 'public',\n" +
                "request.interval = '500',\n" +
                "agent.port = '" + port + "',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0')\n" +
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
        SiddhiTestHelper.waitForEvents(sleepTime, 5, eventCount, timeout);
        Assert.assertTrue(eventArrived.get());

        LOG.info("[TestCaseOfSNMPSource.class] Siddhi manager shutting down");
        siddhiManager.shutdown();
    }

    @Test
    public void snmpVersionAgent5() throws InterruptedException, TimeoutException, IOException {
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
                    //log.info(event.toString());
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
    public void snmpValidationTest() throws InterruptedException, TimeoutException, IOException {
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
}


