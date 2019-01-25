package org.wso2.extension.siddhi.io.snmp.util;


import org.snmp4j.PDU;
import org.snmp4j.mp.SnmpConstants;

/**
 *
 * SNMP constants
 *
 */
public class SNMPConstants {
    // dynamic true
    public static final String HOST = "host";
    public static final String VERSION = "version";
    public static final String REQUEST_INTERVAL = "request.interval";
    public static final String COMMUNITY = "community";
    public static final String TYPE = "type";
    public static final String OIDS = "oids";

    // optional
    public static final String AGENT_PORT = "agent.port";
    public static final String MANAGER_PORT = "manager.port";
    public static final String IS_TCP = "istcp";
    public static final String RETRIES = "retries";
    public static final String TIMEOUT = "timeout";

    // v3
    public static final String USER_NAME = "user.name";
    public static final String SECURITY_MODE = "security.mode";
    public static final String USER_PASSWORD = "user.password";
    public static final String ENC_PROTOCOL = "encryption.protocol";
    public static final String AUT_PROTOCOL = "authentication.protocol";

    public static final String COMMA = ",";

    public static final int V1 = SnmpConstants.version1;
    public static final int V2C = SnmpConstants.version2c;
    public static final int V3 = SnmpConstants.version3;

    public static final int GET = PDU.GET;
    public static final int TRAP = PDU.TRAP;
    public static final int SET = PDU.SET;

    // default values
    public static final String DEFAULT_MANAGER_PORT = "162";
    public static final String DEFAULT_AGENT_PORT = "161";
    public static final String DEFAULT_IS_TCP = "false";
    public static final String DEFAULT_RETRIES = "5";
    public static final String DEFAULT_TIMEOUT = "1000";
    public static final String DEFAULT_USERNAME = USER_NAME;
    public static final String DEFAULT_USER_PASSWORD = USER_PASSWORD;
    public static final String DEFAULT_ENC_PROTOCOL = "noEnc";
    public static final String DEFAULT_AUT_PROTOCOL = "noAuth";


    private SNMPConstants(){}
}
