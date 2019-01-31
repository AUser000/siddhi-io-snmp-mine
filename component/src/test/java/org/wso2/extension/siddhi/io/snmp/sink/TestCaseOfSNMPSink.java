package org.wso2.extension.siddhi.io.snmp.sink;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.extension.siddhi.io.snmp.source.TestCaseOfSNMPSource;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.stream.input.InputHandler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCaseOfSNMPSink {
        // If you will know about this related testcase,
        //refer https://github.com/wso2-extensions/siddhi-io-file/blob/master/component/src/test
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
    public void snmpVersion2Sink() throws InterruptedException, TimeoutException, IOException {

        String port = "161";

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('snmpSink') \n" +
                "\n" +
                "@Sink(type='snmp',\n" +
                "@map(type='keyvalue', @payload('1.3.6.1.2.1.1.3.0' = 'value', '1.3.6.1.2.1.1.2.0' = 'value2')),\n" +
                "host = '127.0.0.1',\n" +
                "version = 'v2',\n" +
                "community = 'public',\n" +
                "agent.port = '161',\n" +
                "retries = '5')\n" +
                "define stream outputStream(value string, value2 string);\n" +
                "\n" +
                "@sink(type='log')\n" +
                "define stream testStream(value string, value2 string);\n" +
                "\n" +
                "@info(name='productionProcessingQuery')\n" +
                "from outputStream\n" +
                "select value, value2\n" +
                "insert into testStream;";


        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        InputHandler inputStream = executionPlanRuntime.getInputHandler("outputStream");
        executionPlanRuntime.start();

        inputStream.send(new Object[]{"banana", "Hello"});
        inputStream.send(new Object[]{"banana", "Hello"});
        inputStream.send(new Object[]{"banana", "Hello"});
        Thread.sleep(3000);

        log.info("sleep is over");
        siddhiManager.shutdown();
    }

    /**
     * Test the ability to subscripe to a NATS subject from the beginning.
     */
    @Test
    public void snmpBasicSource() throws InterruptedException, TimeoutException, IOException {

        String port = "161";

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('snmpSink') \n" +
                "\n" +
                "@Sink(type='snmp',\n" +
                "@map(type='keyvalue', @payload('1.3.6.1.2.1.1.3.0' = 'value', '1.3.6.1.2.1.1.2.0' = 'value2')),\n" +
                "host = '127.0.0.1',\n" +
                "version = 'v3',\n" +
                "community = 'public',\n" +
                "agent.port = '161',\n" +
                "retries = '5')\n" +
                "define stream outputStream(value string, value2 string);\n" +
                "\n" +
                "@sink(type='log')\n" +
                "define stream testStream(value string, value2 string);\n" +
                "\n" +
                "@info(name='productionProcessingQuery')\n" +
                "from outputStream\n" +
                "select value, value2\n" +
                "insert into testStream;";


        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        InputHandler inputStream = executionPlanRuntime.getInputHandler("outputStream");
        executionPlanRuntime.start();

        inputStream.send(new Object[]{"banana", "Hello"});
        inputStream.send(new Object[]{"banana", "Hello"});
        inputStream.send(new Object[]{"banana", "Hello"});
        Thread.sleep(3000);

        log.info("sleep is over");
        siddhiManager.shutdown();
    }
}

