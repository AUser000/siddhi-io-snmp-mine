package org.wso2.extension.siddhi.io.snmp.util;


import org.apache.log4j.Logger;

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

//    public static List<String> getOIDs(String oidListString) {
//        if (oidListString != null || !oidListString.equals("")) {
//            log.info("getting oids wait to print");
//            oidListString = oidListString.replace(" ", "");
//            List<String> list = Arrays.asList(oidListString.split(SNMPConstants.COMMA));
//            for (String oid: list) {
//                log.info(oid);
//            }
//            return list;
//        }
//        return null; // null pointer exception
//    }
}
