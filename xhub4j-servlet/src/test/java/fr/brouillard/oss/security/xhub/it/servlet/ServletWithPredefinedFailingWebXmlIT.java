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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.brouillard.oss.security.xhub.XHub;
import fr.brouillard.oss.security.xhub.XHub.XHubConverter;
import fr.brouillard.oss.security.xhub.XHub.XHubDigest;
import fr.brouillard.oss.security.xhub.it.servlet.webhook.OKWebhookServlet;

@RunWith(Arquillian.class)
public class ServletWithPredefinedFailingWebXmlIT {
    @Deployment(testable=false)
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addPackages(false, "fr.brouillard.oss.security.xhub")   // add xhub4j-core
                .addPackages(true, "fr.brouillard.oss.security.xhub.servlet")   // add xhub4j-servlet
                .addClass(OKWebhookServlet.class)		// add test webhook servlet
                .addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml")
                .addAsWebInfResource("WEB-INF/predefined-ok-servlet-with-bad-configured-xhub-filter.xml", "web.xml")
                ;
        return war;
    }
    
    @Test
    public void will_fail_on_init(@ArquillianResteasyResource("resources/webhook") WebTarget target) {
        String data = "This our important content to be sent";
        String secret = "password4tests";
        
        final Builder request = target.request(MediaType.TEXT_PLAIN);
        request.header("X-Hub-Signature", XHub.generateHeaderXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA1, secret, data.getBytes()));
        
        Response resp = request.post(Entity.text(data));
        assertThat(resp, notNullValue());
        assertThat(resp.getStatus(), is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
        resp.close();
    }
}
