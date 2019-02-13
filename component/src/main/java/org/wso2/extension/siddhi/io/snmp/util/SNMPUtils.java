/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.extension.siddhi.io.snmp.util;


import org.apache.log4j.Logger;
import org.snmp4j.security.AuthHMAC192SHA256;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.wso2.extension.siddhi.io.snmp.manager.SNMPManagerConfig;
import org.wso2.siddhi.core.util.transport.OptionHolder;

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

    public static List<VariableBinding> validateAndGetOidList(String oidListString) {
        List<VariableBinding> list = new LinkedList<>();
        if (!oidListString.isEmpty() || !oidListString.equals("")) {
            if (!oidListString.equals("")) {
                List<String> oids = Arrays.asList(oidListString.replace(" ", "")
                        .split(","));
                for (String oid : oids) {
                    list.add(new VariableBinding(new OID(oid)));
                }
            } else {
                log.info(SNMPUtils.class.getName() + " oid genaration faild !");
            }

        }
        return list;
    }

    public static OID validateAndGetPriv(String priv) {
        OID oid;
        switch (priv) {
            case "PRIVDES" :
                oid = PrivDES.ID;
                break;
            case "PRIVDES128" :
                oid = PrivAES128.ID;
                break;
            case "PRIVAES192" :
                oid = PrivAES192.ID;
                break;
            case "PRIVAES256" :
                oid = PrivAES256.ID;
                break;
            case "PRIV3DES" :
                oid = Priv3DES.ID;
                break;
            default :
                oid = PrivDES.ID;
                break;

        }
        return oid;
    }

    public static OID validateAndGetAuth(String auth) {
        OID oid;
        switch (auth) {
            case "AUTHMD5" :
                oid = AuthMD5.ID;
                break;
            case "AUTHSHA" :
                oid = AuthSHA.ID;
                break;
            case "AUTHHMAC192SHA256" :
                oid = AuthHMAC192SHA256.ID;
                break;
            case "AUTHHMAC192SHA512" :
                oid = AuthHMAC192SHA256.ID;
                break;
            default : oid = AuthSHA.ID;

        }
        return oid;
    }

    public static int validateSecLvl(int lvl) {
        switch (lvl) {
            case 1: return SecurityLevel.NOAUTH_NOPRIV;
            case 2: return SecurityLevel.AUTH_NOPRIV;
            case 3: return SecurityLevel.AUTH_PRIV;
            default: return SecurityLevel.NOAUTH_NOPRIV;
        }
    }

    public static SNMPManagerConfig initSnmpProperties(OptionHolder optionHolder, boolean oids) {
        SNMPManagerConfig managerConfig;
        String host = optionHolder.validateAndGetStaticValue(SNMPConstants.HOST);
        String port = optionHolder.validateAndGetStaticValue(SNMPConstants.AGENT_PORT);

        Boolean isTcp = Boolean.parseBoolean(optionHolder.validateAndGetStaticValue(SNMPConstants.IS_TCP,
                SNMPConstants.DEFAULT_IS_TCP));
        int timeout = Integer.parseInt(
                optionHolder.validateAndGetStaticValue(SNMPConstants.TIMEOUT, SNMPConstants.DEFAULT_TIMEOUT));

        managerConfig = new SNMPManagerConfig();
        managerConfig.isTcp(isTcp);
        managerConfig.setVersion(
                SNMPUtils.validateVersion(
                        optionHolder.validateAndGetStaticValue(
                                SNMPConstants.VERSION)));
        if (oids) {
            managerConfig.setVariablebindings(SNMPUtils.
                    validateAndGetOidList(optionHolder.
                            validateAndGetStaticValue(SNMPConstants.OIDS)));
        }
        if (managerConfig.getVersion() == SNMPConstants.V3) {
            String userName = optionHolder.validateAndGetStaticValue(SNMPConstants.USER_NAME,
                    SNMPConstants.DEFAULT_USERNAME);
            String authpass = optionHolder.validateAndGetStaticValue(SNMPConstants.AUTH_PASSWORD,
                    SNMPConstants.DEFAULT_AUT_PASSWORD);
            String privpass = optionHolder.validateAndGetStaticValue(SNMPConstants.PRIV_PASSWORD,
                    SNMPConstants.DEFAULT_PRIV_PASSWORD);
            OID priv = SNMPUtils.validateAndGetPriv(optionHolder.validateAndGetStaticValue(SNMPConstants.PRIV_PROTOCOL,
                    SNMPConstants.DEFAULT_PRIV_PROTOCOL));
            OID auth = SNMPUtils.validateAndGetAuth(optionHolder.validateAndGetStaticValue(SNMPConstants.AUTH_PROTOCOL,
                    SNMPConstants.DEFAULT_AUTH_PROTOCOL));
            int secLvl = SNMPUtils.validateSecLvl(
                    Integer.parseInt(optionHolder.validateAndGetStaticValue(SNMPConstants.SECURITY_LVL,
                            SNMPConstants.DEFAULT_SECURITY_LVL)));
            managerConfig.setUserMatrix(new OctetString(userName), auth,
                    new OctetString(authpass), priv, new OctetString(privpass), secLvl);
            managerConfig.setUserTarget(host,
                    port,
                    5,
                    timeout,
                    managerConfig.getSecLvl());
        } else {
            String community = optionHolder.validateAndGetStaticValue(SNMPConstants.COMMUNITY,
                    SNMPConstants.DEFAULT_COMMUNITY);
            managerConfig.setCommunityTarget(host,
                    port,
                    community,
                    5,
                    timeout);
        }

        return managerConfig;
    }
}
