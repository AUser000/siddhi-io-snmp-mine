package org.wso2.extension.siddhi.io.snmp.manager;

import org.snmp4j.event.ResponseEvent;

/**
 * doc
 *
 */
public interface ResponseHandler {
    void onResponse(ResponseEvent response);
}
