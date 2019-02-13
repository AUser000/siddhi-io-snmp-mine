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
package org.wso2.extension.siddhi.io.snmp.sink;

import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.snmp.manager.SNMPManagerConfig;
import org.wso2.extension.siddhi.io.snmp.manager.SNMPSetManager;
import org.wso2.extension.siddhi.io.snmp.util.SNMPConstants;
import org.wso2.extension.siddhi.io.snmp.util.SNMPUtils;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.stream.output.sink.Sink;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.DynamicOptions;
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.definition.StreamDefinition;

import java.io.IOException;
import java.util.Map;

/**
 * This is a sample class-level comment, explaining what the extension class does.
 */

@Extension(
        name = "snmp",
        namespace = "sink",
        description = " SNMP Sink allows user to set oids from the agent as a manager."
                + " It has ability to make set request and get it's response. ",
        parameters = {
                @Parameter(name = SNMPConstants.HOST,
                        description = " Address or ip of the target. " ,
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.VERSION,
                        description = " Version of the snmp protocol. " ,
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.COMMUNITY,
                        description = " Community string of the network. ",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_COMMUNITY),
                @Parameter(name = SNMPConstants.AGENT_PORT,
                        description = " Port of the agent. ",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AGENT_PORT),
                @Parameter(name = SNMPConstants.IS_TCP,
                        description = " Underline connection protocol. ",
                        optional = true,
                        type = DataType.BOOL,
                        defaultValue = SNMPConstants.DEFAULT_IS_TCP),
                @Parameter(name = SNMPConstants.RETRIES,
                        description = " Underline connection protocol. ",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_RETRIES),
                @Parameter(name = SNMPConstants.TIMEOUT,
                        description = " Underline connection protocol. ",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_TIMEOUT)
                },
        examples = {
                @Example(
                        description = " This example shows how to make set request using snmp " +
                                "version v1 " ,

                        syntax = "@Sink(type='snmp',\n" +
                                "@map(type='keyvalue', @payload('1.3.6.1.2.1.1.1.0' = 'value')),\n" +
                                "host = '127.0.0.1',\n" +
                                "version = 'v1',\n" +
                                "community = 'public',\n" +
                                "agent.port = '161',\n" +
                                "retries = '5')\n" +
                                "define stream outputStream(value string);\n"
                ),
                @Example(
                        description = " This example shows how to make set request using snmp " +
                                " version v2c ",

                        syntax = "@Sink(type='snmp',\n" +
                                "@map(type='keyvalue', @payload('1.3.6.1.2.1.1.1.0' = 'value')),\n" +
                                "host = '127.0.0.1',\n" +
                                "version = 'v2c',\n" +
                                "community = 'public',\n" +
                                "agent.port = '161',\n" +
                                "retries = '5')\n" +
                                "define stream outputStream(value string);\n"
                ),
                @Example(
                        description = " This example shows how to make set request using snmp " +
                                " version v3 ",

                        syntax =  "@Sink(type='snmp',\n" +
                                "@map(type='keyvalue', " +
                                "@payload('1.3.6.1.2.1.1.3.0' = 'value', '1.3.6.1.2.1.1.2.0' = 'value2')),\n" +
                                "host = '127.0.0.1',\n" +
                                "version = 'v3',\n" +
                                "agent.port = '161',\n" +
                                "priv.password = 'privpass',\n" +
                                "auth.protocol = 'AUTHMD5',\n" +
                                "priv.protocol = 'PRIVDES',\n" +
                                "auth.password = 'authpass',\n" +
                                "priv.password = 'privpass',\n" +
                                "user.name = 'agent5', \n" +
                                "retries = '5')\n" +
                                "define stream outputStream(value string, value2 string);\n"
                ),
        }
)

// for more information refer https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sinks

public class SNMPSink extends Sink {
    // TODO -> security level

    private static final Logger LOG = Logger.getLogger(SNMPSink.class);
    private boolean isTcp = false;
    private SNMPManagerConfig managerConfig;
    private SNMPSetManager manager;

    @Override
    public Class[] getSupportedInputEventClasses() {
            return new Class[] { Map.class };
    }

    @Override
    public String[] getSupportedDynamicOptions() {
        LOG.info("getSupportedDynamicOptions requested!");
        return new String[0];
    }

    @Override
    protected void init(StreamDefinition streamDefinition, OptionHolder optionHolder, ConfigReader configReader,
            SiddhiAppContext siddhiAppContext) {
        LOG.info("init !");
        managerConfig = SNMPUtils.initSnmpProperties(optionHolder, false);
        manager = new SNMPSetManager();
    }

    @Override
    public void publish(Object payload, DynamicOptions dynamicOptions) throws ConnectionUnavailableException {
        LOG.info("publish !");
        Map data = (Map) payload;
        try {
            manager.setManagerConfig(managerConfig);
        } catch (IOException e) {
            throw new ConnectionUnavailableException(" Error in Connecting to agent : " + e);
        }

        try {
            //ToDO -> validation part
            //manager.validateResponseAndNotify(manager.send(data));
            manager.send(data);
        } catch (IOException e) {
            throw new ConnectionUnavailableException(" e " + e.toString());
        }
        manager.close();
    }

    @Override
    public void connect() throws ConnectionUnavailableException {
        LOG.info("connect !");
    }

    @Override
    public void disconnect() {
        LOG.info("disconnect! ");
    }

    @Override
    public void destroy() {
        LOG.info("destroy! ");
    }

    @Override
    public Map<String, Object> currentState() {
            return null;
    }

    @Override
    public void restoreState(Map<String, Object> map) {

    }
}

