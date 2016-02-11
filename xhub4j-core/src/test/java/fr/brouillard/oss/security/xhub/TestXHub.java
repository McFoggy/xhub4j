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
package fr.brouillard.oss.security.xhub;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import fr.brouillard.oss.security.xhub.XHub.XHubConverter;
import fr.brouillard.oss.security.xhub.XHub.XHubDigest;

public class TestXHub {
    @Test
    public void checkSHA1() {
        String token = "qnscAdgRlkIhAUPY44oiexBKtQbGY0orf7OV1I50";
        String data = "foo";
        
        String expectedResult = "+3h2gpjf4xcynjCGU5lbdMBwGOc=";
        
        assertThat(XHub.generateXHubToken(XHubConverter.BASE64, XHubDigest.SHA1, token, data.getBytes()), is(expectedResult));
    }
    
    @Test
    public void check_token_is_same_as_github() {
        // https://github.com/github/github-services/blob/f3bb3dd780feb6318c42b2db064ed6d481b70a1f/lib/service/http_helper.rb#L77
        // http://ruby-doc.org/stdlib-2.1.0/libdoc/openssl/rdoc/OpenSSL/HMAC.html#hexdigest-method
        
        String token = "key";
        String data = "The quick brown fox jumps over the lazy dog";
        String expectedResult = "de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9";
        
        assertThat(XHub.generateXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA1, token, data.getBytes()), is(expectedResult));
    }
    
    @Test
    public void check_header_is_same_as_github() {
        String token = "key";
        String data = "The quick brown fox jumps over the lazy dog";
        String expectedResult = "sha1=de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9";
        
        String generatedHeader = XHub.generateHeaderXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA1, token, data.getBytes());
        assertThat(generatedHeader, is(expectedResult));
    }
}
