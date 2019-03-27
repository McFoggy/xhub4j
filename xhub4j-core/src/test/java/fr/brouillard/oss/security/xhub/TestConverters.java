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

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestConverters {
    private final static String LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
    private final static String[] LOREM_BASE64 = {"TG9yZW0=","aXBzdW0=","ZG9sb3I=","c2l0","YW1ldCw=","Y29uc2VjdGV0dXI=","YWRpcGlzY2luZw==","ZWxpdC4="};
    private final static String[] LOREM_BASE64_LOWERCASE = {"tg9yzw0=","axbzdw0=","zg9sb3i=","c2l0","yw1ldcw=","y29uc2vjdgv0dxi=","ywrpcglzy2luzw==","zwxpdc4="};
    private final static String[] LOREM_HEXA = {"4C6F72656D","697073756D","646F6C6F72","736974","616D65742C","636F6E7365637465747572","61646970697363696E67","656C69742E"};
    private final static String[] LOREM_HEXA_LOWERCASE = {"4c6f72656d","697073756d","646f6c6f72","736974","616d65742c","636f6e7365637465747572","61646970697363696e67","656c69742e"};

    @Test
    public void check_BASE64() {
        checkConversion(XHub.XHubConverter.BASE64, LOREM_BASE64);
    }

    private void checkConversion(XHub.XHubConverter converter, String[] expectedResults) {
        List<String> words = loremWords();
        for (int i = 0; i < words.size(); i++) {
            String loremWord = words.get(i);
            assertEquals(expectedResults[i], converter.convert(loremWord.getBytes()));
        }
    }

    @Test
    public void check_BASE64_LOWERCASE() {
        checkConversion(XHub.XHubConverter.BASE64_LOWERCASE, LOREM_BASE64_LOWERCASE);
    }

    @Test
    public void check_HEXA() {
        checkConversion(XHub.XHubConverter.HEXA, LOREM_HEXA);
    }
    @Test
    public void check_HEXA_LOWERCASE() {
        checkConversion(XHub.XHubConverter.HEXA_LOWERCASE, LOREM_HEXA_LOWERCASE);
    }

    private static List<String> loremWords() {
        return Arrays.asList(LOREM.split("\\s"));
    }
}
