/**
 * Copyright (C) 2016 McFoggy [https://github.com/McFoggy/xhub4j] (matthieu@brouillard.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.brouillard.oss.security.xhub.ws.rest.ext;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

import fr.brouillard.oss.security.xhub.XHub;
import fr.brouillard.oss.security.xhub.ws.rest.ext.impl.ByteArrayInterceptingOutputStream;

@Provider
public class XHubClientRequestFilter implements ClientRequestFilter {
    private String header = XHub.DEFAULT_HEADER_XHUB_PROPERTY;
    private XHub.XHubConverter encoder = XHub.XHubConverter.HEXA_LOWERCASE; 
    private XHub.XHubDigest hash = XHub.XHubDigest.SHA1; 
    private String token;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (token == null) {
            throw new IOException("cannot encode with a null token");
        }
        
        forceJerseyBuffering(requestContext);
        
        Consumer<byte[]> dataInterceptor = data -> {
            if (!requestContext.getHeaders().containsKey(header)) {
                String xhubHeaderValue = XHub.generateHeaderXHubToken(getEncoder(), getHash(), token, data);
                requestContext.getHeaders().putSingle(header, xhubHeaderValue);
            }
        };
        
        requestContext.setEntityStream(new ByteArrayInterceptingOutputStream(requestContext.getEntityStream(), dataInterceptor));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void forceJerseyBuffering(ClientRequestContext requestContext) {
        try {
            Class jerseyClientRequestClassImpl = Class.forName("org.glassfish.jersey.client.ClientRequest");
            Method enableBuffering = jerseyClientRequestClassImpl.getMethod("enableBuffering");
            enableBuffering.invoke(requestContext);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public XHub.XHubConverter getEncoder() {
        return encoder;
    }

    public void setEncoder(XHub.XHubConverter encoder) {
        this.encoder = encoder;
    }

    public XHub.XHubDigest getHash() {
        return hash;
    }

    public void setHash(XHub.XHubDigest hash) {
        this.hash = hash;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
