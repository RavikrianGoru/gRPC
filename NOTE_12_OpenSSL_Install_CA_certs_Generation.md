### Install OpenSSL and generate CA certificate

1) Download openSSL installer: https://slproweb.com/products/Win32OpenSSL.html
   Download .exe file for windows
2) Set Env variables for OPENSSL_CONF: C:\Program Files\OpenSSL-Win64\bin\openssl.cfg
						 Path: C:\Program Files\OpenSSL-Win64\bin
3) Win+R --> cmd
```   
> openssl version
```


I) Certificate Authority(CA)
	Create CA's private key
	Create CA Certificate 
```
> cd D:\codebase\git\gRPC\ssl-tls
> openssl genrsa -des3 -out ca.key.pem 2048
```
Enter PEM pass phrase: admin
Verifying - Enter PEM pass phrase: admin

```
> openssl req -x509 -new -nodes -key ca.key.pem -sha256 -days 365 -out ca.cert.pem 
```
Enter pass phrase for ca.key.pem: admin
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
Country Name (2 letter code) [AU]:
State or Province Name (full name) [Some-State]:
Locality Name (eg, city) []:
Organization Name (eg, company) [Internet Widgits Pty Ltd]:
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []:
Email Address []:

II) Server 
    	Create server's private key
    	Certificate sign-in request
```
> openssl genrsa -out localhost.key 2048
```
Generating RSA private key, 2048 bit long modulus (2 primes)
```
> openssl req -new -key localhost.key -out localhost.csr
```
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
Country Name (2 letter code) [AU]:
State or Province Name (full name) [Some-State]:
Locality Name (eg, city) []:
Organization Name (eg, company) [Internet Widgits Pty Ltd]:
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []:localhost
Email Address []:

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:

III) Signin CA certificates
```
> openssl x509 -req -in localhost.csr -CA ca.cert.pem -CAkey ca.key.pem -CAcreateserial -out localhost.crt -days 365
```
Certificate request self-signature ok
subject=C = AU, ST = Some-State, O = Internet Widgits Pty Ltd, CN = localhost
Enter pass phrase for ca.key.pem:admin

* gRRC/java can not understand the localhost.key file. it should be converted to pem format.
```
> openssl pkcs8 -topk8 -nocrypt -in localhost.key -out localhost.pem 
```

### Become CA
```
> mkdir ssl-tls
> cd ssl-tls
> openssl genrsa -des3 -out ca.key.pem 2048
  admin
>dir
    ca.key.pem

> openssl req -x509 -new -nodes -key ca.key.pem -sha256 -days 365 -out ca.cert.pem
  admin
>dir
    ca.key.pem
    ca.cert.pem
```
### Server
```
> openssl genrsa -out localhost.key 2048
>dir
    ca.key.pem
    ca.cert.pem
    localhost.key

> openssl req -new -key localhost.key -out localhost.csr
>dir
    ca.key.pem
    ca.cert.pem
    localhost.key
    localhost.csr
```
### CA signs your request
```
> openssl x509 -req -in localhost.csr -CA ca.cert.pem -CAkey ca.key.pem -CAcreateserial -out localhost.crt -days 365
>dir
    ca.key.pem
    ca.cert.pem
    localhost.key
    localhost.csr
    ca.cert.srl
    localhost.crt
```
### gRRC/java can not understand the localhost.key file. it should be converted to pem format.
```
> openssl pkcs8 -topk8 -nocrypt -in localhost.key -out localhost.pem 
>dir
    ca.key.pem
    ca.cert.pem
    localhost.key
    localhost.csr
    ca.cert.srl
    localhost.crt
    localhost.pem
```

