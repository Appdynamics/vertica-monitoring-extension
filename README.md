# Vertica Monitoring Extension

## Use Case

The Vertica Monitoring Extension collects the stats by querying Vertica DB system tables and reports them to the AppDynamics Controller.

This extension works only with the standalone machine agent.

## Prerequisite
Vertica JDBC library is NOT in maven repo. You will have to add the vertica jdbc jar file in order to get this extension to work.
Once you have the jar file, place it in the monitors/Vertica-Monitor/ folder and update the classpath for the jar file as follows.
Open the Monitor.xml file and update the classpath:
            <classpath>vertica-monitoring-extension.jar;<Name of Vertica Jar file>.jar</classpath>

This is very essential in order to establish a connection with the Vertica DB to get the metrics.


## Installing Vertica

1. Download the HP Vertica server package.
2. Login as root
   su - root
   password: root-password
3. Use one of the following commands to run package installer:
   If you are root and installing an RPM:
   rpm -Uvh pathname

   If you are using sudo and installing an RPM:
   sudo rpm -Uvh pathname

   If you are using Debian, replace rpm -Uvh with dpkg -i
   where pathname is the HP Vertica package file you downloaded.

   For more info please visit Vertica documentation at : [Installing Using the Command Line](https://my.vertica.com/docs/7.0.x/HTML/index.htm#Authoring/InstallationGuide/InstallingVertica/DownloadAndInstallTheHPVerticaInstallPackage.htm%3FTocPath%3DInstallation%20Guide%7CInstalling%20HP%20Vertica%7CInstalling%20Using%20the%20Command%20Line%7C_____2)

   To create an example database please visit: [Installing and Connecting to the VMart Example Database](https://my.vertica.com/docs/7.0.x/HTML/index.htm#Authoring/GettingStartedGuide/InstallingAndConnectingToVMart/InstallingandConnecting.htm%3FTocPath%3DGetting%20Started%20Guide%7CInstalling%20and%20Connecting%20to%20the%20VMart%20Example%20Database%7C_____0)


## Installation
1. Run 'mvn clean install' from the vertica-monitoring-extension directory
2. Download the file Vertica-Monitor.zip found in the 'target' directory into /<machineagent install dir/>/monitors/
3. Unzip the downloaded file and cd into VerticaMonitor
4. Open the config.yml file and provide values for host, port, database, user and password. You can also configure system table details for which you want to get the stats.
5. Restart the Machine Agent.
6. In the AppDynamics controller, look for events in Custom Metrics|Vertica|


## Directory Structure

<table><tbody>
<tr>
<th align="left"> Directory/File </th>
<th align="left"> Description </th>
</tr>
<tr>
<td class='confluenceTd'> src/main/java </td>
<td class='confluenceTd'> Contains source code to Vertica Monitoring Extension  </td>
</tr>
<tr>
<td class='confluenceTd'> src/main/resources </td>
<td class='confluenceTd'> Contains monitor.xml and config.yml </td>
</tr>
<tr>
<td class='confluenceTd'> target </td>
<td class='confluenceTd'> Only obtained when using maven. Run 'maven clean install' to get the distributable .zip file </td>
</tr>
<tr>
<td class='confluenceTd'> pom.xml </td>
<td class='confluenceTd'> Maven script file (required only if changing Java code) </td>
</tr>
</tbody>
</table>

## Config files

###  config.yml


| Param | Description |
| ----- | ----- |
| driver | The Driver that will be used to connect to the database.   |
| connectionUrl | URL that will be used to connect to the database. This includes the host and port information. |
| user | user name used to connect to the database |
| password | password used to connect to the database |
| metricPrefix | Metric prefix which is shown in the controller. Default is "Custom Metrics\|Vertica\|" |


### Here is a demo config.yml file.
~~~~
#Make sure the metric prefix ends with a |
#This will create this metric in all the tiers, under this path.
#metricPrefix: "Custom Metrics|SQL|"
#This will create it in specific Tier. Replace <ComponentID> with TierID
#metricPrefix: "Server|Component:<ComponentID>|Custom Metrics|Vertica|"
metricPrefix: "Custom Metrics|Vertica|"

dbServers:
    - displayName: "Vertica"
      connectionUrl: "jdbc:vertica://192.168.57.102:5433/VMart"
      driver: "com.vertica.jdbc.Driver"

      connectionProperties:
        - user: "dbadmin"
        - password: "password"

      #Needs to be used in conjunction with `encryptionKey`. Please read the extension documentation to generate encrypted password
      #encryptedPassword: ""

      #Needs to be used in conjunction with `encryptedPassword`. Please read the extension documentation to generate encrypted password
      #encryptionKey: "welcome"

      # Replaces characters in metric name with the specified characters.
      # "replace" takes any regular expression
      # "replaceWith" takes the string to replace the matched characters

      metricCharacterReplacer:
        - replace: "%"
          replaceWith: ""
        - replace: ","
          replaceWith: "-"


      queries:
        - displayName: "Active Events"
          queryStmt: "Select NODE_NAME, EVENT_CODE, EVENT_ID, EVENT_POSTED_COUNT from Active_events"
          columns:
            - name: "NODE_NAME"
              type: "metricPathName"

            - name: "EVENT_ID"
              type: "metricPathName"

            - name: "EVENT_CODE"
              type: "metricValue"

            - name: "EVENT_POSTED_COUNT"
              type: "metricValue"

        - displayName: "Disk Storage"
          queryStmt: "Select NODE_NAME, STORAGE_USAGE, RANK, THROUGHPUT, LATENCY, DISK_BLOCK_SIZE_BYTES, DISK_SPACE_USED_BLOCKS, DISK_SPACE_USED_MB, DISK_SPACE_FREE_BLOCKS, DISK_SPACE_FREE_MB, DISK_SPACE_FREE_PERCENT from DISK_STORAGE"
          columns:
            - name: "NODE_NAME"
              type: "metricPathName"

            - name: "STORAGE_USAGE"
              type: "metricPathName"

            - name: "RANK"
              type: "metricValue"

            - name: "THROUGHPUT"
              type: "metricValue"

            - name: "LATENCY"
              type: "metricValue"

            - name: "DISK_BLOCK_SIZE_BYTES"
              type: "metricValue"

            - name: "DISK_SPACE_USED_BLOCKS"
              type: "metricValue"

            - name: "DISK_SPACE_USED_MB"
              type: "metricValue"

            - name: "DISK_SPACE_FREE_BLOCKS"
              type: "metricValue"

            - name: "DISK_SPACE_FREE_MB"
              type: "metricValue"

            - name: "DISK_SPACE_FREE_PERCENT"
              type: "metricValue"

        - displayName: "Host Resources"
          queryStmt: "Select HOST_NAME, OPEN_FILES_LIMIT, THREADS_LIMIT, CORE_FILE_LIMIT_MAX_SIZE_BYTES, PROCESSOR_COUNT, OPENED_FILE_COUNT, OPENED_SOCKET_COUNT, OPENED_NONFILE_NONSOCKET_COUNT, TOTAL_MEMORY_BYTES, TOTAL_MEMORY_FREE_BYTES,  TOTAL_BUFFER_MEMORY_BYTES, TOTAL_MEMORY_CACHE_BYTES, TOTAL_SWAP_MEMORY_BYTES, TOTAL_SWAP_MEMORY_FREE_BYTES, DISK_SPACE_FREE_MB, DISK_SPACE_USED_MB, DISK_SPACE_TOTAL_MB from HOST_RESOURCES"
          columns:
            - name: "HOST_NAME"
              type: "metricPathName"

            - name: "OPEN_FILES_LIMIT"
              type: "metricValue"

            - name: "THREADS_LIMIT"
              type: "metricValue"

            - name: "CORE_FILE_LIMIT_MAX_SIZE_BYTES"
              type: "metricValue"

            - name: "PROCESSOR_COUNT"
              type: "metricValue"

            - name: "OPENED_FILE_COUNT"
              type: "metricValue"

            - name: "OPENED_SOCKET_COUNT"
              type: "metricValue"

            - name: "OPENED_NONFILE_NONSOCKET_COUNT"
              type: "metricValue"

            - name: "TOTAL_MEMORY_BYTES"
              type: "metricValue"

            - name: "TOTAL_MEMORY_FREE_BYTES"
              type: "metricValue"

            - name: "TOTAL_BUFFER_MEMORY_BYTES"
              type: "metricValue"

            - name: "TOTAL_MEMORY_CACHE_BYTES"
              type: "metricValue"

            - name: "TOTAL_SWAP_MEMORY_BYTES"
              type: "metricValue"

            - name: "TOTAL_SWAP_MEMORY_FREE_BYTES"
              type: "metricValue"

            - name: "DISK_SPACE_FREE_MB"
              type: "metricValue"

            - name: "DISK_SPACE_USED_MB"
              type: "metricValue"

            - name: "DISK_SPACE_TOTAL_MB"
              type: "metricValue"

        - displayName: "IO Usage"
          queryStmt: "Select NODE_NAME, READ_KBYTES_PER_SEC, WRITTEN_KBYTES_PER_SEC from IO_USAGE"
          columns:
            - name: "NODE_NAME"
              type: "metricPathName"

            - name: "READ_KBYTES_PER_SEC"
              type: "metricValue"

            - name: "WRITTEN_KBYTES_PER_SEC"
              type: "metricValue"

        - displayName: "Node Status"
          queryStmt: "Select NODE_NAME, NODE_STATE from NODE_STATES"
          columns:
            - name: "NODE_NAME"
              type: "metricPathName"

            - name: "NODE_STATE"
              type: "metricValue"
              properties:
                convert:
                  "INITIALIZING" : 0
                  "UP" : 1
                  "DOWN" : 2
                  "READY" : 3
                  "UNSAFE" : 4
                  "SHUTDOWN" : 5
                  "RECOVERING" : 6

        - displayName: "Query Metrics"
          queryStmt: "Select NODE_NAME, ACTIVE_USER_SESSION_COUNT, ACTIVE_SYSTEM_SESSION_COUNT, TOTAL_USER_SESSION_COUNT, TOTAL_SYSTEM_SESSION_COUNT, TOTAL_ACTIVE_SESSION_COUNT, TOTAL_SESSION_COUNT, RUNNING_QUERY_COUNT, EXECUTED_QUERY_COUNT  from QUERY_METRICS "
          columns:
            - name: "NODE_NAME"
              type: "metricPathName"

            - name: "ACTIVE_USER_SESSION_COUNT"
              type: "metricValue"

            - name: "ACTIVE_SYSTEM_SESSION_COUNT"
              type: "metricValue"

            - name: "TOTAL_USER_SESSION_COUNT"
              type: "metricValue"

            - name: "TOTAL_SYSTEM_SESSION_COUNT"
              type: "metricValue"

            - name: "TOTAL_ACTIVE_SESSION_COUNT"
              type: "metricValue"

            - name: "TOTAL_SESSION_COUNT"
              type: "metricValue"

            - name: "RUNNING_QUERY_COUNT"
              type: "metricValue"

            - name: "EXECUTED_QUERY_COUNT"
              type: "metricValue"

        - displayName: "Resource Usage"
          queryStmt: "SELECT NODE_NAME, REQUEST_COUNT, LOCAL_REQUEST_COUNT, ACTIVE_THREAD_COUNT, OPEN_FILE_HANDLE_COUNT, MEMORY_REQUESTED_KB, ADDRESS_SPACE_REQUESTED_KB, WOS_USED_BYTES, WOS_ROW_COUNT, ROS_USED_BYTES, ROS_ROW_COUNT, TOTAL_ROW_COUNT, TOTAL_USED_BYTES, TOKENS_USED FROM RESOURCE_USAGE "
          columns:
            - name: "NODE_NAME"
              type: "metricPathName"

            - name: "REQUEST_COUNT"
              type: "metricValue"

            - name: "LOCAL_REQUEST_COUNT"
              type: "metricValue"

            - name: "ACTIVE_THREAD_COUNT"
              type: "metricValue"

            - name: "OPEN_FILE_HANDLE_COUNT"
              type: "metricValue"

            - name: "MEMORY_REQUESTED_KB"
              type: "metricValue"

            - name: "ADDRESS_SPACE_REQUESTED_KB"
              type: "metricValue"

            - name: "WOS_USED_BYTES"
              type: "metricValue"

            - name: "WOS_ROW_COUNT"
              type: "metricValue"

            - name: "ROS_USED_BYTES"
              type: "metricValue"

            - name: "ROS_ROW_COUNT"
              type: "metricValue"

            - name: "TOTAL_ROW_COUNT"
              type: "metricValue"

            - name: "TOTAL_USED_BYTES"
              type: "metricValue"

            - name: "TOKENS_USED"
              type: "metricValue"

        - displayName: "System Resource Usage"
          queryStmt: "SELECT NODE_NAME, AVERAGE_MEMORY_USAGE_PERCENT, AVERAGE_CPU_USAGE_PERCENT, NET_RX_KBYTES_PER_SECOND, NET_TX_KBYTES_PER_SECOND, IO_READ_KBYTES_PER_SECOND, IO_WRITTEN_KBYTES_PER_SECOND FROM SYSTEM_RESOURCE_USAGE"
          columns:
            - name: "NODE_NAME"
              type: "metricPathName"

            - name: "AVERAGE_MEMORY_USAGE_PERCENT"
              type: "metricValue"

            - name: "AVERAGE_CPU_USAGE_PERCENT"
              type: "metricValue"

            - name: "NET_RX_KBYTES_PER_SECOND"
              type: "metricValue"

            - name: "NET_TX_KBYTES_PER_SECOND"
              type: "metricValue"

            - name: "IO_READ_KBYTES_PER_SECOND"
              type: "metricValue"

            - name: "IO_WRITTEN_KBYTES_PER_SECOND"
              type: "metricValue"

        - displayName: "System"
          queryStmt: "SELECT CURRENT_EPOCH, AHM_EPOCH, LAST_GOOD_EPOCH, REFRESH_EPOCH, DESIGNED_FAULT_TOLERANCE, NODE_COUNT, NODE_DOWN_COUNT, CURRENT_FAULT_TOLERANCE, CATALOG_REVISION_NUMBER, WOS_USED_BYTES, WOS_ROW_COUNT, ROS_USED_BYTES, ROS_ROW_COUNT, TOTAL_USED_BYTES, TOTAL_ROW_COUNT FROM SYSTEM"
          columns:
            - name: "CURRENT_EPOCH"
              type: "metricValue"

            - name: "AHM_EPOCH"
              type: "metricValue"

            - name: "LAST_GOOD_EPOCH"
              type: "metricValue"

            - name: "REFRESH_EPOCH"
              type: "metricValue"

            - name: "DESIGNED_FAULT_TOLERANCE"
              type: "metricValue"

            - name: "NODE_COUNT"
              type: "metricValue"

            - name: "NODE_DOWN_COUNT"
              type: "metricValue"

            - name: "CURRENT_FAULT_TOLERANCE"
              type: "metricValue"

            - name: "CATALOG_REVISION_NUMBER"
              type: "metricValue"

            - name: "WOS_USED_BYTES"
              type: "metricValue"

            - name: "WOS_ROW_COUNT"
              type: "metricValue"

            - name: "ROS_USED_BYTES"
              type: "metricValue"

            - name: "ROS_ROW_COUNT"
              type: "metricValue"

            - name: "TOTAL_USED_BYTES"
              type: "metricValue"

            - name: "TOTAL_ROW_COUNT"
              type: "metricValue"


numberOfThreads: 5

~~~~

###  monitor.xml
~~~~
<monitor>
    <name>Vertica-Monitor</name>
    <type>managed</type>
    <enabled>true</enabled>
    <description>Run's queries and monitor their return values</description>
    <monitor-configuration></monitor-configuration>
    <monitor-run-task>
        <execution-style>periodic</execution-style>
        <execution-frequency-in-seconds>55</execution-frequency-in-seconds>
        <name>Vertica-Monitor Task</name>
        <display-name>Vertica-Monitor Task</display-name>
        <description>Vertica-Monitor Task</description>
        <type>java</type>
        <execution-timeout-in-seconds>120</execution-timeout-in-seconds>
        <task-arguments>
            <!-- config file-->
            <argument name="config-file" is-required="true" default-value="monitors/Vertica-Monitor/config.yml"     />
        </task-arguments>
        <java-task>


       <!--     <classpath>vertica-monitoring-extension.jar;jar-file-t0-connect-to-db.jar</classpath> -->

            <classpath>vertica-monitoring-extension.jar</classpath>

            <impl-class>com.appdynamics.extensions.sql.SQLMonitor</impl-class>
        </java-task>
    </monitor-run-task>

</monitor>

~~~~

##Metrics

Metrics are collected by querying system tables of Vertica.
For the complete list of system tables please visit [Vertica System Tables](https://my.vertica.com/docs/7.0.x/HTML/index.htm#Authoring/SQLReferenceManual/SystemTables/MONITOR/V_MONITORSchema.htm%3FTocPath%3DSQL%20Reference%20Manual%7CHP%20Vertica%20System%20Tables%7CV_MONITOR%20Schema%7C_____0)

###Active Events
Metrics related to Active Events

| Name | Description |
| ----- | ----- |
| Custom Metrics/Vertica/Active Events/{NODE_NAME}/{EVENT_ID}/event_code | A numeric ID that indicates the type of event  |
| Custom Metrics|Vertica/Active Events/{NODE_NAME}/{EVENT_ID}/event_posted_count | Tracks the number of times an event occurs  |

###Disk Storage
Metrics related to Disk Storage

| Name | Description |
| ----- | ----- |
| Custom Metrics/Vertica/Disk Storage/{NODE_NAME}/{STORAGE_USAGE}/rank | The rank assigned to the storage location based on its performance  |
| Custom Metrics/Vertica/Disk Storage/{NODE_NAME}/{STORAGE_USAGE}/throughput | The measure of a storage location's performance in MB/sec  |
| Custom Metrics/Vertica/Disk Storage/{NODE_NAME}/{STORAGE_USAGE}/latency | The measure of a storage location's performance in seeks/sec  |
| Custom Metrics/Vertica/Disk Storage/{NODE_NAME}/{STORAGE_USAGE}/disk_block_size_bytes | The block size of the disk in bytes  |
| Custom Metrics/Vertica/Disk Storage/{NODE_NAME}/{STORAGE_USAGE}/disk_space_free_blocks | The number of free disk blocks available  |
| Custom Metrics/Vertica/Disk Storage/{NODE_NAME}/{STORAGE_USAGE}/disk_space_free_mb | The number of megabytes of free storage available  |
| Custom Metrics/Vertica/Disk Storage/{NODE_NAME}/{STORAGE_USAGE}/disk_space_free_percent | The percentage of free disk space remaining  |
| Custom Metrics/Vertica/Disk Storage/{NODE_NAME}/{STORAGE_USAGE}/disk_space_used_blocks | The number of disk blocks in use  |
| Custom Metrics/Vertica/Disk Storage/{NODE_NAME}/{STORAGE_USAGE}/disk_space_used_mb | The number of megabytes of disk storage in use  |

###Host Resources
Provides a snapshot of the node

| Name | Description |
| ----- | ----- |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/ |   |

| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/core_file_limit_max_size_bytes | The maximum core file size allowed on the node  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/disk_space_free_mb | The free disk space available, in megabytes, for all storage location file systems  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/disk_space_total_mb | The total free disk space available, in megabytes, for all storage location file systems  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/disk_space_used_mb | The disk space used, in megabytes, for all storage location file systems  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/open_files_limit | The maximum number of files that can be open at one time on the node |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/opened_file_count | The total number of open files on the node  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/opened_nonfile_nonsocket_count | The total number of other file descriptions open in which 'other' could be a directory or FIFO |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/opened_socket_count | The total number of open sockets on the node  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/processor_core_count | The number of processor cores in the system  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/processor_count | The number of system processors  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/threads_limit | The maximum number of threads that can coexist on the node  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/total_buffer_memory_bytes | The amount of physical RAM, in bytes, used for file buffers on the system  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/total_memory_bytes | The total amount of physical RAM, in bytes, available on the system  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/total_memory_cache_bytes | The amount of physical RAM, in bytes, used as cache memory on the system  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/total_memory_free_bytes | The amount of physical RAM, in bytes, left unused by the system  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/total_swap_memory_bytes | The total amount of swap memory available, in bytes, on the system  |
| Custom Metrics/Vertica/Host Resources/{HOST_NAME}/total_swap_free_memory_bytes | The total amount of swap memory free, in bytes, on the system  |

###IO Usage
Provides a snapshot of the node

| Name | Description |
| ----- | ----- |
| Custom Metrics/Vertica/IO Usage/{NODE_NAME}/read_kbytes_per_sec | Counter history of the number of bytes read measured in kilobytes per second  |
| Custom Metrics/Vertica/IO Usage/{NODE_NAME}/written_kbytes_per_sec | Counter history of the number of bytes written measured in kilobytes per second  |

###Node State
Monitors node recovery state-change history on the system

| Name | Description |
| ----- | ----- |
| Custom Metrics/Vertica/Node State/{NODE_NAME}/node_state | Shows the node's state. Can be one of: INITIALIZING (0), UP (1), DOWN(2), READY (3), UNSAFE (4), SHUTDOWN (5), RECOVERING (6)  |

###Query Metrics
Monitors the sessions and queries running on each node

| Name | Description |
| ----- | ----- |
| Custom Metrics/Vertica/Query Metrics/{NODE_NAME}/active_system_session_count | The number of active system sessions |
| Custom Metrics/Vertica/Query Metrics/{NODE_NAME}/active_user_session_count | The number of active user sessions (connections) |
| Custom Metrics/Vertica/Query Metrics/{NODE_NAME}/executed_query_count | The total number of queries that ran |
| Custom Metrics/Vertica/Query Metrics/{NODE_NAME}/running_query_count | The number of queries currently running |
| Custom Metrics/Vertica/Query Metrics/{NODE_NAME}/total_active_session_count | The total number of active user and system sessions |
| Custom Metrics/Vertica/Query Metrics/{NODE_NAME}/total_session_count | The total number of user and system sessions |
| Custom Metrics/Vertica/Query Metrics/{NODE_NAME}/total_system_session_count | The number of active system sessions |
| Custom Metrics/Vertica/Query Metrics/{NODE_NAME}/total_user_session_count | The total number of user sessions |

###Resource Usage
Monitors system resource management on each node

| Name | Description |
| ----- | ----- |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/active_thread_count | The current number of active threads |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/address_space_requested_kb | The address space requested in kilobytes |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/local_request_count | The cumulative number of local requests |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/memory_requested_kb | The memory requested in kilobytes |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/open_file_handle_count | The current number of open file handles |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/request_count | The cumulative number of requests for threads, file handles, and memory (in kilobytes) |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/ros_row_count | The number of rows in the ROS |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/ros_used_bytes | The size of the ROS in bytes |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/total_row_count | The total number of rows in storage (WOS + ROS) |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/total_used_bytes | The total size of storage (WOS + ROS) in bytes |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/wos_row_count | The number of rows in the WOS |
| Custom Metrics/Vertica/Resource Usage/{NODE_NAME}/wos_used_bytes | The size of the WOS in bytes |

###System
Monitors the overall state of the database

| Name | Description |
| ----- | ----- |
| Custom Metrics/Vertica/System/current_fault_tolerance | The number of node failures the cluster can tolerate before it shuts down automatically |
| Custom Metrics/Vertica/System/designed_fault_tolerance | The designed or intended K-safety level |
| Custom Metrics/Vertica/System/node_count | The number of nodes in the cluster  |
| Custom Metrics/Vertica/System/node_down_count | The number of nodes in the cluster that are currently down |

###System Resource Usage
Provides history about system resources, such as memory, CPU, network, disk, I/O

| Name | Description |
| ----- | ----- |
| Custom Metrics/Vertica/System Resource Usage/{NODE_NAME}/average_cpu_usage_percent | Average CPU usage in percent of total CPU time (0-100) during the history interval |
| Custom Metrics/Vertica/System Resource Usage/{NODE_NAME}/average_memory_usage_percent | Average memory usage in percent of total memory (0-100) during the history interval |
| Custom Metrics/Vertica/System Resource Usage/{NODE_NAME}/io_read_kbytes_per_second | Disk I/O average number of kilobytes read from disk per second during the history interval |
| Custom Metrics/Vertica/System Resource Usage/{NODE_NAME}/io_written_kbytes_per_second | Average number of kilobytes written to disk per second during the history interval |
| Custom Metrics/Vertica/System Resource Usage/{NODE_NAME}/net_rx_kbytes_per_second | Average number of kilobytes received from network (incoming) per second during the history interval |
| Custom Metrics/Vertica/System Resource Usage/{NODE_NAME}/net_tx_kbytes_per_second | Average number of kilobytes transmitting to network (outgoing) per second during the history interval |



## Custom Dashboard ##
![](https://github.com/Appdynamics/vertica-monitoring-extension/raw/master/vertica_dashboard.png)

##Contributing

Always feel free to fork and contribute any changes directly here on GitHub

##Community

Find out more in the [AppSphere](http://community.appdynamics.com/t5/AppDynamics-eXchange/Vertica-Monitoring-Extension/idi-p/8802) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).
