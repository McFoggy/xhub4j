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

    @Test
    public void check_BASE64() {
        checkConvertersCompatibility(XHub.XHubConverter.OLD_BASE64, XHub.XHubConverter.BASE64);
    }
    @Test
    public void check_BASE64_LOWERCASE() {
        checkConvertersCompatibility(XHub.XHubConverter.OLD_BASE64_LOWERCASE, XHub.XHubConverter.BASE64_LOWERCASE);
    }
    @Test
    public void check_HEXA() {
        checkConvertersCompatibility(XHub.XHubConverter.OLD_HEXA, XHub.XHubConverter.HEXA);
    }
    @Test
    public void check_HEXA_LOWERCASE() {
        checkConvertersCompatibility(XHub.XHubConverter.OLD_HEXA_LOWERCASE, XHub.XHubConverter.HEXA_LOWERCASE);
    }

    private void checkConvertersCompatibility(XHub.XHubConverter oldConverter, XHub.XHubConverter newConverter) {
        loremWords().forEach(word -> {
            byte[] bytes = word.getBytes();
            assertEquals(oldConverter.convert(bytes), newConverter.convert(bytes));
        });
    }

    private static List<String> loremWords() {
        return Arrays.asList(LOREM.split("\\s"));
    }
}
