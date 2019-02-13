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

import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.Map;

/**
 *
 *
 */
public class SNMPSetManager extends SNMPManager {

    public void validateResponseAndNotify(ResponseEvent event) {
        if (event != null) {
            if (event.getResponse() != null) {
                log.debug(event.getResponse().toString());
            } else {
                log.debug(SNMPGetManager.class.getName() + " response pdu is null");
            }
        } else {
            log.debug(SNMPGetManager.class.getName() + "event is null");
        }
    }

    // for sending set request messages
    public ResponseEvent send(Map<String, String> map) throws IOException {
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
}
