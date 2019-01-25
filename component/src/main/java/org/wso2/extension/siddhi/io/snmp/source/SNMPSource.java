package org.wso2.extension.siddhi.io.snmp.source;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.wso2.extension.siddhi.io.snmp.manager.SNMPManager;
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
                @Parameter(name = SNMPConstants.TYPE,
                        description = " Type of the request. ",
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.REQUEST_INTERVAL,
                        description = " Request interval of the get requests. ",
                        type = DataType.INT),
                @Parameter(name = SNMPConstants.OIDS,
                        description = " list of the OIDs separated by comma. ",
                        dynamic = false,    // TODO -> this should consider after technical lead decision
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.COMMUNITY,
                        description = " Community string of the network. ",
                        dynamic = false,    // TODO -> dynamic thing
                        type = DataType.STRING),
                @Parameter(name = SNMPConstants.AGENT_PORT,
                        description = " Port of the agent. ",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AGENT_PORT),
                @Parameter(name = SNMPConstants.MANAGER_PORT,
                        description = " Port of the manager. ",
                        optional = true,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_MANAGER_PORT),
                @Parameter(name = SNMPConstants.IS_TCP,
                        description = " Underline protocol default id UDP ",
                        optional  = true,
                        type = DataType.BOOL,
                        defaultValue = SNMPConstants.DEFAULT_IS_TCP),
                @Parameter(name = SNMPConstants.RETRIES,
                        description = " Number of retries of if request fails. ",
                        dynamic = false,     // TODO -> dynamic thing
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_RETRIES),
                @Parameter(name  = SNMPConstants.TIMEOUT,
                        description = " Timeout for response of the request default value is 1500 of milliseconds. ",
                        dynamic = false,     // TODO -> dynamic thing
                        optional = true,
                        type = DataType.INT,
                        defaultValue = SNMPConstants.DEFAULT_TIMEOUT),
                @Parameter(name = SNMPConstants.USER_NAME,
                        description = " Username if user use snmp version 3.     ",
                        dynamic = false,     // TODO -> dynamic thing
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_USERNAME),
                @Parameter(name = SNMPConstants.USER_PASSWORD,
                        description = " User password if user use snmp vertion 3. ",
                        dynamic = false,     //TODO -> dynamic thing
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_USER_PASSWORD),
                @Parameter(name = SNMPConstants.ENC_PROTOCOL,
                        description = " Encryption protocol if use ",
                        dynamic = false,     //TODO -> dynamic thing
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_ENC_PROTOCOL),
                @Parameter(name = SNMPConstants.AUT_PROTOCOL,
                        description = " Auth protocol is use. ",
                        dynamic = false,
                        type = DataType.STRING,
                        defaultValue = SNMPConstants.DEFAULT_AUT_PROTOCOL),

        },
        examples = {
                @Example(
                        syntax = " @Source(type=’snmp’, \n" +
                                    "@map(type=’keyvalue’),\n" +
                                    "host =’127.0.0.1’,\n" +
                                    "version = ‘v2c’,\n" +
                                    "type = ‘snmp.get’,\n" +
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

    private String host;
    private String agentPort;
    private int version;
    private int type;
    private int requestInterval;
    private String community;
    private SNMPManagerConfig managerConfig;
    private SNMPManager manager;


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
    }

    private void initSnmpProperties() {
        this.host = optionHolder.validateAndGetStaticValue(SNMPConstants.HOST);
        this.agentPort = optionHolder.validateAndGetStaticValue(SNMPConstants.AGENT_PORT);
        this.version = SNMPUtils.validateVersion(optionHolder.validateAndGetStaticValue(SNMPConstants.VERSION));
        this.type = PDU.GET; // get type from user
        this.requestInterval = Integer.parseInt(optionHolder.validateAndGetStaticValue(SNMPConstants.REQUEST_INTERVAL));
        this.community = optionHolder.validateAndGetStaticValue(SNMPConstants.COMMUNITY);
        managerConfig = new SNMPManagerConfig();
        managerConfig.setOIDs(optionHolder.validateAndGetStaticValue(SNMPConstants.OIDS));
        managerConfig.setRequestInterval(requestInterval);
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
            managerConfig.setTransportMappingUDP();
            managerConfig.setCommunityTarget(host, agentPort, community, 5, 1000, SNMPConstants.V2C);
            manager = SNMPManager.getInstance();
            try {
                manager.setManagerConfig(managerConfig);
                manager.addSourceEventListener(sourceEventListener);
                manager.startInAnotherThread();
            } catch (IOException e) {
                throw new ConnectionUnavailableException(e);
            }
        } catch (IOException e) {
            throw new ConnectionUnavailableException(e);
        }

    }

    /**
     * This method can be called when it is needed to disconnect from the end point.
     */
    @Override
    public void disconnect() {
        manager.stop();
        log.info(" disconnect triggered ");
    }

    /**
     * Called at the end to clean all the resources consumed by the {@link Source}
     */
    @Override
    public void destroy() {
        manager.stop();
        log.info(" destroy triggered");
    }

    /**
     * Called to pause event consumption
     */
    @Override
    public void pause() {
        manager.pause();
    }

    /**
     * Called to resume event consumption
     */
    @Override
    public void resume() {
        manager.resume();
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

