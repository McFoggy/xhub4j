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

public class HexadecimalConverter {
    public static String toHexadecimal(byte[] data) {
        StringBuffer hexadecimalBuffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            hexadecimalBuffer.append(toHexadecimal(data[i]));
        }
        return hexadecimalBuffer.toString();
    }

    public static String toHexadecimal(byte b) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((b >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((b & 0xF), 16);
        return new String(hexDigits).toUpperCase();
    }
}
