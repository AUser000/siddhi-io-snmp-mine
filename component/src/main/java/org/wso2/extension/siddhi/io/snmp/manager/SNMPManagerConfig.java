package org.wso2.extension.siddhi.io.snmp.manager;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.SnmpConstants;
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

    TransportMapping transportMapping;
    boolean isTCP = false;
    PDU pdu = new PDU();
    List<String> oids;
    CommunityTarget communityTarget;
    UserTarget userTarget;
    private int version;

    // version 3 properties
    private OctetString userName;
    private OID authProtocol;
    private OctetString authProtocolPass;
    private OID privProtocol;
    private OctetString privProtocolPass;
    private int secLvl;
    private OctetString localEngineID;

    // version 3 getters and setters //
    boolean isTSM = false;

    public OctetString getLocalEngineID() {
        return localEngineID;
    }

    public void setLocalEngineID(OctetString getLocalEngineID) {
        this.localEngineID = getLocalEngineID;
    }

    public int getSecLvl() {
        return secLvl;
    }

    public void setSecLvl(int secLvl) {
        this.secLvl = secLvl;
    }

    public OctetString getUserName() {
        return userName;
    }

    public void setUserName(OctetString userName) {
        this.userName = userName;
    }

    public OID getAuthProtocol() {
        return authProtocol;
    }

    public void setAuthProtocol(OID authProtocol) {
        this.authProtocol = authProtocol;
    }

    public OctetString getAuthProtocolPass() {
        return authProtocolPass;
    }

    public void setAuthProtocolPass(OctetString authProtocolPass) {
        this.authProtocolPass = authProtocolPass;
    }

    public OID getPrivProtocol() {
        return privProtocol;
    }

    public void setPrivProtocol(OID privProtocol) {
        this.privProtocol = privProtocol;
    }

    public OctetString getPrivProtocolPass() {
        return privProtocolPass;
    }

    public void setPrivProtocolPass(OctetString privProtocolPass) {
        this.privProtocolPass = privProtocolPass;
    }

    public void setSec(OctetString userName,
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
    // version 3 getters and setters over //

    // version 3 Target
    public UserTarget getUserTarget() {
        return userTarget;
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
    // version 3 Target over

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setTransportMappingUDP() throws IOException {
        this.transportMapping = new DefaultUdpTransportMapping();
        isTCP = false;
    }

    public boolean isTcp() {
        return isTCP;
    }

    public void setTransportMappingTCP() throws IOException {
        this.transportMapping = new DefaultTcpTransportMapping();
        isTCP = true;
    }

    public TransportMapping getTransportMapping() {
        return transportMapping;
    }

    public void setCommunityTarget(String ip,
                          String port,
                          String community,
                          int retries,
                          int timeout,
                          int version) {
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
        if (version == SnmpConstants.version2c) {
            communityTarget.setVersion(SnmpConstants.version2c);
            setVersion(SnmpConstants.version2c);
        } else if (version == SnmpConstants.version1) {
            communityTarget.setVersion(SnmpConstants.version1);
            setVersion(SnmpConstants.version1);
        } else {
            log.info("snmp version is not set");
        }
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
            pdu = new ScopedPDU();
            pdu.addAll(vbs);
        }
    }

    public PDU getPdu() {
        return  this.pdu;
    }

    public void close() {
        this.pdu = null;
        this.pdu = new PDU();
    }

    public void setType() {
        pdu.setType(PDU.GET);
    }
}
