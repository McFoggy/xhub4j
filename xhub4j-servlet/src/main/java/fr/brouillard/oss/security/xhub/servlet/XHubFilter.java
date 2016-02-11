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
package fr.brouillard.oss.security.xhub.servlet;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import fr.brouillard.oss.security.xhub.XHub;
import fr.brouillard.oss.security.xhub.XHub.XHubConverter;
import fr.brouillard.oss.security.xhub.XHub.XHubDigest;
import fr.brouillard.oss.security.xhub.servlet.impl.ReadableHttpServletRequestWrapper;

public class XHubFilter implements Filter {
    public final String HEADER_XHUB_PROPERTY = "x-hub-header";
    public final String TOKEN_PARAM_NAME = "x-hub-token";
    public final String CONVERTER_PARAM_NAME = "x-hub-converter";
    private final String DEFAULT_CONVERTER = XHub.XHubConverter.HEXA_LOWERCASE.name();;

    private String token;
    private String headerProperty;
    private XHub.XHubConverter converter;

    public void init(FilterConfig filterConfig) throws ServletException {
        token = filterConfig.getInitParameter(TOKEN_PARAM_NAME);
        Objects.requireNonNull(token, "missing mandatory " + TOKEN_PARAM_NAME + " in filter" + XHubFilter.class.getName() + " configuration");

        headerProperty = filterConfig.getInitParameter(HEADER_XHUB_PROPERTY);
        if (headerProperty == null) {
            headerProperty = XHub.DEFAULT_HEADER_XHUB_PROPERTY;
        }
        
        String converterName = filterConfig.getInitParameter(CONVERTER_PARAM_NAME);
        if (converterName == null) {
            converterName = DEFAULT_CONVERTER;
        }
        converter = XHubConverter.valueOf(converterName);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            ReadableHttpServletRequestWrapper readable = new ReadableHttpServletRequestWrapper(httpRequest);

            String xhubReceivedHeader = httpRequest.getHeader(headerProperty);
            if (xhubReceivedHeader == null) {
                throw new ServletException(String.format("no %s security header received, cannot authenticate call", headerProperty));
            }
            String[] splittedXHubData = xhubReceivedHeader.split("=");

            if (splittedXHubData.length != 2) {
                throw new ServletException(String.format("received %s security header cannot be splitted, should be of the form {DIGEST}:{TOKEN}", headerProperty));
            }

            String xhubReceivedDigest = splittedXHubData[0];
            String xhubReceivedToken = splittedXHubData[1];
            
            String xhubCalculatedToken = XHub.generateXHubToken(converter, XHubDigest.fromAlgorithm(xhubReceivedDigest), getToken(), readable.getRequestBodyData());

            if (xhubCalculatedToken.equals(xhubReceivedToken)) {
                String exMessage = String.format("Security failure, received message '%s: %s' does not match calculated one: %s for %s digest", headerProperty, xhubReceivedHeader,
                        xhubCalculatedToken, xhubReceivedDigest);
                throw new ServletException(exMessage);
            }

            chain.doFilter(readable, response);
        }
    }

    protected String getToken() {
        return token;
    }

    public void destroy() {
    }
}
