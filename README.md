# XHub4J [![Build Status](https://travis-ci.org/McFoggy/xhub4j.svg?branch=master)](https://travis-ci.org/McFoggy/xhub4j)

XHub4J is a set of utilities to allow to secured webhooks either as a sender or as a consumer.

## maven coordinates

```
<dependency>
    <groupId>fr.brouillard.oss.security.xhub</groupId>
    <artifactId>xhub4j-core</artifactId>
    <version>X.Y.Z</version>
</dependency>
<dependency>
    <groupId>fr.brouillard.oss.security.xhub</groupId>
    <artifactId>xhub4j-servlet</artifactId>
    <version>X.Y.Z</version>
</dependency>
<dependency>
    <groupId>fr.brouillard.oss.security.xhub</groupId>
    <artifactId>xhub4j-jaxrs-client</artifactId>
    <version>X.Y.Z</version>
</dependency>
```

## xhub4j-core

Can be used standalone to create `X-Hub-Signature` HTTP headers.

For example to create a `X-Hub-Signature` exactly like github does:

```
String token = "key";
String data = "The quick brown fox jumps over the lazy dog";
String expectedResult = "sha1=de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9";

String generatedHeader = XHub.generateHeaderXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA1, token, data.getBytes());
assertThat(generatedHeader, is(expectedResult));
```

## xhub4j-servlet

Provides a servlet Filter to secure a webhook consumer.

Example to filter a webhook sent by github:

```
<filter>
    <filter-name>Github-XHubFilter</filter-name>
    <filter-class>fr.brouillard.oss.security.xhub.servlet.XHubFilter</filter-class>
    <init-param>
        <param-name>x-hub-token</param-name>
        <param-value>This is the secret key value</param-value>
    </init-param>
    <init-param>
        <param-name>x-hub-converter</param-name>
        <param-value>HEXA_LOWERCASE</param-value>       <!-- convert the data as an hexadecimal string with lowercase letters  -->
    </init-param>
</filter>
```

### Filter parameters

