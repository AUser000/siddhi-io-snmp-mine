package org.wso2.extension.siddhi.io.snmp.manager;

import org.snmp4j.mp.SnmpConstants;

import java.io.IOException;

/**
 * Java Doc Comment
 */
public class Test {
    public static void main(String args[]) throws IOException, InterruptedException {
        SNMPManagerConfig managerConfig = new SNMPManagerConfig();
        managerConfig.setOIDs(".1.3.6.1.2.1.1.3.0,.1.3.6.1.2.1.1.4.0");
        managerConfig.setRequestInterval(1500);
        managerConfig.setCommunityTarget("127.0.0.1", "161", "public", 2, 1500, SnmpConstants.version2c);
        managerConfig.setType();

        SNMPManager manager = SNMPManager.getInstance();
        manager.setManagerConfig(managerConfig);
        manager.start();
        Thread.sleep(5000);
        manager.stop();
    }


}
