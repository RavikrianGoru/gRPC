### Reference


- [Different google protobuf types](https://protobuf.dev/reference/protobuf/google.protobuf/)
- [TechShool github repo for grpc java](https://github.com/techschool/pcbook-java)



### The motivation of gRPC:
1. Communication
```
Backend & Frontend applications are written in different languages.
Microservices might be written in different languages.(Go,java,Python,Rust..)
They must agree on the API contract to exchange information.
	Communication Channel: REST,SOAP, Message Queue
	Authentication Machanism: Basic, OAuth, JWT
	Payload format: json,xml, binary
	Date model
	Error Handling
```
2. Communication should be efficient.
```
Huge amount of exchange messages b/w micro-services.
Mobile n/w can be slow with limited bandwidth.
```
3. Communication should be simple.
```
Client & Server should focus on their core service logic
Let the framework handle the rest
```
### gRPC
What is gRPC?
```
gRPC is a high-performance open-source feature-rich RPC framework.
gRPC is originally developed by Google
Now it is a part of the Could Native Computing Foundation-CNCF
g stands for different things in each release gRPC,good,green,glorious,game,gon....
RPC stands for Remote Procedure Calls.
```
What is RPC?
```
It is prototcol that allows a program to execute a procedure of another program located in other computer 
without the developer explicitly coding the details for th remote interaction.
```
How gRPC works?
```
Client has a generated stub that provides the same method as the server.
The stub calls gRPC framework under the hood to exchange information over n/w.
Client & servr use stubs to interact with each other, so they only need to implement their core service logic.
```
gRPC code generation with Protobuf
```
API Contract description
	The services & payload messages are defined using Protocol Buffer
```
```
syntax="proto3";
message HelloRequest
{
	string name=1;
}
message HelloResponse
{
	string gret=1;
}
service WelcomeService
{
	rpc hello(HelloRequest) returns (HelloResponse);
}
```

Why gRPC uses Protocol Buffer?
```
Human-readable Interface Definition Language(IDL)
Programing languages interoperable: code generators for many languages.
Binary data representation: smaller size, faster to transport, more efficient to serialize/deserialize.
Strongly typed contract.
API evolution: Backward & forward compatibility
Alternative options: Google flatbuffers, Microsoft bond.
```
gRPC support 10+ languages.
```
Pure implementation: Go, Java NodeJS.
C/C++, C#, Objective-C, Pythn, Ruby, Dart, PHP.
Swift, Rust, TypeScript,...
```

What makes gRPC efficient?
```
gRPC uses http/2 as its transfer protocol.
http/2: 
	binary framing: more performance, lighter to traansport, safer to decode, greate combination with protobuf.
	Header compression using HPACK: reduce overhead & improve performance.
	Multiplexing: sends multiple reqs & resp in parallel over a single TCP connection. Reduces latency & improve n/w utilization.
	Server Push: one client req, multiple resps. resuce round-trip latency.
```

How http/2 works?
```
Single TCP connection carries multiple bidirectional sreams.
Each stream has a unique ID & carries multipl bidirectional messages.
Each msg broken down into multiple binary frames.
Frame is the smallest unit that carries different types of data:  headers, settings,priority, data...etc.
Frames from different streams are interleaved and then reassembled on the other side.
```

Difference b/w http/2 and http/1.1?

| Difference    | http/2    | http/1.1 |
| ----------------- | ----------- | ------- |
| Transfer Protocol | Binary | Text |
| Headers Compressed | Plain | Text |
| Multiplexing | Yes | No |
| Reqs per Connection | Multiple | 1 |
| Server Push | Yes | No |
| Release Year | 2015 | 1997 |

### Types of gRPC:

1. Unary: like HTTP API
```
	Client sends 1 req------- Server sends 1 resp
```
2. Client Streaming
```
	Clinet sends multiple stream of messages ----- Server sends 1 resp.
```
3. Server Streaming
```
	Client sends 1 req ----Server sends stream messages back.
```
4. BiDirectional Streaming
```
	Client sends stream of messages -------- Server sends back stream of messages in parallel with arbitrary order.
```

gRPC vs REST

| Features |			gRPC |					REST |
| ------------------ | -------------------------- | ------------------------|
| Protocol |		http/2(fast) |			http/1.1(slow) |
| Payload |				Protobuf(binary,small) |	JSON(text,large) |
| APIcontract |			strict,requied(.proto) |	Loose,optional(Open API) |
| Code generation |		Built-in(protoc) |		3rd party tools(Swagger) |
| Security |			TLS/SSL |					TLS/SSL |
| Streaming |			Bidirectional |			client--> server |
| Browser support |		Limited(required gRPC-web) |	Yes |


Where gRPC is well suited to?
```
Microservices(low latency & high throughput communication), strong API contract.
Code generation put of bux for many languages
Point-to-Point realtime communication
Network constrained env(mobile apps as lightweight mgs format)
```
### How to define protocol buffer message?
```
syntax ="proto3";
message <NameOfTheMessage>
{
	<data-type> name_of_field_1 = tag_1;
	<data-type> name_of_field_2 = tag_2;

	<data-type> name_of_field_n = tag_n;
}
```
```
Name of the message : UpperCamelCase
Name of the field: lower_snake_case
Some scalar-value data types:
	string, bool, bytes
	float, double
	int32,int64,uint32,uint64,sint32,sint64...etc
Data types can be user-defined enums or other messages.
Tags are more important than field names.
	is an arbitrary integer
		From 1 to 2^29-1
		Except 19000 to 19999 are reserved
		From 1 to 15 tkes 1 byte. apply for most frequently used fields
		From 15 o 2047 takes 2 bytes
		No need to be in-order or sequetial
		Must be unique for same-level fields.
Custom data types
	Enum
	MessageNested or Not nested?
	Well-known types( google)
	Multiple proto files
		package
		import
	Repeated fields
	Oneof fields
	option 
```
