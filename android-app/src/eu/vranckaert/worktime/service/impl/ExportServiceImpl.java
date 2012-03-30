/*
 *  Copyright 2011 Dirk Vranckaert
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
package eu.vranckaert.worktime.service.impl;

import android.content.Context;
import android.util.Log;
import eu.vranckaert.worktime.constants.TextConstants;
import eu.vranckaert.worktime.enums.Encoding;
import eu.vranckaert.worktime.enums.export.CsvSeparator;
import eu.vranckaert.worktime.exceptions.export.GeneralExportException;
import eu.vranckaert.worktime.service.ExportService;
import eu.vranckaert.worktime.utils.file.FileUtil;
import eu.vranckaert.worktime.utils.string.StringUtils;

import java.io.*;
import java.util.List;

/**
 * @author Dirk Vranckaert
 *         Date: 5/11/11
 *         Time: 14:46
 */
public class ExportServiceImpl implements ExportService {
    private static final String LOG_TAG = ExportServiceImpl.class.getSimpleName();

    @Override
    public File exportCsvFile(Context ctx, String filename, List<String> headers, List<String[]> values, CsvSeparator separator)
	throws GeneralExportException {
        Character separatorChar = separator.getSeperator();
        String emptyValue = "\"\"";

        StringBuilder result = new StringBuilder();

        if (headers != null && headers.size() > 0) {
            for (String header : headers) {
                if (StringUtils.isNotBlank(header)) {
                    result.append("\"" + header + "\"");
                } else {
                    result.append(emptyValue);
                }
                result.append(separatorChar);
            }
            result.append(TextConstants.NEW_LINE);
        }

        for (String[] valuesRecord : values) {
            for (int i=0; i<valuesRecord.length; i++) {
                String value = valuesRecord[i];
                if (StringUtils.isNotBlank(value)) {
                    result.append("\"" + value + "\"");
                } else {
                    result.append(emptyValue);
                }
                result.append(separatorChar);
            }

            result.append(TextConstants.NEW_LINE);
        }

        File exportDir = FileUtil.getExportDir(ctx);
        FileUtil.enableForMTP(ctx, exportDir);

        File file = new File(
                exportDir,
                filename + "." + CSV_EXTENSTION
        );
        FileUtil.applyPermissions(file, true, true, false);

	try {
	    boolean fileAlreadyExists = file.createNewFile();

	    if(fileAlreadyExists) {
		file.delete();
		file.createNewFile();
	    }
	} catch (IOException e) {
	    Log.e(LOG_TAG, "Probably a file-system issue...", e);
	    throw new GeneralExportException("Probably a file-system issue...", e);
	}

	FileOutputStream fos = null;
	try {
	    Encoding encoding = Encoding.UTF_8;
	    byte[] textBytes = encoding.encodeString(result.toString());
	    byte[] bom = encoding.getByteOrderMarker();

	    fos = new FileOutputStream(file);
	    fos.write(bom);
	    fos.write(textBytes);
	} catch (FileNotFoundException e) {
	    Log.e(LOG_TAG, "The file is not found", e);
	    throw new GeneralExportException("The file is not found, probably a file-system issue...", e);
	} catch (IOException e) {
	    Log.e(LOG_TAG, "Exception occurred during export...", e);
	    throw new GeneralExportException("Exception occurred during export", e);
	} finally {
	    if (fos != null) {
		try {
		    fos.close();
		} catch (IOException e) {
		    Log.e(LOG_TAG, "Could not close the stream", e);
		}
	    }
	}

        FileUtil.enableForMTP(ctx, file);

        return file;
    }

    final byte[] HEX_CHAR_TABLE = {
	(byte)'0', (byte)'1', (byte)'2', (byte)'3',
	(byte)'4', (byte)'5', (byte)'6', (byte)'7',
	(byte)'8', (byte)'9', (byte)'a', (byte)'b',
	(byte)'c', (byte)'d', (byte)'e', (byte)'f'
    };

    public String getHexString(byte[] raw) throws UnsupportedEncodingException {
	byte[] hex = new byte[2 * raw.length];
	int index = 0;

	for (byte b : raw) {
	    int v = b & 0xFF;
	    hex[index++] = HEX_CHAR_TABLE[v >>> 4];
	    hex[index++] = HEX_CHAR_TABLE[v & 0xF];
	}
	return new String(hex, "ASCII");
    }
}
