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
package fr.brouillard.oss.security.xhub.it.servlet;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import fr.brouillard.oss.security.xhub.XHub.XHubConverter;
import fr.brouillard.oss.security.xhub.servlet.XHubFilter;

@WebFilter(
		urlPatterns="/resources/webhook"
		,initParams={
				@WebInitParam(name="x-hub-token", value="dynamic")
		}
)
public class DeclarativeWebFilter extends XHubFilter {
	@Override
	protected String getToken() {
		return "password4tests";
	}
	
	@Override
	protected XHubConverter getConverter() {
		return XHubConverter.HEXA_LOWERCASE;
	}
}
