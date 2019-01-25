package org.wso2.extension.siddhi.io.snmp.manager;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Snmp Manager file
 */
public class SNMPManager {
    Logger log = Logger.getLogger(SNMPManager.class);
    private Snmp snmp;
    private boolean started = false;
    private static SNMPManager snmpManager = new SNMPManager();
    private SNMPManagerConfig managerConfig = null;
    private SourceEventListener sourceEventListener;
    private Lock lock = new ReentrantLock();

    private SNMPManager() {
    }

    public static SNMPManager getInstance() {
        return snmpManager;
    }

    public void setManagerConfig(SNMPManagerConfig managerConfig) throws IOException {
        this.managerConfig = managerConfig;
        snmp = new Snmp(managerConfig.getTransportMapping());

        if (managerConfig.getVersion() == SnmpConstants.version3
                && managerConfig.isTSM == false) {

            USM usm = new USM(SecurityProtocols.getInstance(),
                    managerConfig.getLocalEngineID(),
                    0);

            SecurityModels.getInstance().addSecurityModel(usm);

            snmp.getUSM().addUser(managerConfig.getUserName()
                    , new UsmUser(managerConfig.getUserName(),
                            managerConfig.getAuthProtocol(),
                            managerConfig.getAuthProtocolPass(),
                            managerConfig.getPrivProtocol(),
                            managerConfig.getPrivProtocolPass()));

        }
        snmp.listen();
    }

    public void start() {
        if (!started) {
            started = true;
            while (started) {
                try {
                    Thread.sleep(managerConfig.getRequestInterval());
                } catch (InterruptedException e) {
                    log.info(e);
                }
                //log.info("running");
                ResponseEvent event = null;
                try {
                    event = request();
                } catch (IOException e) {
                    log.info(e);
                }
                validateAndLog(event);
            }
        }
    }

    public void startInAnotherThread() {
        class Server implements Runnable {
            SourceEventListener sourceEventListener;
            boolean started = false;

            public Server(SourceEventListener sourceEventListener, boolean started) {
                this.sourceEventListener = sourceEventListener;
            }

            @Override
            public void run() {
                started = true;
                while (started) {
                    ResponseEvent event;
                    try {
                        event = snmp.send(managerConfig.getPdu(), managerConfig.getCommunityTarget(), null);
                        if (event != null) {
                            if (event.getResponse() != null) {
                                List<VariableBinding> vbs = (List<VariableBinding>) event
                                        .getResponse().getVariableBindings();
                                Map<String, String> map = new HashMap<String, String>();
                                for (VariableBinding vb : vbs) {
                                    map.put(vb.getOid().toDottedString(), vb.getVariable().toString());
                                }
                                this.sourceEventListener.onEvent(map, null);
                                //log.info("\n"+ map.toString()+ "\n");
                            } else {
                                log.info("response pdu is null");
                            }
                        } else {
                            log.info("event is null");
                        }
                        try {
                            Thread.sleep(managerConfig.getRequestInterval());
                        } catch (InterruptedException e) {
                            log.info(e);
                        }

                    } catch (IOException e) {
                        log.info(e);
                    }
                }
            }
        }
        Thread t = new Thread(new Server(sourceEventListener, started));
        t.start();
    }


    public void stop() {
        if (started) {
            started = false;
            managerConfig = null;
        }
    }

    public void resume() {
        if (!started) {
            started = true;
            start();
        }
    }

    public void pause() {
        if (started) {
            started = false;
        }
    }


    public void addSourceEventListener(SourceEventListener sourceEventListener) {
        this.sourceEventListener = sourceEventListener;
    }

    private ResponseEvent request() throws IOException {
        //log.info("sending request");
        if (managerConfig.getPdu() == null) {
            log.info("pdu null");
        }
        ResponseEvent event = snmp.send(managerConfig.getPdu(), managerConfig.getCommunityTarget(), null);
        if (event == null) {
            log.info("event is null");
        }
        return event;
    }

    private void validateAndLog(ResponseEvent event) {
        if (event != null) {
            //log.info("event received ");
            if (event.getResponse() != null) {
                List<VariableBinding> vbs = (List<VariableBinding>) event.getResponse().getVariableBindings();
                Map<String, String> map = new HashMap<String, String>();
                for (VariableBinding vb : vbs) {
                    //log.info(vb.toString());
                    map.put(vb.getOid().toDottedString(), vb.getVariable().toString());
                    //log.info("got the vb " + vb.getOid().toDottedString() + " = " + vb.getVariable().toString());
                }
                //log.info(map.toString());
                this.sourceEventListener.onEvent(map, null);
            } else {
                log.info("response pdu is null");
            }
        } else {
            log.info("event is null");
        }
    }

    public void close() {
        try {
            snmp.close();
            managerConfig.close();
        } catch (IOException e) {
            log.info(e);
        }
    }

    public void sendAndValidate(Map<String, String> map) {
        try {
            for (Map.Entry<String, String> entry: map.entrySet()) {
                managerConfig.getPdu().add(new VariableBinding(new OID(entry.getKey()), new OctetString(entry.getValue())));
            }
            managerConfig.getPdu().setType(PDU.SET);
            log.info(managerConfig.getPdu().toString());
            ResponseEvent event = snmp.set(managerConfig.getPdu(), managerConfig.getCommunityTarget());
            if (event != null) {
                log.info("event is n not null");
                if (event.getResponse() != null) {
                    log.info("response is not null");
                    log.info(event.getResponse().toString());
                }
            } else {
                throw new RuntimeException("go to hell");
            }
            if (event.getResponse() != null) {
                List<VariableBinding> vbs = (List<VariableBinding>) event.getResponse().getVariableBindings();
                log.info(vbs.toString());
            } else {
                log.info("response pdu is null");
            }
        } catch (IOException e) {
            log.info(e);
        }
    }
}
