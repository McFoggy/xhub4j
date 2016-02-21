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
package fr.brouillard.oss.security.xhub.it.servlet.webhook;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OKWebhookServlet extends HttpServlet {
    private static Logger LOGGER = Logger.getLogger(OKWebhookServlet.class.getName());
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info(String.format("handling: %s on %s", req.getRequestURI(), OKWebhookServlet.class.getSimpleName()));
        LOGGER.info("HTTP Headers");
        
        Collections.list(req.getHeaderNames())
            .stream()
            .map(header -> header + ": " + req.getHeader(header))
            .forEach(LOGGER::info);
        
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }
}
