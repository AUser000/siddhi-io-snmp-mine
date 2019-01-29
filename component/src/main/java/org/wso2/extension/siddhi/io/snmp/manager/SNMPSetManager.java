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
//                List<VariableBinding> vbs = (List<VariableBinding>) event
//                        .getResponse()
//                        .getVariableBindings();

                log.info(event.getResponse().toString());
            } else {
                log.info(SNMPGetManager.class.getName() + " response pdu is null");
            }
        } else {
            log.info(SNMPGetManager.class.getName() + "event is null");
        }
    }
}
