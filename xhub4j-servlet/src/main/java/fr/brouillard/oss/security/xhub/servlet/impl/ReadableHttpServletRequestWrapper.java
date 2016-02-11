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
package fr.brouillard.oss.security.xhub.servlet.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ReadableHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final HttpServletRequest request;
    private byte[] rawContent = null;

    public ReadableHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        
        this.request = request;
    }
    
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (rawContent == null) {
            readContent();
        }
        return new ByteArrayServletInputStream(new ByteArrayInputStream(rawContent));
    }

    private void readContent() throws IOException {
        try (ServletInputStream readOnceInputStream = request.getInputStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            int readBytes = 0;
            while ((readBytes = readOnceInputStream.read(data)) != -1) {
                buffer.write(data, 0, readBytes);
            }
            buffer.flush();
            rawContent = buffer.toByteArray();
        }
    }
    
    public byte[] getRequestBodyData() throws IOException {
        if (rawContent == null) {
            readContent();
        }
        return rawContent;
    }
}
