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
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.exception.SiddhiAppValidationException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 *  SNMP validations
 *
 */
public class SNMPValidations {
    private static final Logger log = Logger.getLogger(SNMPValidations.class);

    private int validateVersion(String versionString, String streamName) {
        versionString = versionString.toLowerCase(Locale.ENGLISH);
        if (versionString.equals("v1")) {
            return SNMPConstants.V1;
        } else if (versionString.equals("v2c")) {
            return SNMPConstants.V2C;
        } else if (versionString.equals("v3")) {
            return SNMPConstants.V3;
        }
        throw new SiddhiAppValidationException(streamName + " version validation failed. " +
                "snmp accept v1, v2c, v3 only. ");
    }

    private List<VariableBinding> validateAndGetOidList(String oidListString, String streamName) {
        List<VariableBinding> list = new LinkedList<>();
        oidListString = oidListString.replace(" ", "");
        if (oidListString.equals("")) {
            throw new SiddhiAppValidationException(streamName + "oid list empty!");
        }
        List<String> oids = Arrays.asList(oidListString.split(","));
        for (String oid : oids) {
            list.add(new VariableBinding(new OID(oid)));
        }
        return list;
    }

    private OID validateAndGetPriv(String priv, String streamName) {
        OID oid;
        priv = priv.toUpperCase(Locale.ENGLISH);
        switch (priv) {
            case "PRIVDES" :
                return PrivDES.ID;
            case "PRIVDES128" :
                return PrivAES128.ID;
            case "PRIVAES192" :
                return PrivAES192.ID;
            case "PRIVAES256" :
                return PrivAES256.ID;
            case "PRIV3DES" :
                return Priv3DES.ID;
            default :
                throw new SiddhiAppValidationException(streamName + " PRIV protocol validation failed! " +
                        "only acceptable for PRIVDES, PRIVDES128, PRIVAES192, PRIVAES256, PRIV3DES");

        }

    }

    private OID validateAndGetAuth(String auth, String streamName) {
        OID oid;
        auth = auth.toUpperCase(Locale.ENGLISH);
        switch (auth) {
            case "AUTHMD5" :
                return AuthMD5.ID;
            case "AUTHSHA" :
                return AuthSHA.ID;
            case "AUTHHMAC192SHA256" :
                return AuthHMAC192SHA256.ID;
            case "AUTHHMAC192SHA512" :
                return AuthHMAC192SHA256.ID;
            default :
                throw new SiddhiAppValidationException(streamName + " AUTH not acceptable! " +
                        "Only acceptable for AUTHMD5, AUTHSHA, AUTHHMAC192SHA256, AUTHHMAC192SHA512");

        }
    }

    private int validateSecLvl(String seclvl, String streamName) {
        seclvl = seclvl.toUpperCase(Locale.ENGLISH);
        switch (seclvl) {
            case "NOAUTH_NOPRIV": return SecurityLevel.NOAUTH_NOPRIV;
            case "AUTH_NOPRIV": return SecurityLevel.AUTH_NOPRIV;
            case "AUTH_PRIV": return SecurityLevel.AUTH_PRIV;
            default: throw new SiddhiAppValidationException(streamName + " Security level not" +
                    " acceptable. only acceptable NOAUTH_NOPRIV, AUTH_NOPRIV, AUTH_PRIV");
        }
    }

    //for validation
    public SNMPManagerConfig initSnmpProperties(OptionHolder optionHolder,
                                                       String streamName,
                                                       boolean includeOids) {
        SNMPManagerConfig managerConfig = new SNMPManagerConfig();
        String host = optionHolder.validateAndGetStaticValue(SNMPConstants.HOST);
        String port = optionHolder.validateAndGetStaticValue(SNMPConstants.AGENT_PORT);
        boolean isTcp = Boolean.parseBoolean(optionHolder.validateAndGetStaticValue(SNMPConstants.IS_TCP,
                                                                                    SNMPConstants.DEFAULT_IS_TCP));
        int timeout = Integer.parseInt(optionHolder.validateAndGetStaticValue(SNMPConstants.TIMEOUT,
                                                                                    SNMPConstants.DEFAULT_TIMEOUT));
        int retries = Integer.parseInt(optionHolder.validateAndGetStaticValue(SNMPConstants.RETRIES,
                                                                                    SNMPConstants.DEFAULT_RETRIES));
        managerConfig.isTcp(isTcp);
        managerConfig.setVersion(validateVersion(optionHolder.validateAndGetStaticValue(SNMPConstants.VERSION),
                                                                                        streamName));
        if (includeOids) {
            managerConfig.setVariablebindings(validateAndGetOidList(
                    optionHolder.validateAndGetStaticValue(SNMPConstants.OIDS),
                    streamName));
        }
        if (managerConfig.getVersion() == SNMPConstants.V3) {
            String userName = optionHolder.validateAndGetStaticValue(SNMPConstants.USER_NAME,
                                                                        SNMPConstants.DEFAULT_USERNAME);
            String authpass = optionHolder.validateAndGetStaticValue(SNMPConstants.AUTH_PASSWORD,
                                                                        SNMPConstants.DEFAULT_AUT_PASSWORD);
            String privpass = optionHolder.validateAndGetStaticValue(SNMPConstants.PRIV_PASSWORD,
                                                                        SNMPConstants.DEFAULT_PRIV_PASSWORD);
            OID priv = validateAndGetPriv(optionHolder.validateAndGetStaticValue(SNMPConstants.PRIV_PROTOCOL,
                                                                                SNMPConstants.DEFAULT_PRIV_PROTOCOL),
                                                                                streamName);
            OID auth = validateAndGetAuth(optionHolder.validateAndGetStaticValue(SNMPConstants.AUTH_PROTOCOL,
                                                                                SNMPConstants.DEFAULT_AUTH_PROTOCOL),
                                                                                streamName);
            int secLvl = validateSecLvl(optionHolder.validateAndGetStaticValue(SNMPConstants.SECURITY_LVL,
                                                                                SNMPConstants.DEFAULT_SECURITY_LVL),
                                                                                streamName);
            managerConfig.setUserMatrix(new OctetString(userName),
                                        auth,
                                        new OctetString(authpass),
                                        priv,
                                        new OctetString(privpass),
                                        secLvl);

            managerConfig.setUserTarget(host,
                                        port,
                                        retries,
                                        timeout,
                                        managerConfig.getSecLvl());
        } else {
            String community = optionHolder.validateAndGetStaticValue(SNMPConstants.COMMUNITY,
                                                                        SNMPConstants.DEFAULT_COMMUNITY);
            managerConfig.setCommunityTarget(host,
                                            port,
                                            community,
                                            retries,
                                            timeout);
        }

        return managerConfig;
    }
}
