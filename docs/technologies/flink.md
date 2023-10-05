# Introduction to Flink

## Overview

Apache Flink is a framework for stateful computations over unbounded and bounded data streams. Flink provides multiple APIs at different levels of abstraction and offers dedicated libraries for common use cases.

## Most Important Concepts / Tools

- **Stream Processing**: Flink is a versatile processing framework that can handle any kind of stream.

  - **Bounded** and **unbounded** streams  
  - **Real-time** and **recorded** streams
  
- **Event-driven Applications**

  An event-driven application is a stateful application that ingest events from one or more event streams and reacts to incoming events by triggering computations, state updates, or external actions.

  - **State Management**: Every non-trivial streaming application is stateful. Any application that runs basic business logic needs to remember events or intermediate results to access them at a later point in time.
  - **Event Time Processing**: Applications that process streams with event-time semantics compute results based on timestamps of the events. 

- **Fault Tolerance**: Through distributed snapshot mechanisms, Flink can recover its state in case of failures and continue processing data.

- **Integration with Cluster Managers**: When a process fails, a new process is automatically started to take over its work.

- **Layered APIs**

  ![img](https://flink.apache.org/img/api-stack.png)

  - **ProcessFunction API**: For fine-grained control and advanced applications.
  - **DataStream API**: The DataStream API provides primitives for many common stream processing operations. 
  - **Table API & SQL**: Declarative API for data processing using SQL and table operations.

## Resources (Links)

- [Official Apache Flink Website](https://flink.apache.org/)
- [Apache Flink Documentation](https://ci.apache.org/projects/flink/flink-docs-release-1.13/)
- [Flink Tutorials](https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream_api_tutorial/)
