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

    /**
     * Test the ability to subscripe to a NATS subject from the beginning.
     */
    @Test
    public void snmpBasicSource() throws InterruptedException, TimeoutException, IOException {

        String port = "161";

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = " @Sink(type='snmp',\n" +
                "@map(type='keyvalue', @payload('1.2.3' = 'value', '1.2.3.4' = 'value2')),\n" +
                "host = '127.0.0.1',\n" +
                "version = 'v2c',\n" +
                "community = 'public',\n" +
                "agent.port = '161', \n" +
                "retries = '5'\n" +
                ")\n" +
                "define stream outputStream(value string, value2 string); \n";

        String siddhi = "@info(name='productionProcessingQuery')\n" +
                "from outputStream\n" +
                "select value, value2\n" +
                "insert into testStream;";

        String si = "@sink(type='log') \n" +
                "define stream testStream(value string, value2 string); \n";


        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp+ si+siddhi);
        InputHandler inputStream = executionPlanRuntime.getInputHandler("outputStream");
        executionPlanRuntime.start();

        inputStream.send(new Object[]{"AJAY", "Hello"});
        inputStream.send(new Object[]{"AJAY", "Hello"});
        inputStream.send(new Object[]{"AJAY", "Hello"});
        Thread.sleep(3000);

        log.info("sleep is over");
        siddhiManager.shutdown();
    }
}

