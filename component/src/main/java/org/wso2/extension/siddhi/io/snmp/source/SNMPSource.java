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
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.wso2.extension.siddhi.io.snmp.manager.SNMPGetManager;
import org.wso2.extension.siddhi.io.snmp.manager.SNMPListener;
import org.wso2.extension.siddhi.io.snmp.manager.SNMPManagerConfig;
import org.wso2.extension.siddhi.io.snmp.util.SNMPConstants;
import org.wso2.extension.siddhi.io.snmp.util.SNMPUtils;
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

import java.io.IOException;
import java.util.Map;

/**
 * This is a sample class-level comment, explaining what the extension class does.
 */

/**
 * Annotation of Siddhi Extension.
 * <pre><code>
 * eg:-
 * {@literal @}Extension(
 * name = "The name of the extension",
 * namespace = "The namespace of the extension",
 * description = "The description of the extension (optional).",
 * //Source configurations
 * parameters = {
 * {@literal @}Parameter(name = "The name of the first parameter",
 *                               description= "The description of the first parameter",
 *                               type =  "Supported parameter types.
 *                                        eg:{DataType.STRING, DataType.INT, DataType.LONG etc}",
 *                               dynamic= "false
 *                                         (if parameter doesn't depend on each event then dynamic parameter is false.
 *                                         In Source, only use static parameter)",
 *                               optional= "true/false, defaultValue= if it is optional then assign a default value
 *                                          according to the type."),
 * {@literal @}Parameter(name = "The name of the second parameter",
 *                               description= "The description of the second parameter",
 *                               type =   "Supported parameter types.
 *                                         eg:{DataType.STRING, DataType.INT, DataType.LONG etc}",
 *                               dynamic= "false
 *                                         (if parameter doesn't depend on each event then dynamic parameter is false.
 *                                         In Source, only use static parameter)",
 *                               optional= "true/false, defaultValue= if it is optional then assign a default value
 *                                         according to the type."),
 * },
 * //If Source system configurations will need then
 * systemParameters = {
 * {@literal @}SystemParameter(name = "The name of the first  system parameter",
 *                                      description="The description of the first system parameter." ,
 *                                      defaultValue = "the default value of the system parameter.",
 *                                      possibleParameter="the possible value of the system parameter.",
 *                               ),
 * },
 * examples = {
 * {@literal @}Example(syntax = "sample query with Source annotation that explain how extension use in Siddhi."
 *                              description =" The description of the given example's query."
 *                      ),
 * }
 * )
 * </code></pre>
 */

@Extension(
        name = "snmp",
        namespace = "source",
        description = " SNMP Source allows user to get massages from the agent as a manager."
                + " It has ability to make get request and get it's responce and get trap massages. ",
        parameters = {
                @Parameter(name = SNMPConstants.HOST,
                        description = " Address or ip of the target. " ,
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.VERSION,
                        description = " Version of the snmp protocol. " ,
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.REQUEST_INTERVAL,
                        description = " Request interval of the get requests. ",
                        type = DataType.INT),
                @Parameter(name = SNMPConstants.OIDS,
                        description = " list of the OIDs separated by comma. ",
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.COMMUNITY,
                        optional = true, // TODO did as optional
                        description = " Community string of the network. ",
                        defaultValue = SNMPConstants.DEFAULT_COMMUNITY,
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.AGENT_PORT,
                        description = " Port of the agent. ",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AGENT_PORT),
                @Parameter(name = SNMPConstants.IS_TCP,
                        description = " Underline protocol default id UDP ",
                        optional  = true,
                        type = DataType.BOOL,
                        defaultValue = SNMPConstants.DEFAULT_IS_TCP),
                @Parameter(name = SNMPConstants.RETRIES,
                        description = " Number of retries of if request fails. ",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_RETRIES),
                @Parameter(name  = SNMPConstants.TIMEOUT,
                        description = " Timeout for response of the request default value is 1500 of milliseconds. ",
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_TIMEOUT),
                // this parameters for v3
                @Parameter(name = SNMPConstants.USER_NAME,
                        description = " Username if user use snmp version 3.",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_USERNAME),
                @Parameter(name = SNMPConstants.SECURITY_MODE,
                        description = " Security mode. ",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_SECURITY_MODE),
                @Parameter(name = SNMPConstants.PRIV_PROTOCOL,
                        description = " Encryption protocol if use ",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_PRIV_PROTOCOL),
                @Parameter(name = SNMPConstants.PRIV_PASSWORD,
                        description = " Encryption protocol password ",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_PRIV_PASSWORD),
                @Parameter(name = SNMPConstants.AUTH_PROTOCOL,
                        description = " Auth protocol is use. ",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AUTH_PROTOCOL),
                @Parameter(name = SNMPConstants.AUTH_PASSWORD,
                        description = " Auth protocol is use. ",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AUT_PASSWORD)

        },
        examples = {
                @Example(
                        syntax = " @Source(type=’snmp’, \n" +
                                    "@map(type=’keyvalue’),\n" +
                                    "host =’127.0.0.1’,\n" +
                                    "version = ‘v2c’,\n" +
                                    "request.interval = ‘20’,\n" +
                                    "oids=’1.2.3.32.323, 9878.88’,\n" +
                                    "community = ‘public’) \n" +
                                    "define stream inputStream(oid string, value string);\n",
                        description = " please fill this "
                )
        }
)

