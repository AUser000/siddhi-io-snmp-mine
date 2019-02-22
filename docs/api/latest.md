# API Docs - v1.0.0

## Sink

### snmp *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sink">(Sink)</a>*

<p style="word-wrap: break-word"> SNMP Sink allows user to make set request as a manager and make changes on agent</p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@sink(type="snmp", host="<STRING>", version="<STRING>", community="<STRING>", agent.port="<STRING>", istcp="<BOOL>", retries="<INT>", timeout="<INT>", user.name="<STRING>", security.lvl="<INT>", priv.protocol="<STRING>", priv.password="<STRING>", auth.protocol="<STRING>", auth.password="<STRING>", engine.id="<STRING>", engine.boot="<INT>", @map(...)))
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
        <td style="vertical-align: top; word-wrap: break-word">Address or ip of the target.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">version</td>
        <td style="vertical-align: top; word-wrap: break-word">Version of the snmp protocol.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">community</td>
        <td style="vertical-align: top; word-wrap: break-word">Community string of the network.</td>
        <td style="vertical-align: top">public</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">agent.port</td>
        <td style="vertical-align: top; word-wrap: break-word">Port of the agent.</td>
        <td style="vertical-align: top">161</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">istcp</td>
        <td style="vertical-align: top; word-wrap: break-word">Underline connection protocol.</td>
        <td style="vertical-align: top">false</td>
        <td style="vertical-align: top">BOOL</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">retries</td>
        <td style="vertical-align: top; word-wrap: break-word">Underline connection protocol.</td>
        <td style="vertical-align: top">5</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">timeout</td>
        <td style="vertical-align: top; word-wrap: break-word">Underline connection protocol.</td>
        <td style="vertical-align: top">1500</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">user.name</td>
        <td style="vertical-align: top; word-wrap: break-word">Username if user use snmp version 3.</td>
        <td style="vertical-align: top">noUser</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">security.lvl</td>
        <td style="vertical-align: top; word-wrap: break-word">Security level. Acceptance level AUTH_PRIV, AUTH_NO_PRIVE, NO_AUTH_NO_PRIVE.</td>
        <td style="vertical-align: top">AUTH_PRIVE</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">priv.protocol</td>
        <td style="vertical-align: top; word-wrap: break-word">Encryption protocol if use.</td>
        <td style="vertical-align: top">NO_PRIV</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">priv.password</td>
        <td style="vertical-align: top; word-wrap: break-word">Privacy protocol password.</td>
        <td style="vertical-align: top">privpass</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">auth.protocol</td>
        <td style="vertical-align: top; word-wrap: break-word">Authentication protocol if use.</td>
        <td style="vertical-align: top">NO_AUTH</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">auth.password</td>
        <td style="vertical-align: top; word-wrap: break-word">Auth protocol password.</td>
        <td style="vertical-align: top">authpass</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">engine.id</td>
        <td style="vertical-align: top; word-wrap: break-word">Local engine ID.</td>
        <td style="vertical-align: top">Empty</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">engine.boot</td>
        <td style="vertical-align: top; word-wrap: break-word">Engine boot of the snmp engine</td>
        <td style="vertical-align: top">0</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
@Sink(type='snmp',
@map(type='keyvalue', @payload('1.3.6.1.2.1.1.1.0' = 'value')),
host = '127.0.0.1',
version = 'v1',
community = 'public',
agent.port = '161',
retries = '5')
define stream outputStream(value string);

```
<p style="word-wrap: break-word">This example shows how to make set request using snmp version v1 </p>

<span id="example-2" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 2</span>
```
@Sink(type='snmp',
@map(type='keyvalue', @payload('1.3.6.1.2.1.1.1.0' = 'value')),
host = '127.0.0.1',
version = 'v2c',
community = 'public',
agent.port = '161',
retries = '5')
define stream outputStream(value string);

```
<p style="word-wrap: break-word">This example shows how to make set request using snmp version v2c </p>

<span id="example-3" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 3</span>
```
@Sink(type='snmp',
@map(type='keyvalue', @payload('1.3.6.1.2.1.1.3.0' = 'value', '1.3.6.1.2.1.1.2.0' = 'value2')),
host = '127.0.0.1',
version = 'v3',
agent.port = '161',
priv.password = 'privpass',
auth.protocol = 'AUTHMD5',
priv.protocol = 'PRIVDES',
auth.password = 'authpass',
priv.password = 'privpass',
user.name = 'agent5', 
retries = '5')
define stream outputStream(value string, value2 string);

