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
### Problems in REST
	1) Request & Response protocols
		Json usage over http 1.1 protocol.
		TCP connection: 3 messages exchange required form TCP connection(3 Way handshake).
		Need to wait response back before sending the next request or new TCP connection required.
	2) Headers:
		HTTP is stateless protocol so, every request carries headers(cookies, user agent,...), body(data) in plain text format.
	3) Serialization & Deserialization 
		If client App has two send Person details in request;
		Create Person Object/json serialize data and send it over n/w then Server receives the data & deserialize the data.
		Machine understand the binary data . The Data in plain text format consumes more CPU, Memory....
	4) API Contract:
		There is no strict type/contract in json.
		Person(frame) can be mapped to Person(f_name)
		Client has to share Models dependency/swagger or has to share over openAPI but it is not standard.
	5) Client SDK:
		Server's service development in java, can't send library for other languages like JavaScript, Python...etc.
	6) Stubby:RPC f/w from Google, support cross-platform but tightly coupled with their infrastructure. 
	          It can process 10 billions reqs per sec,
	7) gRPC: Developed by Google, inspired by stubby, adopted by netflix, microsoft. Belongs to CNCF.
	   gRPC: Remote procedure calls for inter micro service communication. Looks like method call in distributed applications.
### HTTP2 vs HTTP1.1
	HTTP 1.1 : Introduced in 1997, baseline.
	HTTP2 : Multiplexing, Binary, Header compression, Flow Control.
	        Multiplexing: one TCP connection is enough to get multiple responses.
            Flow Control: Sender will not send too much info when receiver can't handle it.
	* gRPC uses HTTP2 protocol by default. can be used any protocol.
	* Protobuf: it is an IDL-Interface Description language for gRPC API.
	            Strict typing, DTO, Service definition, Language-agnostic,
	            Auto-Generated binding for multiple languages. 
	            Great for Mobile apps.
### gRPC bs Rest
	REST : architecture style, resource oriented(Book,Person....), [ JSON + HTTP1.1 ]
	gRPC: A RPC f/w, more flexible & action oriented, specific to inter-service communication.

* Apache Benchmark : Performance test to generate load: Good to learn.
* Apache Activity: BPMN, Good to learn.

