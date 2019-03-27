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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class XHub {
    public enum XHubDigest {
        MD5,
        SHA1,
        SHA256,
        SHA512;
        
        public static XHubDigest fromAlgorithm(String algo) {
            if (algo == null) {
                throw new IllegalArgumentException("cannot build a " +  XHubDigest.class.getName() + " from a null value");
            }
            
            return XHubDigest.valueOf(algo.toUpperCase());
        }
        
        String macAlgorithm() {
            return "Hmac" + name();
        }
    }
    
    public enum XHubConverter {
        BASE64 {
            @Override
            public String convert(byte[] data) {
                return Base64.getEncoder().encodeToString(data);
            }
        },
        BASE64_LOWERCASE {
            @Override
            public String convert(byte[] data) {
                return Base64.getEncoder().encodeToString(data).toLowerCase();
            }
        },
        HEXA {
            @Override
            public String convert(byte[] data) {
                return HexadecimalConverter.toHexadecimal(data);
            }
        }, 
        HEXA_LOWERCASE {
            @Override
            public String convert(byte[] data) {
                return HexadecimalConverter.toHexadecimal(data).toLowerCase();
            }
        };
        
        public abstract String convert(byte[] data);
    }
    
    public static String generateHeaderXHubToken(XHubConverter converter, XHubDigest digest, String secret, byte[] data) {
        return String.format("%s=%s", digest.name().toLowerCase(Locale.ENGLISH), generateXHubToken(converter, digest, secret, data));
    }
    
    public static String generateXHubToken(XHubConverter converter, XHubDigest digest, String secret, byte[] data) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(),
                    digest.macAlgorithm());
            
            Mac mac = Mac.getInstance(digest.macAlgorithm());
            mac.init(keySpec);
            byte[] result = mac.doFinal(data);
            
            return converter.convert(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("cannot compute x-hub token", e);
        }
    }

    public static final String DEFAULT_HEADER_XHUB_PROPERTY = "X-Hub-Signature";
}
