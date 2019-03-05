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
import org.wso2.extension.siddhi.io.snmp.util.SNMPValidator;
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
import org.wso2.siddhi.query.api.exception.SiddhiAppValidationException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * SNMP Source implementation
 */
@Extension(
        name = "snmp",
        namespace = "source",
        description = " SNMP Source allows user to make get request as manager and get agent status in periodically",
        parameters = {
                @Parameter(name = SNMPConstants.HOST,
                        description = "Address or ip of the target SNMP agent.",
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.VERSION,
                        description = "Version of the snmp protocol. Acceptance parameters 'V1' - version1, " +
                                "'V2C' - versionv2c, 'V3' - versionv3. ",
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.OIDS,
                        description = "list of the OIDs separated by comma. " +
                                "ex :- '1.3.6.1.2.1.1.1.0, 1.3.6.1.2.1.1.6.0' ",
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.REQUEST_INTERVAL,
                        description = "Request interval between two requests.",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_REQUEST_INTERVAL),
                @Parameter(name = SNMPConstants.COMMUNITY,
                        optional = true,
                        description = "Community string of the target SNMP agent. Default value is 'public'." +
                                " This property only uses SNMP V1, V2C and do not need to provide while using V3",
                        defaultValue = SNMPConstants.DEFAULT_COMMUNITY,
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.AGENT_PORT,
                        description = "Port number of the target SNMP agent.",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_AGENT_PORT),
                @Parameter(name = SNMPConstants.TRANSPORT_PROTOCOL,
                        description = "Transport protocol. Acceptance parameters TCP, UDP",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_TRANSPORT_PROTOCOL),
                @Parameter(name = SNMPConstants.TIMEOUT,
                        description = "Waiting time for response of a request in milliseconds.",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_TIMEOUT),
                @Parameter(name = SNMPConstants.RETRIES,
                        description = "Number of retries of when a request fails.",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_RETRIES),
                // this parameters for v3
                @Parameter(name = SNMPConstants.USER_NAME,
                        description = "User Name of the user that configured on target agent. " +
                                "This property only uses for SNMP version 3 and do not need to provide this " +
                                "when using other versions(v2c, v1).",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_USERNAME),
                @Parameter(name = SNMPConstants.SECURITY_LVL,
                        description = "Security level. Acceptance parameters AUTH_PRIV, AUTH_NO_PRIVE, " +
                                "NO_AUTH_NO_PRIVE.This property only uses for SNMP version 3 and do not need to " +
                                "provide this when using other versions(v2c, v1).",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_SECURITY_LVL),
                @Parameter(name = SNMPConstants.PRIV_PROTOCOL,
                        description = "Privacy protocol of the target SNMP agent. Acceptance parameters NO_PRIV," +
                                " PRIVDES, PRIVDES128, PRIVAES192, PRIVAES256, PRIV3DES. This property only uses for" +
                                " SNMP version 3 and do not need to provide this when using other versions(v2c, v1).",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_PRIV_PROTOCOL),

                @Parameter(name = SNMPConstants.PRIV_PASSWORD,
                        description = "Privacy protocol passphrase of the target SNMP agent." +
                                " Passphrase should have more than 8 characters. This property only uses for " +
                                "SNMP version 3 and do not need to provide this when using other versions(v2c, v1).",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_PRIV_PASSWORD),

                @Parameter(name = SNMPConstants.AUTH_PROTOCOL,
                        description = "Authentication protocol of the target SNMP agent. Can use NO_AUTH, AUTHMD5," +
                                " AUTHSHA, AUTHHMAC192SHA256, AUTHHMAC192SHA512. This property only uses for SNMP" +
                                " version 3 and do not need to provide this when using other versions(v2c, v1).",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AUTH_PROTOCOL),

                @Parameter(name = SNMPConstants.AUTH_PASSWORD,
                        description = "Authentication protocol passphrase of the target SNMP agent." +
                                "Passphrase should have more than 8 characters. This property only uses for " +
                                "SNMP version 3 and do not need to provide this when using other versions(v2c, v1).",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AUT_PASSWORD),

                @Parameter(name = SNMPConstants.LOCAL_ENGINE_ID,
                        description = "Local engine ID of the target SNMP agent. Default value is " +
                                "device-generated ID, based on the local IP address and additional four " +
                                "random bytes. This property only uses for SNMP version 3 and do not need to " +
                                "provide this when using other versions(v2c, v1).",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_LOCAL_ENGINE_ID),
                @Parameter(name = SNMPConstants.ENGINE_BOOT,
                        description = "Engine boot of the snmp engine of the target SNMP agent. " +
                                "Default value is 0. This property only uses for SNMP version 3 and do " +
                                "not need to provide this when using other versions(v2c, v1).",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_ENGINE_BOOT)
        },
        examples = {
                @Example(
                        description = "This example shows how to make get request for snmp version 1 ",

                        syntax = "@source(type='snmp', \n" +
                                "@map(type='keyvalue', " +
                                "   @attributes(" +
                                        "'value1' = '1.3.6.1.2.1.1.3.0', " +
                                        "'sysLocation' = '1.3.6.1.2.1.1.6.0') " +
                                        "),\n" +
                                "host ='127.0.0.1',\n" +
                                "version = 'v1',\n" +
                                "agent.port = '161',\n" +
                                "request.interval = '60000',\n" +
                                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.6.0',\n" +
                                "community = 'public') \n" +
                                " define stream inputStream(sysUpTime string, sysLocation string);\n"
                ),
                @Example(
                        description = "This example shows how to make get request for snmp version 2c ",

                        syntax = "@source(type='snmp', \n" +
                                "@map(type='keyvalue', " +
                                "   @attributes(" +
                                        "'sysUpTime' = '1.3.6.1.2.1.1.3.0', " +
                                        "'sysLocation' = '1.3.6.1.2.1.1.6.0') " +
                                        "),\n" +
                                "host ='127.0.0.1',\n" +
                                "version = 'v2c',\n" +
                                "agent.port = '161',\n" +
                                "request.interval = '60000',\n" +
                                "oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.6.0',\n" +
                                "community = 'public') \n" +
                                " define stream inputStream(sysUpTime string, sysLocation string);\n"
                ),
                @Example(
                        description = "This example shows how to make get request for snmp version 3 ",

                        syntax = "@source(type ='snmp', \n" +
                                "@map(type='keyvalue', " +
                                "   @attributes(" +
                                        "'sysUpTime' = '1.3.6.1.2.1.1.3.0', " +
                                        "'sysDescr' = '1.3.6.1.2.1.1.1.0') " +
                                    "),\n" +
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
                                "define stream inputStream(sysUpTime string, sysDescr string);\n"
                ),
        }
)

