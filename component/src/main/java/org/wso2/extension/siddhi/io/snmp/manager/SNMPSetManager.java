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

import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;

import java.io.IOException;
import java.util.Map;

/**
 *
 *
 */
public class SNMPSetManager extends SNMPManager {

    public ResponseEvent send(Map<String, String> map) throws IOException {
        return send(map, PDU.SET);
    }

    public void validateResponseAndNotify(ResponseEvent event) {
        if (event != null) {
            if (event.getResponse() != null) {
                //log.info(event.getResponse().toString());
            } else {
                log.info(SNMPGetManager.class.getName() + " response pdu is null");
            }
        } else {
            log.info(SNMPGetManager.class.getName() + "event is null");
        }
    }
}
