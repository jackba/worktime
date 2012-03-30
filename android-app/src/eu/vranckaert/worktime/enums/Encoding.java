/*
 *  Copyright 2012 Dirk Vranckaert
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.vranckaert.worktime.enums;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Dirk Vranckaert
 */
public enum Encoding {
    UTF_8("UTF-8", new Byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}),
    UTF_16_BE("UTF-16BE", new Byte[] {(byte) 0xFE, (byte) 0xFF}),
    UTF_16_LE("UTF-16LE", new Byte[] {(byte) 0xFF, (byte) 0xFE});
    
    private String encoding;
    private Byte[] byteOrderMarker;
    
    Encoding(String encoding, Byte[] byteOrderMarker) {
	this.encoding = encoding;
	this.byteOrderMarker = byteOrderMarker;
    }
    
    public byte[] encodeString(String text) {
	Charset charset = Charset.forName(encoding);
	ByteBuffer b = charset.encode(text);
	for (int i=0; i<byteOrderMarker.length; i++) {
	    byte bomByte = byteOrderMarker[i];
	    b.put(i, bomByte);
	}
	return b.array();
    }
}
