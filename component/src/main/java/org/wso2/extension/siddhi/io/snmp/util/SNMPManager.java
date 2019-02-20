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
package org.wso2.extension.siddhi.io.snmp.util;

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
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Handle SNMP Manager data and request
 */
public class SNMPManager {

    private Logger log = Logger.getLogger(SNMPManager.class);
    private Snmp snmp;
    private TransportMapping transportMapping;
    private SNMPManagerConfig managerConfig;
    private SourceEventListener sourceEventListener;

    public SNMPManager() {
    }

    public void setSourceEventListener(SourceEventListener sourceEventListener) {
        this.sourceEventListener = sourceEventListener;
    }

    public void setUDPTransportMapping() throws IOException {
        this.transportMapping = new DefaultUdpTransportMapping();
    }

    public void setTCPTransportMapping() throws IOException {
        this.transportMapping = new DefaultTcpTransportMapping();
    }

    public void setManagerConfig(SNMPManagerConfig managerConfig) throws IOException {
        if (managerConfig.isTCP()) {
            setTCPTransportMapping();
        } else {
            setUDPTransportMapping();
        }
        this.managerConfig = managerConfig;
        snmp = new Snmp(this.transportMapping);

        if (managerConfig.getVersion() == SnmpConstants.version3) {
            USM usm = new USM(SecurityProtocols.getInstance(), null, 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            snmp.getUSM().addUser(managerConfig.getUserName(), managerConfig.getUser());
        }
        snmp.listen();
    }

    public void close() {
        try {
            if (snmp != null) {
                snmp.close();
            }
            if (managerConfig != null) {
                managerConfig.clear();
            }
        } catch (IOException e) {
            log.info("  ");
        }
    }

    // get snmp event, rearrange data and notify to siddhi
    private void validateResponseAndNotify(ResponseEvent event) {
        if (event != null) {
            if (event.getResponse() != null) {
                List<VariableBinding> vbs = (List<VariableBinding>) event.getResponse().getVariableBindings();
                Map<String, String> map = new HashMap<>();
                for (VariableBinding vb: vbs) {
                    map.put(vb.getOid().toString(), vb.getVariable().toString());
                }
                sourceEventListener.onEvent(map, null);
            }
        }
    }

    // make get-request
    private ResponseEvent send() throws IOException {
        if (managerConfig.getVersion() == SnmpConstants.version3) {
            USM usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(),
                    new OctetString(MPv3.createLocalEngineID()).substring(0, 9), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            snmp.getUSM().addUser(managerConfig.getUserName(), managerConfig.getUser());
            return snmp.get(managerConfig.getPdu(), managerConfig.getUserTarget());
        }
        return snmp.get(managerConfig.getPdu(), managerConfig.getCommunityTarget());
    }

    public void sendAndNotify() throws IOException {
        validateResponseAndNotify(send());
    }

    // set snmp event validation
    private void validateResponse(ResponseEvent event) {
        if (event == null) {
            throw new RuntimeException(" No such target or invalid authentication ");
        }
        if (event.getResponse() == null) {
            throw new RuntimeException(" No such target ");
        }
        if (event.getResponse().getErrorIndex() != 0) {
            throw new RuntimeException(" Target has no privilege to write ");
        }
    }
    // make set-request
    private ResponseEvent send(Map<String, String> map) throws IOException {
        for (Map.Entry<String, String> entry: map.entrySet()) {
            this.managerConfig.getPdu()
                    .add(new VariableBinding(new OID(entry.getKey()), new OctetString(entry.getValue())));
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

    public void sendAndValidate(Map<String, String> map) throws IOException {
        validateResponse(send(map));
    }
}
