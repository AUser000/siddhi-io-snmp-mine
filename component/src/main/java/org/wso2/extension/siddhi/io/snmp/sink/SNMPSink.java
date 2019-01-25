package org.wso2.extension.siddhi.io.snmp.sink;

import org.apache.log4j.Logger;
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

/**
 * Annotation of Siddhi Extension.
 * <pre><code>
 * eg:-
 * {@literal @}Extension(
 * name = "The name of the extension",
 * namespace = "The namespace of the extension",
 * description = "The description of the extension (optional).",
 * //Sink configurations
 * parameters = {
 * {@literal @}Parameter(name = "The name of the first parameter", type = "Supprted parameter types.
 *                              eg:{DataType.STRING,DataType.INT, DataType.LONG etc},dynamic=false ,optinal=true/false ,
 *                              if optional =true then assign default value according the type")
 *   System parameter is used to define common extension wide
 *              },
 * examples = {
 * {@literal @}Example({"Example of the first CustomExtension contain syntax and description.Here,
 *                      Syntax describe default mapping for SourceMapper and description describes
 *                      the output of according this syntax},
 *                      }
 * </code></pre>
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
                        type = DataType.STRING),
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
                        syntax = " @Sink(type='snmp',\n" +
                                "@map(type='keyvalue'),\n" +
                                "host = '127.0.0.1'\n" +
                                "version = 'v2c'\n" +
                                "community = 'public')\n" +
                                "agent.port = '161' \n" +
                                "define stream outputStream(value string, value string);",
                        description = " please fill this "
                )
        }
)

// for more information refer https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sinks

public class SNMPSink extends Sink {
    private static final Logger log = Logger.getLogger(SNMPSink.class);
    private OptionHolder optionHolder;
    private String siddhiAppName;

    private String host;
    private String agentPort;
    private int version;
    private int timeout;
    private int retries;
    private String community;
    private SNMPManagerConfig managerConfig;
    private SNMPManager manager;
    /**
     * Returns the list of classes which this sink can consume.
     * Based on the type of the sink, it may be limited to being able to publish specific type of classes.
     * For example, a sink of type file can only write objects of type String .
     * @return array of supported classes , if extension can support of any types of classes
     * then return empty array .
     */
    @Override
    public Class[] getSupportedInputEventClasses() {
            return new Class[] { Map.class };
    }

    /**
     * Returns a list of supported dynamic options (that means for each event value of the option can change) by
     * the transport
     *
     * @return the list of supported dynamic option keys
     */
    @Override
    public String[] getSupportedDynamicOptions() {
            return new String[0];
    }

    /**
     * The initialization method for {@link Sink}, will be called before other methods. It used to validate
     * all configurations and to get initial values.
     * @param streamDefinition  containing stream definition bind to the {@link Sink}
     * @param optionHolder            Option holder containing static and dynamic configuration related
     *                                to the {@link Sink}
     * @param configReader        to read the sink related system configuration.
     * @param siddhiAppContext        the context of the {@link org.wso2.siddhi.query.api.SiddhiApp} used to
     *                                get siddhi related utility functions.
     */
    @Override
    protected void init(StreamDefinition streamDefinition, OptionHolder optionHolder, ConfigReader configReader,
            SiddhiAppContext siddhiAppContext) {
        this.optionHolder = optionHolder;
        this.siddhiAppName = siddhiAppContext.getName();
        initSnmpProperties();
    }

    private void initSnmpProperties() {
        this.host = optionHolder.validateAndGetStaticValue(SNMPConstants.HOST);
        this.agentPort = optionHolder.validateAndGetStaticValue(SNMPConstants.AGENT_PORT);
        this.version = SNMPUtils.validateVersion(optionHolder.validateAndGetStaticValue(SNMPConstants.VERSION));
        this.community = optionHolder.validateAndGetStaticValue(SNMPConstants.COMMUNITY);
        this.timeout = Integer.parseInt(optionHolder
                .validateAndGetStaticValue(SNMPConstants.TIMEOUT, SNMPConstants.DEFAULT_TIMEOUT));
        this.retries = Integer.parseInt(optionHolder
                .validateAndGetStaticValue(SNMPConstants.RETRIES, SNMPConstants.DEFAULT_RETRIES));

        managerConfig = new SNMPManagerConfig();
        managerConfig.setOIDs(null);

    }

    /**
     * This method will be called when events need to be published via this sink
     * @param payload        payload of the event based on the supported event class exported by the extensions
     * @param dynamicOptions holds the dynamic options of this sink and Use this object to obtain dynamic options.
     * @throws ConnectionUnavailableException if end point is unavailable the ConnectionUnavailableException thrown
     *                                        such that the  system will take care retrying for connection
     */
    @Override
    public void publish(Object payload, DynamicOptions dynamicOptions) throws ConnectionUnavailableException {
        Map data = (Map) payload;
        log.info(payload.toString());
        log.info(data.toString());
        log.info("\n publish method started! \n");

        try {
            managerConfig.setCommunityTarget(host, agentPort, community, retries, timeout, version);
            managerConfig.setTransportMappingUDP();
        } catch (IOException e) {
            log.info(e);
        }
        manager = SNMPManager.getInstance();
        try {
            manager.setManagerConfig(managerConfig);
        } catch (IOException e) {
            log.info(e);
        }
        manager.sendAndValidate(data); //============== //TODO
        manager.close();
    }

    /**
     * This method will be called before the processing method.
     * Intention to establish connection to publish event.
     * @throws ConnectionUnavailableException if end point is unavailable the ConnectionUnavailableException thrown
     *                                        such that the  system will take care retrying for connection
     */
    @Override
    public void connect() throws ConnectionUnavailableException {
        log.info(" \n Connect method trged! \n ");

    }

    /**
     * Called after all publishing is done, or when {@link ConnectionUnavailableException} is thrown
     * Implementation of this method should contain the steps needed to disconnect from the sink.
     */
    @Override
    public void disconnect() {
        log.info(" \n Disconnect method trged! \n ");
        manager.close();
    }

    /**
     * The method can be called when removing an event receiver.
     * The cleanups that have to be done after removing the receiver could be done here.
     */
    @Override
    public void destroy() {

    }

    /**
     * Used to collect the serializable state of the processing element, that need to be
     * persisted for reconstructing the element to the same state on a different point of time
     * This is also used to identify the internal states and debugging
     * @return all internal states should be return as an map with meaning full keys
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
     *              This map will have the  same keys that is created upon calling currentState() method.
     */
    @Override
    public void restoreState(Map<String, Object> map) {

    }
}

