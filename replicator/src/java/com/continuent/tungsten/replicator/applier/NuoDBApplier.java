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
package com.continuent.tungsten.replicator.applier;

import com.continuent.tungsten.replicator.ReplicatorException;
import com.continuent.tungsten.replicator.dbms.StatementData;
import com.continuent.tungsten.replicator.plugin.PluginContext;
import org.apache.log4j.Logger;

import java.util.Map;

import static com.continuent.tungsten.replicator.database.JdbcURLHelper.mergeParameters;
import static com.continuent.tungsten.replicator.database.JdbcURLHelper.parseParameters;

/**
 * @author Sergey Bushik
 */
public class NuoDBApplier extends JdbcApplier {

    public static final String PARAMETER_SCHEMA = "schema";
    private transient Logger logger = Logger.getLogger(getClass());

    private String host = "localhost";
    private Integer port;
    private String database;
    private String schema;
    private String parameters;

    public void configure(PluginContext context) throws ReplicatorException {
        if (url == null) {
            StringBuilder url = new StringBuilder();
            url.append("jdbc:com.nuodb://");
            url.append(host);
            if (port != null) {
                url.append(":");
                url.append(port);
            }
            url.append("/");
            url.append(database);
            String parameters = createParameters();
            if (parameters != null && !parameters.isEmpty()) {
                url.append("?");
                url.append(parameters);
            }
            this.url = url.toString();
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Property url is already set, ignoring connection properties");
            }
        }
        super.configure(context);
    }

    protected String createParameters() {
        Map<String, Object> parameters = parseParameters(this.parameters);
        if (schema != null && !schema.isEmpty()) {
            parameters.put(PARAMETER_SCHEMA, schema);
        }
        return mergeParameters(parameters);
    }

    @Override
    protected void applyStatementData(StatementData data) throws ReplicatorException {
        applyStatementData(data, null);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
