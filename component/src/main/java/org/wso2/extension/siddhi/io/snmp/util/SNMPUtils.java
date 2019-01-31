package org.wso2.extension.siddhi.io.snmp.util;


import org.apache.log4j.Logger;
import org.snmp4j.security.*;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *  SNMP Utils //
 *
 */
public class SNMPUtils {
    private static final Logger log = Logger.getLogger(SNMPUtils.class);

    public static int validateVersion(String versionString) {
        if (versionString.equals("v1")) {
            return SNMPConstants.V1;
        } else if (versionString.equals("v2c")) {
            return SNMPConstants.V2C;
        } else if (versionString.equals("v3")) {
            return SNMPConstants.V3;
        }
        return SNMPConstants.V2C;
    }

//    public static int validateType(String typeString) {
//        if (typeString.toLowerCase().equals("type.get")) {
//            return SNMPConstants.GET;
//        } else if (typeString.toLowerCase().equals("type.trap")) {
//            return SNMPConstants.TRAP;
//        } else if (typeString.toLowerCase().equals("type.set")) {
//            return SNMPConstants.SET;
//        }
//        return SNMPConstants.GET;
//    }

    public static List<VariableBinding> validateAndGetOidList(String oidListString) { // TODO Fix this
        List<VariableBinding> list = new LinkedList<>();
        if (!oidListString.isEmpty() || !oidListString.equals("")) {
            if (!oidListString.equals("")) {
                List<String> oids = Arrays.asList(oidListString.replace(" ", "").split(","));
                for (String oid : oids) {
                    list.add(new VariableBinding(new OID(oid)));
                }
            } else {
                log.info(SNMPUtils.class.getName() + " oid genaration faild !");
            }

        }
        return list; // null pointer exception
    }

    public static OID validateAndGetPriv(String priv) {
        OID oid;
        //log.info(" checking priv ");
        switch (priv) {
            case "PRIVDES" :
                oid = PrivDES.ID ;
                log.info("taking priv des");
                break;
            case "PRIVDES128" :
                oid = PrivAES128.ID
                ;break;
            case "PRIVAES192" :
                oid = PrivAES192.ID
                ;break;
            case "PRIVAES256" :
                oid = PrivAES256.ID;
                break;
            case "PRIV3DES" :
                oid = Priv3DES.ID;
                break;
            default :
                oid = PrivDES.ID;
                //log.info("this is fucking default");
                break;

        }
        return oid;
    }

    public static OID validateAndGetAuth(String auth) {
        OID oid;
        log.info(" checking auth ");
        switch (auth) {
            case "AUTHMD5" : oid = AuthMD5.ID ;break;
            case "AUTHSHA" : oid = AuthSHA.ID;break;
            case "AUTHHMAC192SHA256" : oid = AuthHMAC192SHA256.ID;break;
            case "AUTHHMAC192SHA512" : oid = AuthHMAC192SHA256.ID; break;
            default : oid = AuthSHA.ID;

        }
        return oid;
    }
}