```
<p style="word-wrap: break-word">This example shows how to make set request using snmp version v3 </p>

## Source

### snmp *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#source">(Source)</a>*

<p style="word-wrap: break-word"> SNMP Source allows user to make get request and get the response of the request, once in request interval. </p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@source(type="snmp", host="<STRING>", version="<STRING>", request.interval="<INT>", oids="<STRING>", community="<STRING>", agent.port="<STRING>", istcp="<BOOL>", retries="<INT>", timeout="<INT>", user.name="<STRING>", security.lvl="<INT>", priv.protocol="<STRING>", priv.password="<STRING>", auth.protocol="<STRING>", auth.password="<STRING>", engine.id="<STRING>", engine.boot="<INT>", @map(...)))
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
        <td style="vertical-align: top; word-wrap: break-word">Address or ip of the target.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">version</td>
        <td style="vertical-align: top; word-wrap: break-word">Version of the snmp protocol.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">request.interval</td>
        <td style="vertical-align: top; word-wrap: break-word">Request interval of the get requests.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">oids</td>
        <td style="vertical-align: top; word-wrap: break-word">list of the OIDs separated by comma.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">community</td>
        <td style="vertical-align: top; word-wrap: break-word">Community string of the network.</td>
        <td style="vertical-align: top">public</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">agent.port</td>
        <td style="vertical-align: top; word-wrap: break-word">Port of the agent.</td>
        <td style="vertical-align: top">161</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">istcp</td>
        <td style="vertical-align: top; word-wrap: break-word">Underline protocol default id UDP.</td>
        <td style="vertical-align: top">false</td>
        <td style="vertical-align: top">BOOL</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">retries</td>
        <td style="vertical-align: top; word-wrap: break-word">Number of retries of if request fails.</td>
        <td style="vertical-align: top">5</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">timeout</td>
        <td style="vertical-align: top; word-wrap: break-word">Timeout for response of the request in milliseconds, default value is 1500 of milliseconds.</td>
        <td style="vertical-align: top">1500</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">user.name</td>
        <td style="vertical-align: top; word-wrap: break-word">Username if user use snmp version 3.</td>
        <td style="vertical-align: top">noUser</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">security.lvl</td>
        <td style="vertical-align: top; word-wrap: break-word">Security level. Acceptance level AUTH_PRIV, AUTH_NO_PRIVE, NO_AUTH_NO_PRIVE.</td>
        <td style="vertical-align: top">AUTH_PRIVE</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">priv.protocol</td>
        <td style="vertical-align: top; word-wrap: break-word">Encryption protocol if use.</td>
        <td style="vertical-align: top">NO_PRIV</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">priv.password</td>
        <td style="vertical-align: top; word-wrap: break-word">Privacy protocol password.</td>
        <td style="vertical-align: top">privpass</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">auth.protocol</td>
        <td style="vertical-align: top; word-wrap: break-word">Authentication protocol if use.</td>
        <td style="vertical-align: top">NO_AUTH</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">auth.password</td>
        <td style="vertical-align: top; word-wrap: break-word">Auth protocol password.</td>
        <td style="vertical-align: top">authpass</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">engine.id</td>
        <td style="vertical-align: top; word-wrap: break-word">Local engine ID.</td>
        <td style="vertical-align: top">Empty</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">engine.boot</td>
        <td style="vertical-align: top; word-wrap: break-word">Engine boot of the snmp engine</td>
        <td style="vertical-align: top">0</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
@source(type='snmp', 
@map(type='keyvalue',    @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),
host ='127.0.0.1',
version = 'v1',
agent.port = '161',
request.interval = '60000',
oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',
community = 'public') 
 define stream inputStream(value1 string, value2 string);

```
<p style="word-wrap: break-word">This example shows how to make get request for snmp version 1 </p>

<span id="example-2" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 2</span>
```
@source(type='snmp', 
@map(type='keyvalue',    @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),
host ='127.0.0.1',
version = 'v2c',
agent.port = '161',
request.interval = '60000',
oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',
community = 'public') 
 define stream inputStream(value1 string, value2 string);

```
<p style="word-wrap: break-word">This example shows how to make get request for snmp version 2c </p>

<span id="example-3" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 3</span>
```
@source(type ='snmp', 
@map(type='keyvalue',    @attributes('value1' = '1.3.6.1.2.1.1.3.0', 'value2' = '1.3.6.1.2.1.1.1.0') ),
host ='127.0.0.1',
version = 'v3',
timeout = '1500',
request.interval = '60000',
agent.port = '161',
oids='1.3.6.1.2.1.1.3.0, 1.3.6.1.2.1.1.1.0',
auth.protocol = 'AUTHMD5',
priv.protocol = 'PRIVDES',
priv.password = 'privpass',
auth.password = 'authpass',
user.name = 'agent5') 
define stream inputStream(value1 string, value2 string);

```
<p style="word-wrap: break-word">This example shows how to make get request for snmp version 3 </p>

