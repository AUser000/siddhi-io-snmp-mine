package org.wso2.extension.siddhi.io.snmp.sink;

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
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.query.api.exception.SiddhiAppValidationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TestCaseOfSNMPSinkV3 {


    private static final Logger log = Logger.getLogger(TestCaseOfSNMPSinkV3.class);
    String port = "2019";
    String ip = "127.0.0.1";
    Agent agent;
    AdvancedCommandProcessor processor;
    EventHolder eventHolder;

    @BeforeClass
    public void startAgent() throws IOException {
        processor = new AdvancedCommandProcessor();
        eventHolder = new EventHolder();
        processor.setEventListener(eventHolder);
        agent = new Agent(processor);
        agent.start(ip, port);
        log.info("agent started.");
    }

    @AfterClass
    public void stopAgent() {
        //log.info(processor.getEventListener().getEvent(0).toString());
        agent.stop();
    }

    @BeforeMethod
    public void clearHolder() {
        eventHolder.clear();
    }


    @Test
    public void snmpVersion3Sink() throws InterruptedException, TimeoutException, IOException {

        log.info("-----------------------------------------------");
        log.info("       SNMP Version 3 Sink Test Case     ");
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
                for (Event event: events) {
                    log.info(event.toString());
                }
            }
        });


        log.info(" Siddhi manager started ");
        executionPlanRuntime.start();

        inputStream.send(new Object[]{"mail@wso2.com"});
        inputStream.send(new Object[]{"mail@wso2.com"});
        inputStream.send(new Object[]{"mail@wso2.com"});
        inputStream.send(new Object[]{"dhanu@wso2.com"});
        inputStream.send(new Object[]{"mail@wso2.com"});
        Thread.sleep(1000);

        Assert.assertTrue(eventHolder.assertDataContent("mail@wso2.com", 0));

        log.info(" siddhi manager shutting down ");
        siddhiManager.shutdown();
    }


    @Test(expectedExceptions = SiddhiAppValidationException.class)
    public void snmpVersion3SinkException() throws InterruptedException, TimeoutException, IOException {

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
                "priv.protocol = 'PRVDES',\n" +
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
                for (Event event: events) {
                    log.info(event.toString());
                }
            }
        });


        log.info(" Siddhi manager started ");
        executionPlanRuntime.start();
        log.info(" siddhi manager shutting down ");
        siddhiManager.shutdown();
    }

}