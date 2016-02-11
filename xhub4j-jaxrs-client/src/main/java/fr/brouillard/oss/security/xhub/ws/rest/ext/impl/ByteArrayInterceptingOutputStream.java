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
package fr.brouillard.oss.security.xhub.ws.rest.ext.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

public class ByteArrayInterceptingOutputStream extends OutputStream {
    private OutputStream delegate;
    private ByteArrayOutputStream intercepted;
    private Consumer<byte[]> onCloseConsumer;
    
    public ByteArrayInterceptingOutputStream(OutputStream delegate) {
        this(delegate, null);
    }
    
    public ByteArrayInterceptingOutputStream(OutputStream delegate, Consumer<byte[]> onCloseConsumer) {
        this.delegate = delegate;
        this.onCloseConsumer = onCloseConsumer;
        intercepted = new ByteArrayOutputStream();
    }
    
    public void write(int b) throws IOException {
        delegate.write(b);
        intercepted.write(b);
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public void write(byte[] b) throws IOException {
        delegate.write(b);
        intercepted.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        delegate.write(b, off, len);
        intercepted.write(b, off, len);
    }

    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public void flush() throws IOException {
        delegate.flush();
        intercepted.flush();
    }

    public void close() throws IOException {
        intercepted.close();
        
        if (onCloseConsumer != null) {
            onCloseConsumer.accept(interceptedContent());
        }
        
        delegate.close();
    }

    public String toString() {
        return delegate.toString();
    }
    
    public byte[] interceptedContent() {
        try {
            intercepted.flush();
        } catch (IOException ignore) {}
        return intercepted.toByteArray();
    }
}
