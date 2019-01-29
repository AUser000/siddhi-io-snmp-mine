package org.wso2.extension.siddhi.io.snmp.manager;

import org.apache.log4j.Logger;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Snmp Manager file
 */
public class SNMPManager {

    Logger log = Logger.getLogger(SNMPManager.class);
    Snmp snmp;

    public SNMPManagerConfig getManagerConfig() {
        return managerConfig;
    }

    SNMPManagerConfig managerConfig = null;
    private Lock lock = new ReentrantLock();

    public SNMPManager() {
    }



    public void setManagerConfig(SNMPManagerConfig managerConfig) throws IOException {
        this.managerConfig = managerConfig;
        snmp = new Snmp(managerConfig.getTransportMapping());

        if (managerConfig.getVersion() == SnmpConstants.version3
                && managerConfig.isTSM == false) {

            USM usm = new USM(SecurityProtocols.getInstance(),
                    managerConfig.getLocalEngineID(),
                    0);

            SecurityModels.getInstance().addSecurityModel(usm);

            snmp.getUSM().addUser(managerConfig.getUserName()
                    , new UsmUser(managerConfig.getUserName(),
                            managerConfig.getAuthProtocol(),
                            managerConfig.getAuthProtocolPass(),
                            managerConfig.getPrivProtocol(),
                            managerConfig.getPrivProtocolPass()));

        }
        snmp.listen();
    }

    public ResponseEvent send(Map<String, String> map, int type) throws IOException {
        for (Map.Entry<String, String> entry: map.entrySet()) {
            this.managerConfig.getPdu().add(new VariableBinding(new OID(entry.getKey()),
                    new OctetString(entry.getValue())));
        }
        managerConfig.getPdu().setType(type);
        ResponseEvent event = snmp.set(managerConfig.getPdu(), managerConfig.getCommunityTarget());
        return event;

    }

    public ResponseEvent send() throws IOException {
        ResponseEvent event = snmp.get(managerConfig.getPdu(), managerConfig.getCommunityTarget());
        return event;
    }



    public void close() {
        try {
            snmp.close();
            managerConfig.close();
        } catch (IOException e) {
            log.info(" hell ");
        }
    }

}
