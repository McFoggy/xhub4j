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
package fr.brouillard.oss.security.xhub.ws.rest.ext;

import javax.json.Json;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class WithClientRequestFilterWebhookSender {
    public static void main(String[] args) {
        XHubClientRequestFilter clientFilter = new XHubClientRequestFilter();
        clientFilter.setToken("this is the secret key");
        
        Client c = ClientBuilder
                .newBuilder()
                .register(clientFilter)
                .build();
        WebTarget webhookTarget = c.target("http://localhost:8180/xhub4j-sample/resources/loghook");

        String payload = calculateJSONPayload();
        Response r = webhookTarget.request().post(Entity.json(payload));
        
        System.out.println((r.getStatus()==202)?"SUCCESS":"ERROR");
    }

    private static String calculateJSONPayload() {
        return Json.createObjectBuilder()
                .add("name", "Matthieu Brouillard")
                .add("likes", "coding !!!")
                .build().toString();
    }
}
