package org.wso2.extension.siddhi.io.snmp.source;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCaseOfSNMPSource {
    private Logger log  = Logger.getLogger(TestCaseOfSNMPSource.class);

    private AtomicInteger eventCounter = new AtomicInteger(0);

    @BeforeMethod
    private void setUp() {
        eventCounter.set(0);
    }

    @BeforeClass
    private void initializeDockerContainer() throws InterruptedException {
        eventCounter.set(0);
    }

    @Test
    public void snmpVersion1Source() throws InterruptedException, TimeoutException, IOException {

        SiddhiManager siddhiManager = new SiddhiManager();

        // agent staring part
        // agent start() bind with a port
        // agent wait()


        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.4.0') ),\n" +
                "host ='127.0.0.1',\n" +
                "version = 'v1',\n" +
                "agent.port = '161',\n" +
                "request.interval = '500',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.4.0',\n" +
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

        // agent stop()
        // agent close()

        executionPlanRuntime.start();
        Thread.sleep(8000);
        log.info("sleep is over");
        siddhiManager.shutdown();
    }

    /**
     * Test the ability to subscripe to a NATS subject from the beginning.
     */
    @Test
    public void snmpVersion2Source() throws InterruptedException, TimeoutException, IOException {

        SiddhiManager siddhiManager = new SiddhiManager();

        // agent staring part
        // agent start() bind with a port
        // agent wait()


        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.4.0') ),\n" +
                "host ='127.0.0.1',\n" +
                "version = 'v2c',\n" +
                "type = 'snmp.get',\n" +
                "agent.port = '161',\n" +
                "request.interval = '500',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.4.0',\n" +
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

        // agent stop()
        // agent close()

        executionPlanRuntime.start();
        Thread.sleep(8000);
        log.info("sleep is over");
        siddhiManager.shutdown();
    }

    @Test
    public void snmpVersion3TestWithAllValues() throws InterruptedException, TimeoutException, IOException {

        // agent staring part
        // agent start() bind with a port
        // agent wait()

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('test') \n" +
                "@source(type='snmp', \n" +
                "@map(type='keyvalue', " +
                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                "host ='127.0.0.1',\n" +
                "version = 'v3',\n" +
                "timeout = '100',\n" +
                "request.interval = '100',\n" +
                "agent.port = '161',\n" +
                "community = 'public',\n" +
                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                "priv.password = 'privpass',\n" +
                "auth.protocol = 'AUTHMD5',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "auth.password = 'authpass',\n" +
                "priv.password = 'privpass',\n" +
                "user.name = 'agent5') \n" +
                " define stream inputStream(value1 string, value2 string);\n";

        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        executionPlanRuntime.addCallback("inputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    log.info(event.toString());
                }
            }
        });
        executionPlanRuntime.start();
        Thread.sleep(1500);

        // agent stop()
        // agent close()

        log.info("sleep is over");
        siddhiManager.shutdown();
    }
}