public class SNMPSource extends Source {

    private static final Logger LOG = Logger.getLogger(SNMPSource.class);
    private int requestInterval;
    private SNMPManager manager;
    private SourceEventListener sourceEventListener;
    private StreamDefinition streamDefinition;
    private ScheduledFuture future;
    private ScheduledExecutorService scheduledExecutorService;
    private SNMPListener listener;

    @Override
    public void init(SourceEventListener sourceEventListener, OptionHolder optionHolder,
                     String[] requestedTransportPropertyNames, ConfigReader configReader,
                     SiddhiAppContext siddhiAppContext) {

        SNMPManagerConfig managerConfig = SNMPValidator.validateAndGetManagerConfig(optionHolder,
                sourceEventListener.getStreamDefinition().getId(),
                true);
        this.sourceEventListener = sourceEventListener;
        this.manager = new SNMPManager(managerConfig);
        this.requestInterval = validateRequestInterval(optionHolder, sourceEventListener.getStreamDefinition().getId());
        this.streamDefinition = sourceEventListener.getStreamDefinition();
        scheduledExecutorService = siddhiAppContext.getScheduledExecutorService();
    }

    private int validateRequestInterval(OptionHolder optionHolder, String streamName) {
        try {
            return Integer.parseInt(optionHolder.validateAndGetStaticValue(SNMPConstants.REQUEST_INTERVAL,
                    SNMPConstants.DEFAULT_REQUEST_INTERVAL));
        } catch (Exception e) {
            throw new SiddhiAppValidationException(streamName + " Request interval accept only positive integers");
        }
    }

    @Override
    public Class[] getOutputEventClasses() {

        return new Class[]{Map.class};
    }

    @Override
    public void connect(ConnectionCallback connectionCallback) throws ConnectionUnavailableException {

        try {
            manager.listen();
        } catch (IOException e) {
            throw new ConnectionUnavailableException("Exception in starting the snmp for stream: "
                    + streamDefinition.getId(), e);
        }
        listener = new SNMPListener(manager, sourceEventListener);
        future = scheduledExecutorService.scheduleAtFixedRate(listener, 0, requestInterval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void destroy() {
        if (future != null) {
            future.cancel(true);
        }
        scheduledExecutorService.shutdown();
        manager.close();
    }

    @Override
    public void pause() {
        listener.pause();
    }

    @Override
    public void resume() {
        listener.resume();
    }

    @Override
    public Map<String, Object> currentState() {

        return null;
    }

    @Override
    public void restoreState(Map<String, Object> map) {

    }
}

