/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jackson;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.support.ServiceSupport;

/**
 * A <a href="http://camel.apache.org/data-format.html">data format</a> ({@link DataFormat})
 * using <a href="http://jackson.codehaus.org/">Jackson</a> to marshal to and from JSON.
 */
public class JacksonDataFormat extends ServiceSupport implements DataFormat {

    private final ObjectMapper objectMapper;
    private Class<?> unmarshalType;
    private Class<?> jsonView;
    private String include;

    /**
     * Use the default Jackson {@link ObjectMapper} and {@link Map}
     */
    public JacksonDataFormat() {
        this(HashMap.class);
    }

    /**
     * Use the default Jackson {@link ObjectMapper} and with a custom
     * unmarshal type
     *
     * @param unmarshalType the custom unmarshal type
     */
    public JacksonDataFormat(Class<?> unmarshalType) {
        this(unmarshalType, null);
    }

    /**
     * Use the default Jackson {@link ObjectMapper} and with a custom
     * unmarshal type and JSON view
     *
     * @param unmarshalType the custom unmarshal type
     * @param jsonView marker class to specifiy properties to be included during marshalling.
     *                 See also http://wiki.fasterxml.com/JacksonJsonViews
     */
    public JacksonDataFormat(Class<?> unmarshalType, Class<?> jsonView) {
        this.objectMapper = new ObjectMapper();
        this.unmarshalType = unmarshalType;
        this.jsonView = jsonView;

        // Enables JAXB processing
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        this.objectMapper.registerModule(module);
    }

    /**
     * Use a custom Jackson mapper and and unmarshal type
     *
     * @param mapper        the custom mapper
     * @param unmarshalType the custom unmarshal type
     */
    public JacksonDataFormat(ObjectMapper mapper, Class<?> unmarshalType) {
        this(mapper, unmarshalType, null);
    }

    /**
     * Use a custom Jackson mapper, unmarshal type and JSON view
     *
     * @param mapper        the custom mapper
     * @param unmarshalType the custom unmarshal type
     * @param jsonView marker class to specifiy properties to be included during marshalling.
     *                 See also http://wiki.fasterxml.com/JacksonJsonViews
     */
    public JacksonDataFormat(ObjectMapper mapper, Class<?> unmarshalType, Class<?> jsonView) {
        this.objectMapper = mapper;
        this.unmarshalType = unmarshalType;
        this.jsonView = jsonView;
    }

    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        this.objectMapper.writerWithView(jsonView).writeValue(stream, graph);
    }

    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        return this.objectMapper.readValue(stream, this.unmarshalType);
    }

    // Properties
    // -------------------------------------------------------------------------

    public Class<?> getUnmarshalType() {
        return this.unmarshalType;
    }

    public void setUnmarshalType(Class<?> unmarshalType) {
        this.unmarshalType = unmarshalType;
    }

    public Class<?> getJsonView() {
        return jsonView;
    }

    public void setJsonView(Class<?> jsonView) {
        this.jsonView = jsonView;
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    @Override
    protected void doStart() throws Exception {
        if (include != null) {
            JsonInclude.Include inc = JsonInclude.Include.valueOf(include);
            objectMapper.setSerializationInclusion(inc);
        }
    }

    @Override
    protected void doStop() throws Exception {
        // noop
    }

}
