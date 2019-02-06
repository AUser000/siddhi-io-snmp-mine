package org.wso2.extension.siddhi.io.snmp.agent;

import org.apache.log4j.Logger;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.transport.TransportMappings;

import java.io.File;
import java.io.IOException;

// package and imports removed
/**
 * This Agent contains mimimal functionality for running a version 2c snmp agent.
 */
public class MyAgentV1 extends BaseAgent {

    private static final Logger LOG = Logger.getLogger(MyAgentV2.class);
    private String address;

    public MyAgentV1(String address) throws IOException {

        // These files does not exist and are not used but has to be specified
        // Read snmp4j docs for more info
        super(new File("conf.agent"), new File("bootCounter.agent"),
                new CommandProcessor(
                        new OctetString(MPv3.createLocalEngineID())));
        this.address = address;
    }

    /**
     * We let clients of this agent register the MO they
     * need so this method does nothing
     */
    @Override
    protected void registerManagedObjects() {
    }

    /**
     * Clients can register the MO they need
     */
    public void registerManagedObject(ManagedObject mo) {
        try {
            server.register(mo, null);
        } catch (DuplicateRegistrationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void unregisterManagedObject(MOGroup moGroup) {
        moGroup.unregisterMOs(server, getContext(moGroup));
    }

    /*
     * Empty implementation
     */
    @Override
    protected void addNotificationTargets(SnmpTargetMIB targetMIB,
                                          SnmpNotificationMIB notificationMIB) {
    }

    /**
     * Minimal View based Access Control
     *
     * http://www.faqs.org/rfcs/rfc2575.html
     */
    @Override
    protected void addViews(VacmMIB vacm) {

        vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv1, new OctetString(
                        "cpublic"), new OctetString("v1v2group"),
                StorageType.nonVolatile);

        vacm.addAccess(new OctetString("v1v2group"), new OctetString("public"),
                SecurityModel.SECURITY_MODEL_ANY, SecurityLevel.NOAUTH_NOPRIV,
                MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"),
                new OctetString("fullWriteView"), new OctetString(
                        "fullNotifyView"), StorageType.nonVolatile);

        vacm.addViewTreeFamily(new OctetString("fullReadView"), new OID("1.3"),
                new OctetString(), VacmMIB.vacmViewIncluded,
                StorageType.nonVolatile);
    }

    /**
     * User based Security Model, only applicable to
     * SNMP v.3
     *
     */
    protected void addUsmUser(USM usm) {
    }

    protected void initTransportMappings() throws IOException {
        transportMappings = new TransportMapping[1];
        Address addr = GenericAddress.parse(address);
        TransportMapping tm = TransportMappings.getInstance()
                .createTransportMapping(addr);
        transportMappings[0] = tm;
    }

    /**
     * Start method invokes some initialization methods needed to
     * start the agent
     * @throws IOException
     */
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
        LOG.info("[MyAgentV1] Agent started in " + address);
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

    protected void unregisterManagedObjects() {
        // here we should unregister those objects previously registered...
    }

    /**
     * The table of community strings configured in the SNMP
     * engine's Local Configuration Datastore (LCD).
     *
     * We only configure one, "public".
     */
    protected void addCommunities(SnmpCommunityMIB communityMIB) {
        Variable[] com2sec = new Variable[] {
                new OctetString("public"), // community name
                new OctetString("cpublic"), // security name
                getAgent().getContextEngineID(), // local engine ID
                new OctetString("public"), // default context name
                new OctetString(), // transport tag
                new Integer32(StorageType.nonVolatile), // storage type
                new Integer32(RowStatus.active) // row status
        };
        MOTableRow row = communityMIB.getSnmpCommunityEntry().createRow(
                new OctetString("public2public").toSubIndex(true), com2sec);
        communityMIB.getSnmpCommunityEntry().addRow((SnmpCommunityMIB.SnmpCommunityEntryRow) row);
    }
}
