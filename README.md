# Vertica Monitoring Extension  

##Use Case

The Vertica Monitoring Extension collects the stats by querying Vertica DB system tables and reports them to the AppDynamics Controller.

This extension works only with the standalone machine agent.

##Prerequisite
Vertica JDBC library is not in maven repo. To get it using maven we have to install the library in the local maven repo. JDBC library with version 7.0.1-0 is checked in to the lib folder. Use the below maven command to install the library to local maven repo.

mvn install:install-file -Dfile={path to JDBC library} -DgroupId=com.vertica -DartifactId=vertica-jdbc -Dversion=7.0.1-0 -Dpackaging=jar

##Installing Vertica

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


##Installation
1. Run 'mvn clean install' from the vertica-monitoring-extension directory
2. Download the file VerticaMonitor.zip found in the 'target' directory into \<machineagent install dir\>/monitors/
3. Unzip the downloaded file and cd into VerticaMonitor
4. Open the config.yml file and provide values for host, port, database, user and password. You can also configure system table details for which you want to get the stats.
5. Restart the Machine Agent.
6. In the AppDynamics controller, look for events in Custom Metrics|Vertica|


##Directory Structure

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

##XML Examples

###  config.yml


| Param | Description |
| ----- | ----- |
| host | Vertica host  |
| port | Vertica port. Default port is 5433 |
| database | Database name you want to connect to |
| user | user name |
| password | password |
| metricPrefix | Metric prefix which is shown in the controller. Default is "Custom Metrics\|Vertica\|" |
| sysTables | System table names for which metrics has to be collected |

~~~~
<monitor>
        <name>VerticaMonitor</name>
        <type>managed</type>
        <description>Vertica DB monitor</description>
        <monitor-configuration></monitor-configuration>
        <monitor-run-task>
                <execution-style>periodic</execution-style>
                <execution-frequency-in-seconds>60</execution-frequency-in-seconds>
                <name>Vertica Monitor Run Task</name>
                <display-name>Vertica Monitor Task</display-name>
                <description>Vertica Monitor Task</description>
                <type>java</type>
                <execution-timeout-in-secs>60</execution-timeout-in-secs>
                <task-arguments>
                    <argument name="config-file" is-required="true" default-value="monitors/VerticaMonitor/config.yml" />
	       </task-arguments>
                <java-task>
                    <classpath>vertica-monitoring-extension.jar</classpath>
                    <impl-class>com.appdynamics.monitors.vertica.VerticaMonitor</impl-class>
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
| Custom Metrics/Vertica/Node State/{NODE_NAME}/node_state | Shows the node's state. Can be one of: UP, READY, UNSAFE, , SHUTDOWN, RECOVERING  |

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