# Protocol Buffer

	IDL (Interface Description Language for gRPC API. Like WSDL for SOAP....etc
	Platform Neutral, Language Neutral, Serializing/Deserializing structured data, very fast/optimized for inter-service communication.
	Provides client libraries automatically for many languages(jva,C++,JavaScript,Go,Ruby,C#,Python...etc)

### Protobuf dependencies

	<dependencies>
		<!-- To generate language specific compiled Classes for message... -->
		<dependency>
			<groupId>io.grpc</groupId>
			<artifactId>grpc-protobuf</artifactId>
			<version>1.49.0</version>
		</dependency>
		
		<!-- Required if we use java 9 or above  -->
		<dependency>
			<groupId>org.apache.tomcat</groupId>
			<artifactId>annotations-api</artifactId>
			<version>6.0.53</version>
			<scope>provided</scope>
		</dependency>
		
		<!-- To handle Json mappings-->
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
			<version>2.13.3</version>
		</dependency>
	</dependencies>	

### Protobuf plugin

    <build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>1.6.2</version>
            </extension>
        </extensions>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.6.1</version>
                <configuration>
                    <protocArtifact>
                        com.google.protobuf:protoc:3.21.1:exe:${os.detected.classifier}
                    </protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>
                        io.grpc:protoc-gen-grpc-java:1.49.0:exe:${os.detected.classifier}
                    </pluginArtifact>
                    <protoSourceRoot>
                        ${basedir}/src/main/proto/
                    </protoSourceRoot>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>compile-custom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
### Project Setup ( IntelliJ ) 

- Create a maven project "gRPC" which is Parent for all modules

```
    <?xml version="1.0" encoding="UTF-8"?>
	<project xmlns="http://maven.apache.org/POM/4.0.0"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
		<modelVersion>4.0.0</modelVersion>
	
		<groupId>in.rk</groupId>
		<artifactId>gRPC</artifactId>
		<version>1.0-SNAPSHOT</version>
		<modules>
			<module>protobuf-demo</module>
		</modules>
	
		<packaging>pom</packaging>
	</project>	

```
- Create a module "protobuf-demo" in "gRPC" project. add the below dependencies & plugin.

```
	<?xml version="1.0" encoding="UTF-8"?>
	<project xmlns="http://maven.apache.org/POM/4.0.0"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
		<parent>
			<artifactId>gRPC</artifactId>
			<groupId>in.rk</groupId>
			<version>1.0-SNAPSHOT</version>
		</parent>
		<modelVersion>4.0.0</modelVersion>
		<artifactId>protobuf-demo</artifactId>
	
		<dependencies>
			<!-- To generate language specific compiled Classes for message...-->
			<dependency>
				<groupId>io.grpc</groupId>
				<artifactId>grpc-protobuf</artifactId>
				<version>1.49.0</version>
			</dependency>
	
			<!-- Required if we use java 9 or above  -->
			<dependency>
				<groupId>org.apache.tomcat</groupId>
				<artifactId>annotations-api</artifactId>
				<version>6.0.53</version>
				<scope>provided</scope>
			</dependency>
	
			<!-- To handle Json mappings-->
			<dependency>
				<groupId>com.fasterxml.jackson.core</groupId>
				<artifactId>jackson-databind</artifactId>
				<version>2.13.3</version>
			</dependency>
		</dependencies>
	
	
		<build>
			<extensions>
				<extension>
					<groupId>kr.motd.maven</groupId>
					<artifactId>os-maven-plugin</artifactId>
					<version>1.6.2</version>
				</extension>
			</extensions>
			<plugins>
				<plugin>
					<groupId>org.apache.maven.plugins</groupId>
					<artifactId>maven-compiler-plugin</artifactId>
					<configuration>
						<source>11</source>
						<target>11</target>
					</configuration>
				</plugin>
				<plugin>
					<groupId>org.xolstice.maven.plugins</groupId>
					<artifactId>protobuf-maven-plugin</artifactId>
					<version>0.6.1</version>
					<configuration>
						<protocArtifact>
							com.google.protobuf:protoc:3.21.1:exe:${os.detected.classifier}
						</protocArtifact>
						<pluginId>grpc-java</pluginId>
						<pluginArtifact>
							io.grpc:protoc-gen-grpc-java:1.49.0:exe:${os.detected.classifier}
						</pluginArtifact>
						<protoSourceRoot>
							${basedir}/src/main/proto/
						</protoSourceRoot>
					</configuration>
					<executions>
						<execution>
							<goals>
								<goal>compile</goal>
								<goal>compile-custom</goal>
							</goals>
						</execution>
					</executions>
				</plugin>
			</plugins>
		</build>
	</project>	
```
* IntelliJ :File-->Settings-->Plugins-->Search Proto--> Install Protocol Buffers

Create "proto" folder under "main" as specified in <protoSourceRoot> tag above.
	4) Create "person.proto" under proto folder.
	------
		syntax="proto3"; //indicates to use proto3 syntax.
		message Person
		{
		string name=1;
		int32 age=2;
		}
	------
	5) Do mvn clean install on "protobuf-demo" module
	   It will generate PersonOuter.class (single file for all classes) under "target/generated-sources/protobuf/java/."
	6) Update person.proto file as below.
	------
		syntax="proto3"; //indicates to use proto3 syntax.
		option java_multiple_files=true;
		option java_package="in.rk.models";
		message Person
		{
		string name=1;
		int32 age=2;
		}	
	------
	7) Do mvn clean install on "protobuf-demo" module
	   It will generates indiidual java files for classes, interfaces in specified package under "target/generated-sources/protobuf/java/."
	8) Create PersonDemo class with main(...) for Testing.
	-----
		package in.rk.protobuf;
		import in.rk.models.Person;
		
		public class PersonDemo {
			public static void main(String[] args) {
				Person p1= Person.newBuilder()
						.setName("Ravi")
						.setAge(30)
						.build();
				Person p2= Person.newBuilder()
						.setName("Ravi")
						.setAge(30)
						.build();
				System.out.println(p1.toString() +": Hash code :"+p1.hashCode());
				System.out.println(p2.toString() +":Hash Code "+p2.hashCode());
				System.out.println("Equals of p1, p2:"+p1.equals(p2));
				System.out.println("== of p1, p2:"+(p1==p2));
			}
		}
	-----
	o/p:
		name: "Ravi"
		age: 30
		: Hash code :-1515260531
		name: "Ravi"
		age: 30
		:Hash Code -1515260531
		Equals of p1, p2:true
		== of p1, p2:false
	-----
	9) Serialize and Deserialize the Person object, Updathe the PesonDemo.java
	-----
		package in.rk.protobuf;
		import in.rk.models.Person;
		import java.io.IOException;
		import java.nio.file.Files;
		import java.nio.file.Path;
		import java.nio.file.Paths;
		
		public class PersonDemo {
			public static void main(String[] args) {
				Person p1= Person.newBuilder()
						.setName("Ravi")
						.setAge(30)
						.build();
				Person p2= Person.newBuilder()
						.setName("Ravi")
						.setAge(30)
						.build();
			
			//Serialize Person
				Path path= Paths.get("person.ser");
				Files.write(path,p1.toByteArray());
			//Deserialize Person
				byte[] bytes = Files.readAllBytes(path);
				System.out.println("Deserialized Data:"+Person.parseFrom(bytes));
			}
		}
	-----
	o/p: new file is created "person.ser"
	
		Deserialized Data:name: "Ravi"
		age: 30
	-----
	10) Protobuf vs Jakson(Performance Test): We will test both Proto Person and JPerson by serializing and deserializing. 
	    Check the time taken  for both. Create JPerson.java with same field in person.proto such as name,age.
	----
		package in.rk.protobuf.models;
	
		public class JPerson 
		{
			private String name;
			private int age;
		
			public String getName() { return name; }
			public void setName(String name) { this.name = name; }
			public int getAge() { return age; }
			public void setAge(int age) { this.age = age; }
		}
	----
		Create ProtoJsonPerformaceDemo.java as below
	----
		package in.rk.protobuf;
		
		import com.fasterxml.jackson.core.JsonProcessingException;
		import com.fasterxml.jackson.databind.ObjectMapper;
		import com.google.protobuf.InvalidProtocolBufferException;
		import in.rk.models.Person;
		import in.rk.protobuf.models.JPerson;
		
		import java.io.IOException;
		
		public class ProtoJacksonPerformaceDemo {
			public static void main(String[] args) {
				//Jackson: Serialization & Deserialization
				JPerson jp1 = new JPerson();
				jp1.setName("Ravi");
				jp1.setAge(35);
		
				ObjectMapper mapper =new ObjectMapper();
				Runnable jaksonRunnable=()-> {
					try {
						//serialization
						byte[] bytes = mapper.writeValueAsBytes(jp1);
						//System.out.println(bytes.length);         //24
						//Deserialization
						JPerson jp2=mapper.readValue(bytes,JPerson.class);
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
		
				//Proto: Serialization & Deserialization
				Person p1= Person.newBuilder().setName("Ravi").setAge(35).build();
		
				Runnable protoRunnable=()->{
					try {
						//serialization
						byte[] bytes = p1.toByteArray();
						//System.out.println(bytes.length);         //8
						//Deserialization
						Person p2 = Person.parseFrom(bytes);
					} catch (InvalidProtocolBufferException e) {
						e.printStackTrace();
					}
				};
		
				//Run :PT
				for (int i=1; i<=5;i++)
				{
					runPT(jaksonRunnable,"JACKSON");
					runPT(protoRunnable,"PROTO");
				}
		
			}
		
			private static void runPT(Runnable r, String method)
			{
				long startTme=System.currentTimeMillis();
				for(int i=1;i<=1000000;i++)
				{
					r.run();
				}
				long endTime=System.currentTimeMillis();
				System.out.println("Method :"+method +" Total Time:"+(endTime-startTme));
			}
		}
	----
	o/p:
		Method :JACKSON Total Time:4694
		Method :PROTO Total Time:549
		Method :JACKSON Total Time:1517
		Method :PROTO Total Time:231
		Method :JACKSON Total Time:1241
		Method :PROTO Total Time:207
		Method :JACKSON Total Time:1206
		Method :PROTO Total Time:209
		Method :JACKSON Total Time:1215
		Method :PROTO Total Time:211
	----
	11) adding comments in .proto file: same like java 
		// inline comments
		/*
			multi-line comments
		*/
	12) Client library generation using protoc compiler through cmds.
	person.proto file is available in src/main/proto/. & protoc compiler is available in target/protoc-plugin/.
	Run the below command will get the error as below.
	RC: some issue with protoc-3.21.1 version:
		Downgrage to 3.19.0 in pom.xml then rerun.
	----
	
	$ ../../../target/protoc-plugins/protoc-3.21.1-windows-x86_64.exe --js_out=./ *.proto
	'protoc-gen-js' is not recognized as an internal or external command,
	operable program or batch file.
	--js_out: protoc-gen-js: Plugin failed with status code 1.
	----
		$ ../../../target/protoc-plugins/protoc-3.19.0-windows-x86_64.exe --js_out=./ *.proto
		$ ../../../target/protoc-plugins/protoc-3.19.0-windows-x86_64.exe --cpp_out=./ *.proto
		$ ../../../target/protoc-plugins/protoc-3.19.0-windows-x86_64.exe --kotlin_out=./ *.proto
		$ ../../../target/protoc-plugins/protoc-3.19.0-windows-x86_64.exe --objc_out=./ *.proto
		$ ../../../target/protoc-plugins/protoc-3.19.0-windows-x86_64.exe --php_out=./ *.proto
		$ ../../../target/protoc-plugins/protoc-3.19.0-windows-x86_64.exe --python_out=./ *.proto
		$ ../../../target/protoc-plugins/protoc-3.19.0-windows-x86_64.exe --ruby_out=./ *.proto
		$ ../../../target/protoc-plugins/protoc-3.19.0-windows-x86_64.exe --java_out=./ *.proto
	----
	13) Install protoc tool , set path variable then we can run >proto --js_out=./ person.proto command from src/main/proto folder.
	14) Proto/scalar types
		--------------------------------------------------
		Java Type						Proto/Scalar Type
		--------------------------------------------------
		int								int32
		long 							int64
		float							float
		double							double
		boolen							bool
		String							string
		byte[]							bytes
	15) Proto composition : Define a class as variable in another class is called composition.
		Update person.proto as below
		----
		syntax="proto3"; //indicates to use proto3 syntax.
		option java_multiple_files=true;
		option java_package="in.rk.models";
				
		message Address
		{
		int32 postbox=1;
		string stret=2;
		string city=3;
		}
		
		message Car
		{
		string make=1;
		string model=2;
		int32 year=3;
		}
		
		message Person
		{
		string name=1;
		int32 age=2;
		Address addr=3;
		Car car=4;
		}		
		----
	16) Proto Collections: use "repeated" keyword.
		----
		message Person
		{
		string name=1;
		int32 age=2;
		Address addr=3;
		repeated Car car=4;
		}		
		----
	17) Proto Map: use "map" keyword.
		----
		message Dealer
		{
		map<int32,Car> model=1;
		}
		----
	18) Proto Enum: use enum keyword.
		1) Create employee.proto in proto directory.
		----
		syntax="proto3";
		option java_multiple_files=true;
		option java_package="in.rk.models";
		
		enum EmpGrade
		{
		UNKNOWN=0;
		AVP=1;
		VP=2;
		SVP=3;
		ED=4;
		CEO=5;
		}
		message Employee
		{
		string name=1;
		EmpGrade emp_grade=2;
		}		
	    -----
		2) Create EmployeeDemo.java
		-----
		package in.rk.protobuf;

		import in.rk.models.EmpGrade;
		import in.rk.models.Employee;
		
		public class EmployeeDemo {
			public static void main(String[] args) {
				Employee e1= Employee.newBuilder()
						.setName("Raju")
						.setEmpGrade(EmpGrade.AVP)
						.build();
				System.out.println(e1);
			}
		}
		-----
		3)
		o/p:
			name: "Raju"
			emp_grade: AVP
		-----
	19) Default values:
		-----------------------------------------
		int32 / any number 			0
		bool						false
		string 						empty string
		enum						first values
		repeated 					empty list
		map							empty map
		-----------------------------------------
	
        Person p=Person.newBuilder().build();
        System.out.println("p -->:"+p);
        System.out.println("No Null pointer exception for p.getAddr().getPostbox():"+p.getAddr().getPostbox());
        System.out.println("Check addr is available in p.hasAddr():"+p.hasAddr());
		
	20) Proto Module and importing
		* Proto files can be packaged and imported.
		* package keyword
		* import keyword
		1) Create "common" package/folder under proto
		2) Create address.proto, car.proto messages into "common" package as below. move the code here as below.
		----
			syntax="proto3"; //indicates to use proto3 syntax.
			
			package common;
			
			option java_multiple_files=true;
			option java_package="in.rk.models";
			
			message Address
			{
			int32 postbox=1;
			string stret=2;
			string city=3;
			}
		----
			syntax="proto3"; //indicates to use proto3 syntax.
			
			package common;
			
			option java_multiple_files=true;
			option java_package="in.rk.models";
			
			message Car
			{
			string make=1;
			string model=2;
			int32 year=3;
			BodyStyle bodyStyle=4;
			}
			
			enum BodyStyle
			{
				UNKNOWN=0;
				COUPE=1;
				SUV=2;
				SEDAN=3;
			}
		----
			syntax="proto3";
			option java_multiple_files=true;
			option java_package="in.rk.models";
			
			import "common/address.proto";
			import "common/car.proto";
			
			message Person
			{
			string name=1;
			int32 age=2;
			common.Address addr=3;
			repeated common.Car car=4;
			}
			
			message Dealer
			{
			map<int32,common.Car> model=1;
			}
		----
		3) Create PersonCompositionDemo.java to create/set Person class object and print.
		----
			package in.rk.protobuf;
			
			import in.rk.models.Address;
			import in.rk.models.BodyStyle;
			import in.rk.models.Car;
			import in.rk.models.Person;
			
			import java.util.ArrayList;
			
			public class PersonCompositionDemo {
				public static void main(String[] args) {
			
					Address addr1= Address.newBuilder()
							.setPostbox(23)
							.setStret("Butchaiah thota")
							.setCity("Guntur")
							.build();
					//System.out.println(addr1);
					Car car1= Car.newBuilder()
							.setMake("Toyota")
							.setModel("M32")
							.setYear(1999)
							.setBodyStyle(BodyStyle.COUPE)
							.build();
					//System.out.println(car1);
					Car car2=Car.newBuilder()
							.setMake("Tomato")
							.setModel("M35")
							.setYear(2000)
							.setBodyStyle(BodyStyle.SEDAN)
							.build();
					//System.out.println(car2);
					ArrayList<Car> cars=new ArrayList<>();
					cars.add(car1);
					cars.add(car2);
			
					Person p1= Person.newBuilder()
							.setName("ravi")
							.setAge(35)
							.setAddr(addr1)
			//                .addCar(car1)
			//                .addCar(car2)
							.addAllCar(cars)
							.build();
					System.out.println(p1);
				}
			}
		-----
		4) o/p
		-----
			name: "ravi"
			age: 35
			addr {
			postbox: 23
			stret: "Butchaiah thota"
			city: "Guntur"
			}
			car {
			make: "Toyota"
			model: "M32"
			year: 1999
			bodyStyle: COUPE
			}
			car {
			make: "Tomato"
			model: "M35"
			year: 2000
			bodyStyle: SEDAN
			}
		----
		5) Create DealerMapDemo.java to test Dealer obj.
		----
			package in.rk.protobuf;
			
			import in.rk.models.BodyStyle;
			import in.rk.models.Car;
			import in.rk.models.CarOrBuilder;
			import in.rk.models.Dealer;
			
			public class DealerMapDemo {
				public static void main(String[] args) {
					Car car1= Car.newBuilder()
							.setMake("ToYoTo")
							.setModel("T001")
							.setYear(1999)
							.setBodyStyle(BodyStyle.COUPE)
							.build();
					Car car2= Car.newBuilder()
							.setMake("TAYATAo")
							.setModel("T009")
							.setYear(2000)
							.build();
			
					Dealer dealer= Dealer.newBuilder()
							.putModel(1999,car1)
							.putModel(2000,car2)
							.build();
			
					System.out.println("dealer.containsModel(2000) :"+dealer.containsModel(2000));
					System.out.println("dealer.containsModel(2001) :"+dealer.containsModel(2001));
					System.out.println("dealer.getModelCount() :"+dealer.getModelCount());
					System.out.println("dealer.getModelMap(): "+dealer.getModelMap());
					System.out.println(dealer.getModelOrThrow(2000).getBodyStyle());
			
				}
			}
		-----
		6) o/p:
		-----
			dealer.containsModel(2000) :true
			dealer.containsModel(2001) :false
			dealer.getModelCount() :2
			dealer.getModelMap(): {1999=make: "ToYoTo"
			model: "T001"
			year: 1999
			bodyStyle: COUPE
			, 2000=make: "TAYATAo"
			model: "T009"
			year: 2000
			}
			UNNOWN
		-----
	21) Proto oneof Demo
		oneof is a special case one of many(like radio button)
		
		1) Create email-login.proto, phone-login.proto under common
			----
			syntax="proto3";
	
			option java_multiple_files=true;
			option java_package="in.rk.models";
			
			package common;
			
			message EmailLogin
			{
			string email_id=1;
			string password=2;
			}
			----
			syntax="proto3";
			
			package common;
			
			option java_multiple_files=true;
			option java_package="in.rk.models";
			
			message PhoneLogin
			{
			string phone_no=1;
			string otp=2;
			}		
			----
		2) Create credentials.proto under proto
			----
			syntax="proto3";
			
			option java_multiple_files=true;
			option java_package="in.rk.models";
			
			import "common/email_login.proto";
			import "common/phone_login.proto";
			
			message Credentials
			{
			
			oneof mode
			{
				common.EmailLogin email_login=1;
				common.PhoneLogin phone_login=2;
			}
			}
			----
		3) Create OneOfDemoCredentials class to test oneof.
			----
			package in.rk.protobuf;
			
			import in.rk.models.Credentials;
			import in.rk.models.EmailLogin;
			import in.rk.models.PhoneLogin;
			
			public class OneOfDemoCredentials {
				public static void main(String[] args)
				{
					EmailLogin emailLogin= EmailLogin.newBuilder()
							.setEmailId("nobody@gmail.com")
							.setPassword("admin123")
							.build();
					PhoneLogin phoneLogin= PhoneLogin.newBuilder()
							.setPhoneNo("1234567890")
							.setOtp("556644")
							.build();
					System.out.println(emailLogin);
					System.out.println(phoneLogin);
					Credentials cred= Credentials.newBuilder()
							.setEmailLogin(emailLogin)
							.setPhoneLogin(phoneLogin)//latest will be taken
							.build();
			
					login(cred);
				}
				private static void login(Credentials cred)
				{
					System.out.println("Mode Case: "+cred.getModeCase());
					System.out.println("cred.hasEmailLogin(): "+cred.hasEmailLogin());
					System.out.println("cred.hasPhoneLogin(): "+cred.hasPhoneLogin());
					System.out.println("cred.getEmailLogin(): "+cred.getEmailLogin());
					System.out.println("cred.getPhoneLogin(): "+cred.getPhoneLogin());
					switch(cred.getModeCase())
					{
						case EMAIL_LOGIN:
							System.out.println(cred.getModeCase()+" : "+cred.getEmailLogin());
							break;
						case PHONE_LOGIN:
							System.out.println(cred.getModeCase()+" : "+cred.getPhoneLogin());
							break;
						default:
							break;
					}
				}
			}
			----
		4) o/p:
			----
			email_id: "nobody@gmail.com"
			password: "admin123"
			
			phone_no: "1234567890"
			otp: "556644"
			
			Mode Case: PHONE_LOGIN
			cred.hasEmailLogin(): false
			cred.hasPhoneLogin(): true
			cred.getEmailLogin(): 
			cred.getPhoneLogin(): phone_no: "1234567890"
			otp: "556644"
			
			PHONE_LOGIN : phone_no: "1234567890"
			otp: "556644"
			----
	22) Proto Wrapper types
		Proto has wrapper types can be imported as below in our .proto file
		1) Create student.proto as below
		----
			syntax="proto3";
			
			option java_package="com.rk.modeles";
			option  java_multiple_files=true;
			
			import "google/protobuf/wrappers.proto";
			
			message Student
			{
			  google.protobuf.StringValue name=1;
			  google.protobuf.BytesValue address=2;
			  google.protobuf.BoolValue isStudying=3;
			  google.protobuf.Int32Value rollNo=4;
			  google.protobuf.Int64Value phoneNo=5;
			  google.protobuf.FloatValue feePaid=6;
			  google.protobuf.DoubleValue pendingFee=7;
			  google.protobuf.UInt32Value rank=8;
			  google.protobuf.UInt64Value maxRank=9;
			}
		----
		2) Create StudentWrapperDemo class to test
		----
			package in.rk.protobuf;
			
			import com.google.protobuf.*;
			import com.rk.modeles.Student;
			
			import java.nio.charset.Charset;
			
			public class StudentWrapperDemo {
			    public static void main(String[] args) {
			        Student s= Student.newBuilder()
			                .setName(StringValue.newBuilder().setValue("ravi").build())
			                .setAddress(BytesValue.newBuilder().setValue(ByteString.copyFrom("Butchaiah thota, gnt", Charset.defaultCharset())).build())
			                .setIsStudying(BoolValue.newBuilder().setValue(true).build())
			                .setRollNo(Int32Value.newBuilder().setValue(1001).build())
			                .setPhoneNo(Int64Value.newBuilder().setValue(9999999999L).build())
			                .setFeePaid(FloatValue.newBuilder().setValue(50000.00f).build())
			                .setPendingFee(DoubleValue.newBuilder().setValue(20000.00).build())
			                .setRank(UInt32Value.newBuilder().setValue(1234567890).buildPartial())
			                .setMaxRank(UInt64Value.newBuilder().setValue(9999999999999l).buildPartial())
			                .build();
			
			        System.out.println(s);
			        if(s.hasName())
			            System.out.println(s.getName());
			        if(s.hasRank())
			            System.out.println(s.getRank());
			    }
			}
		----
		3) o/p:
		----
			name {
			  value: "ravi"
			}
			address {
			  value: "Butchaiah thota, gnt"
			}
			isStudying {
			  value: true
			}
			rollNo {
			  value: 1001
			}
			phoneNo {
			  value: 9999999999
			}
			feePaid {
			  value: 50000.0
			}
			pendingFee {
			  value: 20000.0
			}
			rank {
			  value: 1234567890
			}
			maxRank {
			  value: 9999999999999
			}
			
			value: "ravi"
			
			value: 1234567890
			----		
	23) Proto Filed numbers 1 to 2^29 -1
		* Each field is assigned with unique number
		* 1 is the smallest number, 2^29 -1 is the largest number.
		* 0 is the smallest for enum(Special case)
		* 1-15 for frequently used fields.
		* Do not reorder the fields once it is in use.
		* Adding and Removing fields(use reserved keyword for fields & numbers) will not break old proto.
		* Renaming is ok. but be cautious.
		* Changing int32 to int64 is OK.
				   int64 to int32 may cause issue.
		* Keep the protos as separate maven module and add then as dependencies in other modules.
		# 1-15					--> 1 byte [Apply on frequently used fields]
		# 16-2014				--> 2 bytes 
		# 19000-19999			--> Reserved for proto internal usage.
	24) Message version compatibility
		Case-1: What happens when serialized message(version) mismatche while deserializing?
		Usually, we must use the same object while serialization & deserializion.
				 but i used different classes while deserializion as no issue during test.
		1) Create television.proto files as below
		----
			syntax="proto3";
			option java_multiple_files=true;
			option java_package="in.rk.models";
			
			//v1- brand(string),size(int32),make(int32)
			message Television1
			{
			  string brand=1;
			  int32 size=2;
			  int32 make=3;
			}
			
			//v2- brand(string),size(int32),made(int32)
			//renamed make-->made
			message Television2
			{
			  string brand=1;
			  int32 size=2;
			  int32 made=3;
			}
			
			//v3- brand(string),size(int32)
			//removed size: improper way: wrong
			message Television3
			{
			  string brand=1;
			  int32 made=2;
			}
			
			//v4- brand(string),size(int32)
			//removed size: Proper way
			message Television4
			{
			  string brand=1;
			  reserved 2;
			  reserved "size";
			  int32 made=3;
			}
			
			//v5- brand(string),size(int32),make(int32),price(int32)
			//added price
			message Television5
			{
			  string brand=1;
			  int32 size=2;
			  int32 make=3;
			  int32 price=4;
			}
			
			//v6- brand(string),size(string),make(int32)
			// type change for size(int32 to string)
			message Television6
			{
			  string brand=1;
			  string size=2;
			  int32 make=3;
			}
		----		
		2) Create TelevisionVersionCompatibilityDemo class to test
		----
			package in.rk.protobuf;
			
			import in.rk.models.*;
			
			import java.io.IOException;
			import java.nio.file.Files;
			import java.nio.file.Path;
			import java.nio.file.Paths;
			
			public class TelevisionVersionCompatibilityDemo {
			    public static void main(String[] args) throws IOException {
			        Television1 t1= Television1.newBuilder()
			                .setBrand("Sony")
			                .setSize(22)
			                .setMake(2000)
			                .build();
			        //ser
			        Path path= Paths.get("t_v1.ser");
			        Files.write(path,t1.toByteArray());
			        //deser
			        byte[] bytes = Files.readAllBytes(path);
			        Television1 t11=Television1.parseFrom(bytes);
			        System.out.println("//v1- brand(string),size(int32),make(int32)");
			        System.out.println("Television1 v1:"+t11);
			
			
			        System.out.println("//v2- brand(string),size(int32),made(int32)");
			        System.out.println("//renamed make-->made");
			        Television2 t12=Television2.parseFrom(bytes);
			        System.out.println("Television2 v2:"+t12);
			
			        System.out.println("//v3- brand(string),size(int32)");
			        System.out.println("//removed size: improper way: wrong");
			        Television3 t13= Television3.parseFrom(bytes);
			        System.out.println("Television3 v3:"+t13);
			
			        System.out.println("//v4- brand(string),size(int32)");
			        System.out.println("//remove size:Proper way");
			        Television4 t14= Television4.parseFrom(bytes);
			        System.out.println("Television4 v4:"+t14);
			
			        System.out.println("//v5- brand(string),size(int32),make(int32),price(int32)");
			        System.out.println("//added price");
			        Television5 t15= Television5.parseFrom(bytes);
			        System.out.println("Television5 v5:"+t15);
			
			        System.out.println("//v6- brand(string),size(string),make(int32)");
			        System.out.println("// type change for size(int32 to string)");// incompatable types but it gives output 2:22 unable to map size.
			        Television6 t16= Television6.parseFrom(bytes);
			        System.out.println("Television6 v6:"+t16);
			
			
			    }
			}
		----
		3) o/p:
		----
			//v1- brand(string),size(int32),make(int32)
			Television1 v1:brand: "Sony"
			size: 22
			make: 2000
			
			//v2- brand(string),size(int32),made(int32)
			//renamed make-->made
			Television2 v2:brand: "Sony"
			size: 22
			made: 2000
			
			//v3- brand(string),size(int32)
			//removed size: improper way: wrong
			Television3 v3:brand: "Sony"
			made: 22
			3: 2000
			
			//v4- brand(string),size(int32)
			//remove size:Proper way
			Television4 v4:brand: "Sony"
			made: 2000
			2: 22
			
			//v5- brand(string),size(int32),make(int32),price(int32)
			//added price
			Television5 v5:brand: "Sony"
			size: 22
			make: 2000
			
			//v6- brand(string),size(string),make(int32)
			// type change for size(int32 to string)
			Television6 v6:brand: "Sony"
			make: 2000
			2: 22
		----		
	25) 	
		