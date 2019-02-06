package org.wso2.extension.siddhi.io.snmp.sink;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import org.wso2.extension.siddhi.io.snmp.agent.MyAgentV2;
import org.wso2.extension.siddhi.io.snmp.agent.MyAgentV3;
import org.wso2.extension.siddhi.io.snmp.source.TestCaseOfSNMPSource;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.stream.input.InputHandler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Contains test cases for SNMP sink.
 */
public class TestCaseOfSNMPSink {
    private static final Logger LOG  = Logger.getLogger(TestCaseOfSNMPSource.class);
    String port = "2019";
    String ip = "127.0.0.1";


    /**
     * Test for configure the SNMP Sink to publish the set request to a SNMP-agent.
     */
    @Test
    public void snmpVersion2Sink() throws InterruptedException, TimeoutException, IOException {
        LOG.info("-----------------------------------------------");
        LOG.info("       SNMP Version 2 Sink Test Case     ");
        LOG.info("-----------------------------------------------");

        MyAgentV2 agent = new MyAgentV2(ip + "/" + port);
        agent.start();
        LOG.info("[TestCaseOfSNMPSink] Agent started ");

        Thread.sleep(500);

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('snmpSink') \n" +
                "\n" +
                "@Sink(type='snmp',\n" +
                "@map(type='keyvalue', @payload('1.3.6.1.2.1.1.1.0' = 'value')),\n" +
                "host = '" + ip + "',\n" +
                "version = 'v2',\n" +
                "community = 'public',\n" +
                "agent.port = '" + port + "',\n" +
                "retries = '5')\n" +
                "define stream outputStream(value string);\n" +
                "\n" +
                "@sink(type='log')\n" +
                "define stream testStream(value string);\n" +
                "\n" +
                "@info(name='productionProcessingQuery')\n" +
                "from outputStream\n" +
                "select value\n" +
                "insert into testStream;";


        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
        InputHandler inputStream = executionPlanRuntime.getInputHandler("outputStream");

        LOG.info("[TestCaseOfSNMPSink] Siddhi manager started ");
        executionPlanRuntime.start();

        inputStream.send(new Object[]{"banana"});
        inputStream.send(new Object[]{"banana"});
        inputStream.send(new Object[]{"banana"});
        Thread.sleep(3000);


        LOG.info("[TestCaseOfSNMPSink] Siddhi manager shutting down ");
        siddhiManager.shutdown();
        LOG.info("[TestCaseOfSNMPSink] SNMP agent shutting down \n");
        agent.stop();
    }

    /**
     * Test the ability to subscripe to a NATS subject from the beginning.
     */
    @Test
    public void snmpVersion3Sink() throws InterruptedException, TimeoutException, IOException {

        LOG.info("-----------------------------------------------");
        LOG.info("       SNMP Version 3 Sink Test Case     ");
        LOG.info("-----------------------------------------------");

        MyAgentV3 agent = new MyAgentV3(ip , port);
        LOG.info("[TestCaseOfSNMPSink] agent started ");
        agent.start();
        Thread.sleep(500);

        SiddhiManager siddhiManager = new SiddhiManager();
        String siddhiApp = "@App:name('snmpSink') \n" +
                "\n" +
                "@Sink(type='snmp',\n" +
                "@map(type='keyvalue', @payload('1.3.6.1.2.1.1.3.0' = 'value', '1.3.6.1.2.1.1.2.0' = 'value2')),\n" +
                "host = '127.0.0.1',\n" +
                "version = 'v3',\n" +
                "agent.port = '161',\n" +
                "priv.password = 'privpass',\n" +
                "auth.protocol = 'AUTHMD5',\n" +
                "priv.protocol = 'PRIVDES',\n" +
                "auth.password = 'authpass',\n" +
                "priv.password = 'privpass',\n" +
                "user.name = 'agent5', \n" +
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

        LOG.info("[TestCaseOfSNMPSink] Siddhi manager started ");
        executionPlanRuntime.start();

        inputStream.send(new Object[]{"banana", "Hello"});
        inputStream.send(new Object[]{"banana", "Hello"});
        inputStream.send(new Object[]{"banana", "Hello"});
        Thread.sleep(3000);

        LOG.info("[TestCaseOfSNMPSink] siddhi manager shutting down ");
        siddhiManager.shutdown();
        LOG.info("[TestCaseOfSNMPSink] SNMP agent shutting down ");
        agent.stop();
    }
}

