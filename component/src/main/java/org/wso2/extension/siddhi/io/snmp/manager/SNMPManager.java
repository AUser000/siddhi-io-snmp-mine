/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.extension.siddhi.io.snmp.manager;

import org.apache.log4j.Logger;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

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
    TransportMapping transportMapping;
    boolean isTCP;

    public SNMPManagerConfig getManagerConfig() {
        return managerConfig;
    }

    SNMPManagerConfig managerConfig = null;
    private Lock lock = new ReentrantLock();

    public SNMPManager() {
    }

    public void setTransportMappingUDP() throws IOException {
        this.transportMapping = new DefaultUdpTransportMapping();
        isTCP = false;
    }

    public void setTransportMappingTCP() throws IOException {
        this.transportMapping = new DefaultTcpTransportMapping();
    }


    public void setManagerConfig(SNMPManagerConfig managerConfig) throws IOException {
        this.managerConfig = managerConfig;
        snmp = new Snmp(this.transportMapping);

        if (managerConfig.getVersion() == SnmpConstants.version3
                && managerConfig.isTSM == false) {

            USM usm = new USM(SecurityProtocols.getInstance(),
                    managerConfig.getLocalEngineID(),
                    0);

            SecurityModels.getInstance().addSecurityModel(usm);

            snmp.getUSM().addUser(managerConfig.getUserName()
                    , managerConfig.getUser());

        }
        snmp.listen();
    }

    public ResponseEvent send(Map<String, String> map, int type) throws IOException {
        for (Map.Entry<String, String> entry: map.entrySet()) {
            this.managerConfig.getPdu().add(new VariableBinding(new OID(entry.getKey()),
                    new OctetString(entry.getValue())));
        }
        if (managerConfig.getVersion() == SnmpConstants.version3) {
            USM usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(),
                        new OctetString(MPv3.createLocalEngineID()).substring(0, 9), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            snmp.getUSM().addUser(managerConfig.getUserName(), managerConfig.getUser());

            return snmp.set(managerConfig.getPdu(), managerConfig.getUserTarget());
        }
        return snmp.set(managerConfig.getPdu(), managerConfig.getCommunityTarget());
    }

    public ResponseEvent send() throws IOException {
        if (managerConfig.getVersion() == SnmpConstants.version3) {
            //log.info("v3 pdu sending");
            USM usm = new USM(SecurityProtocols.getInstance()
                    .addDefaultProtocols(),
                    new OctetString(MPv3.createLocalEngineID()).substring(0, 9), 0);

            SecurityModels.getInstance().addSecurityModel(usm);
            snmp.getUSM().addUser(
                    managerConfig.getUserName(),
                    managerConfig.getUser());
            return snmp.send(managerConfig.getPdu(), managerConfig.getUserTarget());
        }
        return snmp.get(managerConfig.getPdu(), managerConfig.getCommunityTarget());
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
