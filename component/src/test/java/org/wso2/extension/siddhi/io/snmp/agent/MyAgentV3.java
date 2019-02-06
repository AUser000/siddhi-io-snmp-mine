package org.wso2.extension.siddhi.io.snmp.agent;

import org.apache.log4j.Logger;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.TransportMappings;

import java.io.File;
import java.io.IOException;

public class MyAgentV3 extends BaseAgent {
    public static final Logger LOG = Logger.getLogger(MyAgentV3.class);

    private static OctetString localEngineId = new OctetString(MPv3.createLocalEngineID()).substring(0, 9);
    String address;

    public MyAgentV3(String ip, String port) {
        //super(bootCounterFile, configFile, commandProcessor);
        super(new File("conf.agent"),
                new File("bootCounter.agent"),
                new CommandProcessor(new OctetString(localEngineId)));
        this.address = ip + "/" + port;
    }

    @Override
    protected void registerManagedObjects() {

    }

    @Override
    protected void unregisterManagedObjects() {

    }

    @Override
    protected void addUsmUser(USM usm) {

        usm.addUser(new UsmUser(new OctetString("agent5"),
                AuthMD5.ID,
                new OctetString("authpass"),
                PrivDES.ID,
                new OctetString("privpass")));

        this.usm = usm;
    }

    @Override
    protected void addNotificationTargets(SnmpTargetMIB snmpTargetMIB, SnmpNotificationMIB snmpNotificationMIB) {

    }

    @Override
    protected void addViews(VacmMIB vacmMIB) {
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_USM,
                new OctetString("cpublic"),
                new OctetString("v3group"),
                StorageType.nonVolatile);

        vacmMIB.addAccess(new OctetString("v3group"),
                new OctetString("public"),
                SecurityModel.SECURITY_MODEL_USM,
                SecurityLevel.NOAUTH_NOPRIV,
                MutableVACM.VACM_MATCH_EXACT,
                new OctetString("fullReadView"),
                new OctetString("fullWriteView"),
                new OctetString("fullNotifyView"),
                StorageType.nonVolatile);

        vacmMIB.addViewTreeFamily(new OctetString("fullReadView"),
                new OID("1.3"),
                new OctetString(),
                VacmMIB.vacmViewIncluded,
                StorageType.nonVolatile);

    }

    protected void registerSnmpMIBs() {

    }

    protected void initTransportMappings() throws IOException {
        transportMappings = new TransportMapping[1];
        Address addr = GenericAddress.parse(address);
        TransportMapping tm = TransportMappings.getInstance().createTransportMapping(addr);
        transportMappings[0] = tm;
    }


    @Override
    protected void addCommunities(SnmpCommunityMIB snmpCommunityMIB) {

    }


    public void start() throws IOException {
        init();
        // This method reads some old config from a file and causes
        // unexpected behavior.
        // loadConfig(ImportModes.REPLACE_CREATE);
        addShutdownHook();
        getServer().addContext(new OctetString("public"));
        finishInit();
        run();
        sendColdStartNotification();
        LOG.info("[MyAgentV3] Agent start in " + address);
    }

    protected void initMessageDispatcher() {
        this.dispatcher = new MessageDispatcherImpl();
        this.mpv3 = new MPv3(this.agent.getContextEngineID().getValue());
        this.usm = new USM(SecurityProtocols.getInstance(), this.agent.getContextEngineID(), this.updateEngineBoots());
        SecurityModels.getInstance().addSecurityModel(this.usm);
        SecurityProtocols.getInstance().addDefaultProtocols();
        this.dispatcher.addMessageProcessingModel(new MPv1());
        this.dispatcher.addMessageProcessingModel(new MPv2c());
        this.dispatcher.addMessageProcessingModel(this.mpv3);
        this.initSnmpSession();
    }
}
