/**
 * Copyright (c) 2012, NuoDB, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of NuoDB, Inc. nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL NUODB, INC. BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.continuent.tungsten.replicator.database;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sergey Bushik
 */
public class JdbcURLHelper {

    public static final String PAIR_SEPARATOR = "&";
    public static final String VALUE_SEPARATOR = "=";

    public static Map<String, Object> parseParams(String pairs) {
        return parseParams(pairs, PAIR_SEPARATOR, VALUE_SEPARATOR);
    }

    public static Map<String, Object> parseParams(String pairs, String pairSeparator, String valueSeparator) {
        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parseParams(parameters, pairs, pairSeparator, valueSeparator);
        return parameters;
    }

    public static void parseParams(Map<String, Object> params, String pairs, String pairSeparator,
                                   String valueSeparator) {
        if (pairs != null) {
            for (String pair : pairs.split(pairSeparator)) {
                String[] values = pair.split(valueSeparator);
                params.put(values[0], values.length > 1 ? values[1] : null);
            }
        }
    }

    public static String mergeParams(Map<String, Object> parameters) {
        return mergeParams(parameters, PAIR_SEPARATOR, VALUE_SEPARATOR);
    }

    public static String mergeParams(Map<String, Object> params, String pairSeparator, String valueSeparator) {
        StringBuilder pairs = new StringBuilder();
        for (Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> pair = iterator.next();
            pairs.append(pair.getKey());
            pairs.append(valueSeparator);
            pairs.append(pair.getValue());
            if (iterator.hasNext()) {
                pairs.append(pairSeparator);
            }
        }
        return pairs.toString();
    }
}