- `x-hub-token`: the token to be used as encryption key by the webhook sender
- `x-hub-converter`: the encoder to use, see [XHub.XHubConverter](https://github.com/McFoggy/xhub4j/blob/master/xhub4j-core/src/main/java/fr/brouillard/oss/security/xhub/XHub.java#L47)
  - `BASE64`: encodes the data as a Base64 string
  - `HEXA`: encodes the data as an hexadecimal string
  - `BASE64_LOWERCASE`: encodes the data as a Base64 string with lowercase letters
  - `HEXA_LOWERCASE` _(default)_: encodes the data as an hexadecimal string with lowercase letters (encoder used in github webhooks)
- `x-hub-header`: the name of the http header containing the XHub token, defaults to `X-Hub-Signature`
 
## xhub4j-jaxrs-client

In this module we provide JAX-RS components that can be registered on `javax.ws.rs.client.ClientBuilder` to automatically include a XHub header to an HTTP post fire using a `WebTarget` produced via this builder.

> TODO add wiki page to comment why 2 impl are needed

> TODO send post to resteasy & jersey for each of their problem

> TODO send post to jaxrs JSR group for expected behavior

### XHubClientRequestFilter

Simple JAXRS component defined as a `ClientRequestFilter`.

**Usage**:

As defined in [`WithClientRequestFilterWebhookSender`](https://github.com/McFoggy/xhub4j/blob/master/xhub4j-jaxrs-client/src/test/java/fr/brouillard/oss/security/xhub/ws/rest/ext/WithClientRequestFilterWebhookSender.java), the following code imitating the send action to a webhook

```
XHubClientRequestFilter clientFilter = new XHubClientRequestFilter();
clientFilter.setToken("this is the secret key");

Client c = ClientBuilder
        .newBuilder()
        .register(clientFilter)
        .build();
WebTarget webhookTarget = c.target("http://registered-host/webhook");

JsonObject payload = calculateJSONPayload();
Response r = webhookTarget.request().post(Entity.json(payload));
```

Produces on the server:

```
23:02:49,199 INFO  [f.b.o.s.x.w.LogWebHook] (default task-4) Headers:
23:02:49,200 INFO  [f.b.o.s.x.w.LogWebHook] (default task-4) 	X-Hub-Signature:sha1=fa0902542a12dbf43ef081c0cc4794fd438e172d
23:02:49,200 INFO  [f.b.o.s.x.w.LogWebHook] (default task-4) 	Accept:text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
23:02:49,200 INFO  [f.b.o.s.x.w.LogWebHook] (default task-4) 	Connection:keep-alive
23:02:49,200 INFO  [f.b.o.s.x.w.LogWebHook] (default task-4) 	User-Agent:Jersey/2.22.1 (HttpUrlConnection 1.8.0_40)
23:02:49,200 INFO  [f.b.o.s.x.w.LogWebHook] (default task-4) 	Host:localhost:8180
23:02:49,200 INFO  [f.b.o.s.x.w.LogWebHook] (default task-4) 	Content-Length:51
23:02:49,200 INFO  [f.b.o.s.x.w.LogWebHook] (default task-4) 	Content-Type:application/json
23:02:49,201 INFO  [f.b.o.s.x.w.LogWebHook] (default task-4) Body:
23:02:49,201 INFO  [f.b.o.s.x.w.LogWebHook] (default task-4) {"name":"Matthieu Brouillard","likes":"coding !!!"}
```

Profiles compliancy (when you use th predefined profile to launch tests):

- `jersey`: works
- `resteasy`: doesn't work


### XHubWriterInterceptor

Simple JAXRS component defined as a `WriterInterceptor`.

**Usage**:

As defined in [`WithWriterInterceptorSender`](https://github.com/McFoggy/xhub4j/blob/master/xhub4j-jaxrs-client/src/test/java/fr/brouillard/oss/security/xhub/ws/rest/ext/WithWriterInterceptorSender.java), the following code imitating the send action to a webhook

```
XHubWriterInterceptor xhubWriter = new XHubWriterInterceptor();
xhubWriter.setToken("this is the secret key");

Client c = ClientBuilder
        .newBuilder()
        .register(xhubWriter)
        .build();
WebTarget webhookTarget = c.target("http://localhost:8180/xhub4j-sample/resources/loghook");

String payload = calculateJSONPayload();
Response r = webhookTarget.request().post(Entity.json(payload));
```

Produces on the server:

```
23:16:57,041 INFO  [f.b.o.s.x.w.LogWebHook] (default task-8) Headers:
23:16:57,041 INFO  [f.b.o.s.x.w.LogWebHook] (default task-8) 	X-Hub-Signature:sha1=fa0902542a12dbf43ef081c0cc4794fd438e172d
23:16:57,041 INFO  [f.b.o.s.x.w.LogWebHook] (default task-8) 	Connection:Keep-Alive
23:16:57,041 INFO  [f.b.o.s.x.w.LogWebHook] (default task-8) 	User-Agent:Apache-HttpClient/4.2.1 (java 1.5)
23:16:57,041 INFO  [f.b.o.s.x.w.LogWebHook] (default task-8) 	Host:localhost:8180
23:16:57,042 INFO  [f.b.o.s.x.w.LogWebHook] (default task-8) 	Accept-Encoding:gzip, deflate
23:16:57,042 INFO  [f.b.o.s.x.w.LogWebHook] (default task-8) 	Content-Length:51
23:16:57,042 INFO  [f.b.o.s.x.w.LogWebHook] (default task-8) 	Content-Type:application/json
23:16:57,042 INFO  [f.b.o.s.x.w.LogWebHook] (default task-8) Body:
23:16:57,042 INFO  [f.b.o.s.x.w.LogWebHook] (default task-8) {"name":"Matthieu Brouillard","likes":"coding !!!"}
```

Profiles compliancy (when you use th predefined profile to launch tests):

- `jersey`: does not work
- `resteasy`: works
