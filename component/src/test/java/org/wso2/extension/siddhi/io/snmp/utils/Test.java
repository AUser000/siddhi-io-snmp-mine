package org.wso2.extension.siddhi.io.snmp.utils;


import java.io.IOException;

public class Test {
    public static void main(String args[]) throws IOException, InterruptedException {
        Agent agent = new Agent(new AdvancedCommandProcessor());
        agent.start("127.0.0.1", "2019");
        for (;;) {
            Thread.sleep(5000);

        }
    }
}
