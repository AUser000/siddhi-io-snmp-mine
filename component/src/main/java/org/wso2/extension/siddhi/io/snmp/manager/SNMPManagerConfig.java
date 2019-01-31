package org.wso2.extension.siddhi.io.snmp.manager;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.List;

/**
 * Manager config
 */
public class SNMPManagerConfig {
    Logger log = Logger.getLogger(SNMPManagerConfig.class);

    boolean isTCP = false;
    PDU pdu = new PDU();
    CommunityTarget communityTarget;

    private int version;

    // version 3 properties
    ScopedPDU scopedPDU = new ScopedPDU();
    private OctetString userName;
    private OID authProtocol;
    private OctetString authProtocolPass;
    private OID privProtocol;
    private OctetString privProtocolPass;
    private int secLvl;
    private OctetString localEngineID;
    private UserTarget userTarget;

    // version 3 getters and setters //
    boolean isTSM = false;

    public OctetString getLocalEngineID() {
        return localEngineID;
    }

    public OctetString getUserName() {
        return userName;
    }

    public OID getAuthProtocol() {
        return authProtocol;
    }

    public OctetString getAuthProtocolPass() {
        return authProtocolPass;
    }

    public OID getPrivProtocol() {
        return privProtocol;
    }

    public OctetString getPrivProtocolPass() {
        return privProtocolPass;
    }

    public int getSecLvl() {
        return secLvl;
    }

    public void setUserMatrix(OctetString userName,
                       OID authProtocol,
                       OctetString authProtocolPass,
                       OID privProtocol,
                       OctetString privProtocolPass,
                       int secLvl) {
        this.userName = userName;
        this.authProtocol = authProtocol;
        this.authProtocolPass = authProtocolPass;
        this.privProtocol = privProtocol;
        this.privProtocolPass = privProtocolPass;
        this.secLvl = secLvl;
    }

    public UserTarget setUserTarget(String ip,
                                    String port,
                                    int retries,
                                    int timeout) {
        userTarget = new UserTarget();
        userTarget.setSecurityName(new OctetString(this.userName));
        userTarget.setSecurityLevel(this.secLvl);
        Address address;
        if (isTCP) {
            address = GenericAddress.parse("tcp:" + ip + "/" + port);
        } else {
            //setTransportMappingUDP();
            address = GenericAddress.parse("udp:" + ip + "/" + port);
        }
        userTarget.setAddress(address);
        userTarget.setRetries(retries);
        userTarget.setTimeout(timeout);
        userTarget.setVersion(SnmpConstants.version3);
        return userTarget;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setCommunityTarget(String ip,
                          String port,
                          String community,
                          int retries,
                          int timeout) {
        communityTarget = new CommunityTarget();
        communityTarget.setCommunity(new OctetString(community));
        Address address;
        if (isTCP) {
            address = GenericAddress.parse("tcp:" + ip + "/" + port);
        } else {
            //setTransportMappingUDP();
            address = GenericAddress.parse("udp:" + ip + "/" + port);
        }
        communityTarget.setAddress(address);
        communityTarget.setRetries(retries);
        communityTarget.setTimeout(timeout);
        if (this.version == SnmpConstants.version2c) {
            communityTarget.setVersion(SnmpConstants.version2c);
            setVersion(SnmpConstants.version2c);
        } else if (this.version == SnmpConstants.version1) {
            communityTarget.setVersion(SnmpConstants.version1);
            setVersion(SnmpConstants.version1);
        } else {
            log.info("snmp version is not set");
        }
    }

    public void setUserTarget(String ip,
                              String port,
                              int retries,
                              int timeout,
                              int securityLvl) {
        userTarget = new UserTarget();
        userTarget.setSecurityLevel(securityLvl);
        userTarget.setVersion(SnmpConstants.version3);
        userTarget.setTimeout(timeout);
        userTarget.setRetries(retries);
        Address address;
        if (isTCP) {
            address = GenericAddress.parse("tcp:" + ip + "/" + port);
        } else {
            address = GenericAddress.parse("udp:" + ip + "/" + port);
        }
        userTarget.setAddress(address);
        userTarget.setSecurityName(this.userName);
    }

    public UserTarget getUserTarget() {
        return this.userTarget;
    };

    public UsmUser getUser() {
        return new UsmUser(this.userName,
                this.authProtocol,
                this.authProtocolPass,
                this.privProtocol,
                this.privProtocolPass);
    }


    public Target getCommunityTarget() {
        return this.communityTarget;
    }

    public void setVariablebindings(List<VariableBinding> vbs) {
        if (vbs == null) {
            pdu = new PDU();
            return;
        }
        if (version != SnmpConstants.version3) {
            pdu = new PDU();
            pdu.addAll(vbs);
        } else {
            scopedPDU = new ScopedPDU();
            scopedPDU.addAll(vbs);
        }
    }

    public PDU getPdu() {
        if (version == SnmpConstants.version3) {
            //log.info(scopedPDU.toString());
            return scopedPDU;
        }
        //log.info(pdu.toString());
        return pdu;
    }

    public void close() {

        if (version == SnmpConstants.version3) {
            this.scopedPDU = null;
            this.scopedPDU = new ScopedPDU();
        } else {
            this.pdu = null;
            this.pdu = new PDU();
        }
    }

    public void setType() {
        pdu.setType(PDU.GET);
    }
}
