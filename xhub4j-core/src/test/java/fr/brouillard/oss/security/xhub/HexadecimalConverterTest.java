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

import static org.junit.Assert.*;

public class HexadecimalConverterTest {
    @Test
    public void can_convert_some_byte_values() {
        assertEquals("00", HexadecimalConverter.toHexadecimal((byte) 0x00));
        assertEquals("01", HexadecimalConverter.toHexadecimal((byte) 0x01));
        assertEquals("0A", HexadecimalConverter.toHexadecimal((byte) 0x0A));
        assertEquals("CD", HexadecimalConverter.toHexadecimal((byte) 0xCD));
        assertEquals("FF", HexadecimalConverter.toHexadecimal((byte) 0xFF));
    }

    @Test
    public void can_convert_some_byte_array_data() {
        byte[] data = {(byte) 0x00, (byte) 0x02, (byte) 0xEE, (byte) 0x14};
        assertEquals("0002EE14", HexadecimalConverter.toHexadecimal(data));
    }
}