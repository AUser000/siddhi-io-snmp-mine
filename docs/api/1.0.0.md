# API Docs - v1.0.0

## Sink

### snmp *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sink">(Sink)</a>*

<p style="word-wrap: break-word"> SNMP Sink allows user to set oids from the agent as a manager. It has ability to make set request and get it's response. </p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@sink(type="snmp", host="<STRING>", version="<STRING>", community="<STRING>", agent.port="<STRING>", istcp="<BOOL>", retries="<INT>", timeout="<INT>", @map(...)))
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">host</td>
        <td style="vertical-align: top; word-wrap: break-word"> Address or ip of the target. </td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">version</td>
        <td style="vertical-align: top; word-wrap: break-word"> Version of the snmp protocol. </td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">community</td>
        <td style="vertical-align: top; word-wrap: break-word"> Community string of the network. </td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">agent.port</td>
        <td style="vertical-align: top; word-wrap: break-word"> Port of the agent. </td>
        <td style="vertical-align: top">161</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">istcp</td>
        <td style="vertical-align: top; word-wrap: break-word"> Underline connection protocol. </td>
        <td style="vertical-align: top">false</td>
        <td style="vertical-align: top">BOOL</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">retries</td>
        <td style="vertical-align: top; word-wrap: break-word"> Underline connection protocol. </td>
        <td style="vertical-align: top">5</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">timeout</td>
        <td style="vertical-align: top; word-wrap: break-word"> Underline connection protocol. </td>
        <td style="vertical-align: top">1000</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
 @Sink(type='snmp',
@map(type='keyvalue'),
host = '127.0.0.1'
version = 'v2c'
community = 'public')
agent.port = '161' 
define stream outputStream(value string, value string);
```
<p style="word-wrap: break-word"> please fill this </p>

## Source

### snmp *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#source">(Source)</a>*

<p style="word-wrap: break-word"> SNMP Source allows user to get massages from the agent as a manager. It has ability to make get request and get it's responce and get trap massages. </p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@source(type="snmp", host="<STRING>", version="<STRING>", type="<STRING>", request.interval="<INT>", oids="<STRING>", community="<STRING>", agent.port="<STRING>", manager.port="<STRING>", istcp="<BOOL>", retries="<INT>", timeout="<INT>", user.name="<STRING>", user.password="<STRING>", encryption.protocol="<STRING>", authentication.protocol="<STRING>", @map(...)))
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">host</td>
        <td style="vertical-align: top; word-wrap: break-word"> Address or ip of the target. </td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">version</td>
        <td style="vertical-align: top; word-wrap: break-word"> Version of the snmp protocol. </td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">type</td>
        <td style="vertical-align: top; word-wrap: break-word"> Type of the request. </td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">request.interval</td>
        <td style="vertical-align: top; word-wrap: break-word"> Request interval of the get requests. </td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">oids</td>
        <td style="vertical-align: top; word-wrap: break-word"> list of the OIDs separated by comma. </td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">community</td>
        <td style="vertical-align: top; word-wrap: break-word"> Community string of the network. </td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">agent.port</td>
        <td style="vertical-align: top; word-wrap: break-word"> Port of the agent. </td>
        <td style="vertical-align: top">161</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">manager.port</td>
        <td style="vertical-align: top; word-wrap: break-word"> Port of the manager. </td>
        <td style="vertical-align: top">162</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">istcp</td>
        <td style="vertical-align: top; word-wrap: break-word"> Underline protocol default id UDP </td>
        <td style="vertical-align: top">false</td>
        <td style="vertical-align: top">BOOL</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">retries</td>
        <td style="vertical-align: top; word-wrap: break-word"> Number of retries of if request fails. </td>
        <td style="vertical-align: top">5</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">timeout</td>
        <td style="vertical-align: top; word-wrap: break-word"> Timeout for response of the request default value is 1500 of milliseconds. </td>
        <td style="vertical-align: top">1000</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">user.name</td>
        <td style="vertical-align: top; word-wrap: break-word"> Username if user use snmp version 3.     </td>
        <td style="vertical-align: top">user.name</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">user.password</td>
        <td style="vertical-align: top; word-wrap: break-word"> User password if user use snmp vertion 3. </td>
        <td style="vertical-align: top">user.password</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">encryption.protocol</td>
        <td style="vertical-align: top; word-wrap: break-word"> Encryption protocol if use </td>
        <td style="vertical-align: top">noEnc</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">authentication.protocol</td>
        <td style="vertical-align: top; word-wrap: break-word"> Auth protocol is use. </td>
        <td style="vertical-align: top">noAuth</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
 @Source(type=’snmp’, 
@map(type=’keyvalue’),
host =’127.0.0.1’,
version = ‘v2c’,
type = ‘snmp.get’,
request.interval = ‘20’,
oids=’1.2.3.32.323, 9878.88’,
community = ‘public’) 
define stream inputStream(oid string, value string);

```
<p style="word-wrap: break-word"> please fill this </p>

