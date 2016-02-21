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
import java.util.Optional;

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
    private String headerProperty = XHub.DEFAULT_HEADER_XHUB_PROPERTY;
	private XHub.XHubConverter converter = XHubConverter.valueOf(DEFAULT_CONVERTER);

    public void init(FilterConfig filterConfig) throws ServletException {
        token = filterConfig.getInitParameter(TOKEN_PARAM_NAME);
        if (token == null) {
            throw new ServletException(String.format("missing mandatory %s  in filter %s configuration", TOKEN_PARAM_NAME, XHubFilter.class.getName()));
        }
        
        headerProperty = Optional.ofNullable(filterConfig.getInitParameter(HEADER_XHUB_PROPERTY)).orElse(headerProperty);
        converter = Optional.ofNullable(filterConfig.getInitParameter(CONVERTER_PARAM_NAME)).map(XHubConverter::valueOf).orElse(converter);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            ReadableHttpServletRequestWrapper readable = new ReadableHttpServletRequestWrapper(httpRequest);

            String xhubReceivedHeader = httpRequest.getHeader(getHeaderProperty());
            if (xhubReceivedHeader == null) {
                throw new ServletException(String.format("no %s security header received, cannot authenticate call", getHeaderProperty()));
            }
            String[] splittedXHubData = xhubReceivedHeader.split("=");

            if (splittedXHubData.length != 2) {
                throw new ServletException(String.format("received %s security header cannot be splitted, should be of the form {DIGEST}:{TOKEN}", getHeaderProperty()));
            }

            String xhubReceivedDigest = splittedXHubData[0];
            String xhubReceivedToken = splittedXHubData[1];
            
            String xhubCalculatedToken = XHub.generateXHubToken(getConverter(), XHubDigest.fromAlgorithm(xhubReceivedDigest), getToken(), readable.getRequestBodyData());

            if (!xhubCalculatedToken.equals(xhubReceivedToken)) {
                String exMessage = String.format("Security failure, received message '%s: %s' does not match calculated one: %s for %s digest", getHeaderProperty(), xhubReceivedHeader,
                        xhubCalculatedToken, xhubReceivedDigest);
                throw new ServletException(exMessage);
            }

            chain.doFilter(readable, response);
        }
    }
    
    public void destroy() {
    }

    protected String getToken() {
        return token;
    }

    protected String getHeaderProperty() {
		return headerProperty;
	}

	protected XHub.XHubConverter getConverter() {
		return converter;
	}
}
