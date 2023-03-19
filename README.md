# gRPC Introduction

### Problem Statement
    Monolithic applications:
		Whole application as a single unit.
		+ Fast processing as local calls
		- Too big to complex to manage.
	MicroServices:
		Split a big monolithic app into multiple sub services based on business sub domain.
		Services talk one and another.
		Exchange data in json format over http.
### Problems in REST:
	1) Request & Response protocols
		Json usage over http 1.1 protocol.
		TCP connection: 3 messages exchange required form TCP connection(3 Way handshake).
		Need to wait response back before sending the next request or new TCP connection required.
	2) Headers:
		HTTP is stateless protocol so, every request carries headers(cookies, user agent,...), body(data) in plain text format.
	3) Serialization & Deserialization 
		If client App has two send personal details in request;
		Create Person Object/json serialize data and send it over n/w then Server receives the data & deserialize the data.
		Machine understand the binary data . The Data in plain text format consumes more CPU, Memory....
	4) API Contract:
		There is no strict type/contract in json.
		Person(frame) can be mapped to Person(f_name)
		Client has to share Models dependency. or has to share openAPI but not standard.
	5) Client SDK:
		Server' service development in java, can't send library for other languages like JavaScript, Python...etc.
	6) Stubby:RPC f/w from Google, support cross-platform but tightly coupled with their infrastructure. Can process10 millions reqs per sec,
	7) gRPC: Developed by Google, inspired by stubby, adopted by netflix, microsoft. Belongs to CNCF.
	   gRPC: Remote procedure calls for inter micro service communication. Looks like method call in distributed applications.
### HTTP2 vs HTTP1.1:
	HTTP 1.1 : Introduced in 1997, baseline.
	HTTP2 : Multiplexing, Binary, Header compression, Flow Control.
	        Multiplexing: one TCP connection is enough to get multiple responses.
            Flow Control: Sender will not send too much info when receiver can't handle it.
	* gRPC uses HTTP2 protocol by default. can be used any protocol.
	* Protobuf: it is an IDL-Interface Description language for gRPC API.
	            Strict typing, DTO, Service definition, Language-agnostic,
	            Auto-Generated binding for multiple languages. 
	            Great for Mobile apps.
### gRPC bs Rest:
	REST : architecture style, resource oriented(Book,Person....), [ JSON + HTTP1.1 ]
	gRPC: A RPC f/w, more flexible & action oriented, specific to inter-service communication.

* Apache Benchmark : Performance test to generate load: Good to learn.
* Apache Activity: BPMN, Good to learn.

