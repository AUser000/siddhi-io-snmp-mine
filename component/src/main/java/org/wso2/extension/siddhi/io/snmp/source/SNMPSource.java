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
package org.wso2.extension.siddhi.io.snmp.source;

import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.snmp.util.SNMPConstants;
import org.wso2.extension.siddhi.io.snmp.util.SNMPManager;
import org.wso2.extension.siddhi.io.snmp.util.SNMPManagerConfig;
import org.wso2.extension.siddhi.io.snmp.util.SNMPValidations;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.stream.input.source.Source;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.definition.StreamDefinition;

import java.io.IOException;
import java.util.Map;

/**
 * SNMP Source implementation
 */
@Extension(
        name = "snmp",
        namespace = "source",
        description = " SNMP Source allows user to make get request and get the response" +
                " of the request, once in request interval. ",
        parameters = {
                @Parameter(name = SNMPConstants.HOST,
                        description = "Address or ip of the target.",
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.VERSION,
                        description = "Version of the snmp protocol.",
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.REQUEST_INTERVAL,
                        description = "Request interval of the get requests.",
                        type = DataType.INT),
                @Parameter(name = SNMPConstants.OIDS,
                        description = "list of the OIDs separated by comma.",
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.COMMUNITY,
                        optional = true,
                        description = "Community string of the network.",
                        defaultValue = SNMPConstants.DEFAULT_COMMUNITY,
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.AGENT_PORT,
                        description = "Port of the agent.",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AGENT_PORT),
                @Parameter(name = SNMPConstants.IS_TCP,
                        description = "Underline protocol default id UDP.",
                        optional = true,
                        type = DataType.BOOL,
                        defaultValue = SNMPConstants.DEFAULT_IS_TCP),
                @Parameter(name = SNMPConstants.RETRIES,
                        description = "Number of retries of if request fails.",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_RETRIES),
                @Parameter(name = SNMPConstants.TIMEOUT,
                        description = "Timeout for response of the request in milliseconds," +
                                " default value is 1500 of milliseconds.",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_TIMEOUT),
                // this parameters for v3
                @Parameter(name = SNMPConstants.USER_NAME,
                        description = "Username if user use snmp version 3.",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_USERNAME),
                @Parameter(name = SNMPConstants.SECURITY_LVL,
                        description = "Security level. Acceptance level AUTH_PRIV, AUTH_NO_PRIVE, NO_AUTH_NO_PRIVE.",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_SECURITY_LVL),
                @Parameter(name = SNMPConstants.PRIV_PROTOCOL,
                        description = "Encryption protocol if use.",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_PRIV_PROTOCOL),
                @Parameter(name = SNMPConstants.PRIV_PASSWORD,
                        description = "Privacy protocol password.",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_PRIV_PASSWORD),
                @Parameter(name = SNMPConstants.AUTH_PROTOCOL,
                        description = "Authentication protocol if use.",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AUTH_PROTOCOL),
                @Parameter(name = SNMPConstants.AUTH_PASSWORD,
                        description = "Auth protocol password.",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AUT_PASSWORD),
                @Parameter(name = SNMPConstants.LOCAL_ENGINE_ID,
                        description = "Local engine ID.",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_LOCAL_ENGINE_ID),
                @Parameter(name = SNMPConstants.ENGINE_BOOT,
                        description = "Engine boot of the snmp engine",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_ENGINE_BOOT)

        },
        examples = {
                @Example(
                        description = "This example shows how to make get request for snmp version 1 ",

                        syntax = "@source(type='snmp', \n" +
                                "@map(type='keyvalue', " +
                                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                                "host ='127.0.0.1',\n" +
                                "version = 'v1',\n" +
                                "agent.port = '161',\n" +
                                "request.interval = '60000',\n" +
                                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                                "community = 'public') \n" +
                                " define stream inputStream(value1 string, value2 string);\n"
                ),
                @Example(
                        description = "This example shows how to make get request for snmp version 2c ",

                        syntax = "@source(type='snmp', \n" +
                                "@map(type='keyvalue', " +
                                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                                "host ='127.0.0.1',\n" +
                                "version = 'v2c',\n" +
                                "agent.port = '161',\n" +
                                "request.interval = '60000',\n" +
                                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                                "community = 'public') \n" +
                                " define stream inputStream(value1 string, value2 string);\n"
                ),
                @Example(
                        description = "This example shows how to make get request for snmp version 3 ",

                        syntax = "@source(type ='snmp', \n" +
                                "@map(type='keyvalue', " +
                                "   @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),\n" +
                                "host ='127.0.0.1',\n" +
                                "version = 'v3',\n" +
                                "timeout = '1500',\n" +
                                "request.interval = '60000',\n" +
                                "agent.port = '161',\n" +
                                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',\n" +
                                "auth.protocol = 'AUTHMD5',\n" +
                                "priv.protocol = 'PRIVDES',\n" +
                                "priv.password = 'privpass',\n" +
                                "auth.password = 'authpass',\n" +
                                "user.name = 'agent5') \n" +
                                "define stream inputStream(value1 string, value2 string);\n"
                ),
        }
)

public class SNMPSource extends Source {

    private static final Logger LOG = Logger.getLogger(SNMPSource.class);
    private int requestInterval;
    private SNMPManagerConfig managerConfig;
    private SNMPManager manager;
    private SNMPServer snmpServer;
    private StreamDefinition streamDefinition;

    @Override
    public void init(SourceEventListener sourceEventListener, OptionHolder optionHolder,
                     String[] requestedTransportPropertyNames, ConfigReader configReader,
                     SiddhiAppContext siddhiAppContext) {

        SNMPValidations validation = new SNMPValidations();
        this.managerConfig = validation.initSnmpProperties(optionHolder,
                sourceEventListener.getStreamDefinition().getId(),
                true);
        this.manager = new SNMPManager();
        this.manager.setSourceEventListener(sourceEventListener);
        this.requestInterval = Integer.parseInt(optionHolder.validateAndGetStaticValue(SNMPConstants.REQUEST_INTERVAL));
        this.streamDefinition = sourceEventListener.getStreamDefinition();
    }

    @Override
    public Class[] getOutputEventClasses() {

        return new Class[]{Map.class};
    }

    @Override
    public void connect(ConnectionCallback connectionCallback) throws ConnectionUnavailableException {

        try {
            manager.setManagerConfig(managerConfig);
            snmpServer = new SNMPServer();
            snmpServer.setManager(manager);
            snmpServer.setRequestInterval(requestInterval);
            snmpServer.start();
        } catch (IOException e) {
            throw new ConnectionUnavailableException(streamDefinition.getId()
                    + " Exception in starting the snmp for stream: ", e);
        }
    }

    @Override
    public void disconnect() {

        if (snmpServer != null) {
            snmpServer.stop();
        }
    }

    @Override
    public void destroy() {

        if (manager != null) {
            manager.close();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public Map<String, Object> currentState() {

        return null;
    }

    @Override
    public void restoreState(Map<String, Object> map) {

    }
}

