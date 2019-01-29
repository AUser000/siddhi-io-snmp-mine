package org.wso2.extension.siddhi.io.snmp.manager;

import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public class SNMPGetManager extends SNMPManager {
    private SourceEventListener sourceEventListener;

    public SourceEventListener getSourceEventListener() {
        return sourceEventListener;
    }

    public void setSourceEventListener(SourceEventListener sourceEventListener) {
        this.sourceEventListener = sourceEventListener;
    }

    public void validateResponseAndNotify(ResponseEvent event) {
        if (event != null) {
            if (event.getResponse() != null) {
                //log.info(event.getResponse().toString());
                List<VariableBinding> vbs = (List<VariableBinding>) event
                        .getResponse()
                        .getVariableBindings();
                Map<String, String> map = new HashMap<>();
                for (VariableBinding vb: vbs) {
                    map.put(vb.getOid().toString(), vb.getVariable().toString());
                }
                sourceEventListener.onEvent(map, null);
                //log.info(event.getResponse().toString());
            } else {
                log.info(SNMPGetManager.class.getName() + " response pdu is null");
            }
        } else {
            log.info(SNMPGetManager.class.getName() + "event is null");
        }
    }

}
