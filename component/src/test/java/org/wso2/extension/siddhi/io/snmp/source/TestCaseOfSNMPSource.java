package org.wso2.extension.siddhi.io.snmp.source;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import org.wso2.extension.siddhi.io.snmp.agent.MyAgentV1;
import org.wso2.extension.siddhi.io.snmp.agent.MyAgentV2;
import org.wso2.extension.siddhi.io.snmp.agent.MyAgentV3;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TestCaseOfSNMPSource {
    private static final Logger LOG  = Logger.getLogger(TestCaseOfSNMPSource.class);
    String port = "2001";
    String ip = "127.0.0.1";

    @Test
    public void snmpVersion1Source() throws InterruptedException, TimeoutException, IOException {
        LOG.info("-----------------------------------------------");
        LOG.info("       SNMP Version 1 Source Test Case     ");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();

        LOG.info("[TestCaseOfSNMPSource.class] snmp agent starting ");
        MyAgentV1 agent = new MyAgentV1(ip + "/" + port);
        LOG.info("[TestCaseOfSNMPSource.class] Siddhi starting ");
        agent.start();
        Thread.sleep(500);


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
                    //log.info("event recieved  hello" + event);
                }
            }
        });

        executionPlanRuntime.start();
        Thread.sleep(5000);
        LOG.info("[TestCaseOfSNMPSource.class] agent shutting down");
        agent.stop();
        LOG.info("[TestCaseOfSNMPSource.class] Siddhi shutting down");
        siddhiManager.shutdown();
    }

    /**
     * Test the ability to subscripe to a NATS subject from the beginning.
     */
    @Test
    public void snmpVersion2Source() throws InterruptedException, TimeoutException, IOException {
        LOG.info("-----------------------------------------------");
        LOG.info("       SNMP Version 2 Source Test Case     ");
        LOG.info("-----------------------------------------------");

        SiddhiManager siddhiManager = new SiddhiManager();

        MyAgentV2 agent = new MyAgentV2(ip + "/" + port);
        agent.start();
        Thread.sleep(500);



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
                    //log.info("event recieved  hello" + event);
                }
            }
        });

        LOG.info("[TestCaseOfSNMPSource.class] Siddhi manager starting");
        executionPlanRuntime.start();
        Thread.sleep(5000);
        LOG.info("[TestCaseOfSNMPSource.class] agent shutting down");
        agent.stop();
        LOG.info("[TestCaseOfSNMPSource.class] Siddhi shutting down");
        siddhiManager.shutdown();
    }

    @Test
    public void snmpVersion3TestWithAllValues() throws InterruptedException, TimeoutException, IOException {
        LOG.info("-----------------------------------------------");
        LOG.info("       SNMP Version 3 Source Test Case     ");
        LOG.info("-----------------------------------------------");

        LOG.info("[TestCaseOfSNMPSource.class] Agent starting");
        MyAgentV3 agent = new MyAgentV3(ip, port);
        agent.start();
        Thread.sleep(500);

        // agent start() bind with a port
        // agent wait()

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
                "user.name = 'agent5') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    //log.info(event.toString());
                }
            }
        });

        LOG.info("[TestCaseOfSNMPSource.class] Siddhi manager starting");
        executionPlanRuntime.start();
        Thread.sleep(5000);

        LOG.info("[TestCaseOfSNMPSource.class] Agent shutting down");
        agent.stop();
        LOG.info("[TestCaseOfSNMPSource.class] Siddhi manager shutting down");
        siddhiManager.shutdown();
    }
}