// for more information refer https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sources
public class SNMPSource extends Source {
    private static final Logger log = Logger.getLogger(SNMPSource.class);
    private OptionHolder optionHolder;
    private SourceEventListener sourceEventListener;
    private String siddhiAppName;

    private boolean isTcp = false;
    private String host;
    private String port;
    private int requestInterval;
    private SNMPManagerConfig managerConfig;
    private SNMPGetManager manager;
    SNMPListener snmpListener;
    Thread thread;


    /**
     * The initialization method for {@link Source}, will be called before other methods. It used to validate
     * all configurations and to get initial values.
     * @param sourceEventListener After receiving events, the source should trigger onEvent() of this listener.
     *                            Listener will then pass on the events to the appropriate mappers for processing .
     * @param optionHolder        Option holder containing static configuration related to the {@link Source}
     * @param configReader        ConfigReader is used to read the {@link Source} related system configuration.
     * @param siddhiAppContext    the context of the {@link org.wso2.siddhi.query.api.SiddhiApp} used to get Siddhi
     *                            related utility functions.
     */
    @Override
    public void init(SourceEventListener sourceEventListener, OptionHolder optionHolder,
                     String[] requestedTransportPropertyNames, ConfigReader configReader,
                     SiddhiAppContext siddhiAppContext) {
        this.sourceEventListener = sourceEventListener;
        this.optionHolder = optionHolder;
        this.siddhiAppName = siddhiAppContext.getName();
        initSnmpProperties();
        manager = new SNMPGetManager();
        manager.setSourceEventListener(sourceEventListener);
    }

    private void initSnmpProperties() {
        this.host = optionHolder.validateAndGetStaticValue(SNMPConstants.HOST);
        this.port = optionHolder.validateAndGetStaticValue(SNMPConstants.AGENT_PORT);
        this.requestInterval = Integer.parseInt(
                optionHolder.validateAndGetStaticValue(SNMPConstants.REQUEST_INTERVAL));
        int timeout = Integer.parseInt(
                optionHolder.validateAndGetStaticValue(SNMPConstants.TIMEOUT, SNMPConstants.DEFAULT_TIMEOUT));

        managerConfig = new SNMPManagerConfig();
        managerConfig.setVersion(
                SNMPUtils.validateVersion(
                        optionHolder.validateAndGetStaticValue(
                                SNMPConstants.VERSION)));
        managerConfig.setVariablebindings(SNMPUtils.
                validateAndGetOidList(optionHolder.
                        validateAndGetStaticValue(SNMPConstants.OIDS)));

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
            managerConfig.setUserMatrix(new OctetString(userName),
                    auth,
                    new OctetString(authpass),
                    priv,
                    new OctetString(privpass),
                    SecurityLevel.AUTH_PRIV);
            managerConfig.setUserTarget(host, port, 5, timeout, managerConfig.getSecLvl());
        } else {
            String community = optionHolder.validateAndGetStaticValue(SNMPConstants.COMMUNITY,
                                                                    SNMPConstants.DEFAULT_COMMUNITY);
            managerConfig.setCommunityTarget(host, port, community, 5, timeout);
        }
    }

    /**
     * Returns the list of classes which this source can output.
     *
     * @return Array of classes that will be output by the source.
     * Null or empty array if it can produce any type of class.
     */
    @Override
    public Class[] getOutputEventClasses() {
        return new Class[0];
    }

    /**
     * Initially Called to connect to the end point for start retrieving the messages asynchronously .
     *
     * @param connectionCallback Callback to pass the ConnectionUnavailableException in case of connection failure after
     *                           initial successful connection. (can be used when events are receiving asynchronously)
     * @throws ConnectionUnavailableException if it cannot connect to the source backend immediately.
     */
    @Override
    public void connect(ConnectionCallback connectionCallback) throws ConnectionUnavailableException {

        try {
            if (isTcp) {
                manager.setTransportMappingTCP();
            } else {
                manager.setTransportMappingUDP();
            }
            manager.setManagerConfig(managerConfig);

            snmpListener = new SNMPListener(manager, requestInterval);
            thread = new Thread(snmpListener);
            thread.start();
        } catch (IOException e) {
            throw new ConnectionUnavailableException(e);
        }
    }

    /**
     * This method can be called when it is needed to disconnect from the end point.
     */
    @Override
    public void disconnect() {
        if (snmpListener != null) {
            snmpListener.cancel(false);
        }
        if (thread != null) {
            thread.interrupt();
        } else {
            //log.info("");
        }
    }

    /**
     * Called at the end to clean all the resources consumed by the {@link Source}
     */
    @Override
    public void destroy() {
        //log.info("[SNMPSource.class] destroy triggered");
    }

    /**
     * Called to pause event consumption
     */
    @Override
    public void pause() {
        //disconnect();
    }

    /**
     * Called to resume event consumption
     */
    @Override
    public void resume() {

    }

    /**
     * Used to collect the serializable state of the processing element, that need to be
     * persisted for the reconstructing the element to the same state on a different point of time
     *
     * @return stateful objects of the processing element as a map
     */
    @Override
    public Map<String, Object> currentState() {
        return null;
    }

    /**
     * Used to restore serialized state of the processing element, for reconstructing
     * the element to the same state as if was on a previous point of time.
     *
     * @param map the stateful objects of the processing element as a map.
     * This map will have the  same keys that is created upon calling currentState() method.
     */
     @Override
     public void restoreState(Map<String, Object> map) {

     }
}

