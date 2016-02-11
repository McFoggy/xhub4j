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
import java.util.Objects;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import fr.brouillard.oss.security.xhub.XHub;
import fr.brouillard.oss.security.xhub.ws.rest.ext.impl.ByteArrayInterceptingOutputStream;

@Provider
public class XHubWriterInterceptor implements WriterInterceptor {
    private String header = XHub.DEFAULT_HEADER_XHUB_PROPERTY;
    private XHub.XHubConverter encoder = XHub.XHubConverter.HEXA_LOWERCASE; 
    private XHub.XHubDigest hash = XHub.XHubDigest.SHA1; 
    private String token; 
    
    public void aroundWriteTo(WriterInterceptorContext ctx) throws IOException, WebApplicationException {
        ByteArrayInterceptingOutputStream interceptOS = new ByteArrayInterceptingOutputStream(ctx.getOutputStream());
        ctx.setOutputStream(interceptOS);
        ctx.proceed();
        
        if (!ctx.getHeaders().containsKey(header)) {
            String xhubHeaderValue = XHub.generateHeaderXHubToken(getEncoder(), getHash(), Objects.requireNonNull(token, "cannot encode with a null token"), interceptOS.interceptedContent());
            ctx.getHeaders().putSingle(header, xhubHeaderValue);
        }
        return;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = Objects.requireNonNull(header, "header property cannot be null");
    }

    public XHub.XHubConverter getEncoder() {
        return encoder;
    }

    public void setEncoder(XHub.XHubConverter encoder) {
        this.encoder = Objects.requireNonNull(encoder, "encoder cannot be null");
    }

    public XHub.XHubDigest getHash() {
        return hash;
    }

    public void setHash(XHub.XHubDigest hash) {
        this.hash = Objects.requireNonNull(hash, "hash cannot be null");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
