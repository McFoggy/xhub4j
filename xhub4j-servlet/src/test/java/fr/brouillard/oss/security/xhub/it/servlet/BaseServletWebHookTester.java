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

import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.junit.Test;

import fr.brouillard.oss.security.xhub.XHub;
import fr.brouillard.oss.security.xhub.XHub.XHubConverter;
import fr.brouillard.oss.security.xhub.XHub.XHubDigest;

public class BaseServletWebHookTester {

	public BaseServletWebHookTester() {
		super();
	}

	@Test
	public void a_post_with_expected_signature_passes_the_filter(@ArquillianResteasyResource("resources/webhook") WebTarget target) {
	    String data = "This our important content to be sent";
	    String secret = "password4tests";
	    
	    final Builder request = target.request(MediaType.TEXT_PLAIN);
	    request.header("X-Hub-Signature", XHub.generateHeaderXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA1, secret, data.getBytes()));
	    
	    Response resp = request.post(Entity.text(data));
	    assertThat(resp, notNullValue());
	    assertThat(resp.getStatus(), is(Status.ACCEPTED.getStatusCode()));
	    resp.close();
	}

	@Test
	public void a_post_with_missing_signature_fails(@ArquillianResteasyResource("resources/webhook") WebTarget target) {
	    String data = "This our important content to be sent";
	    final Builder request = target.request(MediaType.TEXT_PLAIN);
	    Response resp = request.post(Entity.text(data));
	    assertThat(resp, notNullValue());
	    assertThat(resp.getStatus(), is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
	    resp.close();
	}

	@Test
	public void a_post_with_signature_with_wrong_password_fails(@ArquillianResteasyResource("resources/webhook") WebTarget target) {
	    String data = "This our important content to be sent";
	    String secret = "modified-password4tests";
	    
	    final Builder request = target.request(MediaType.TEXT_PLAIN);
	    request.header("X-Hub-Signature", XHub.generateHeaderXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA1, secret, data.getBytes()));
	    
	    Response resp = request.post(Entity.text(data));
	    assertThat(resp, notNullValue());
	    assertThat(resp.getStatus(), is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
	    resp.close();
	}

	@Test
	public void a_post_with_signature_with_wrong_converter_fails(@ArquillianResteasyResource("resources/webhook") WebTarget target) {
	    String data = "This our important content to be sent";
	    String secret = "password4tests";
	    
	    final Builder request = target.request(MediaType.TEXT_PLAIN);
	    request.header("X-Hub-Signature", XHub.generateHeaderXHubToken(XHubConverter.HEXA, XHubDigest.SHA1, secret, data.getBytes()));
	    
	    Response resp = request.post(Entity.text(data));
	    assertThat(resp, notNullValue());
	    assertThat(resp.getStatus(), is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
	    resp.close();
	}

	@Test
	public void a_post_with_signature_with_SH256_digest_succeed(@ArquillianResteasyResource("resources/webhook") WebTarget target) {
	    String data = "This our important content to be sent";
	    String secret = "password4tests";
	    
	    final Builder request = target.request(MediaType.TEXT_PLAIN);
	    request.header("X-Hub-Signature", XHub.generateHeaderXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA256, secret, data.getBytes()));
	    
	    Response resp = request.post(Entity.text(data));
	    assertThat(resp, notNullValue());
	    assertThat(resp.getStatus(), is(Status.ACCEPTED.getStatusCode()));
	    resp.close();
	}

	@Test
	public void a_post_with_signature_with_SHA512_digest_succeed(@ArquillianResteasyResource("resources/webhook") WebTarget target) {
	    String data = "This our important content to be sent";
	    String secret = "password4tests";
	    
	    final Builder request = target.request(MediaType.TEXT_PLAIN);
	    request.header("X-Hub-Signature", XHub.generateHeaderXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA512, secret, data.getBytes()));
	    
	    Response resp = request.post(Entity.text(data));
	    assertThat(resp, notNullValue());
	    assertThat(resp.getStatus(), is(Status.ACCEPTED.getStatusCode()));
	    resp.close();
	}

	@Test
	public void a_post_with_signature_with_MD5_digest_succeed(@ArquillianResteasyResource("resources/webhook") WebTarget target) {
	    String data = "This our important content to be sent";
	    String secret = "password4tests";
	    
	    final Builder request = target.request(MediaType.TEXT_PLAIN);
	    request.header("X-Hub-Signature", XHub.generateHeaderXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.MD5, secret, data.getBytes()));
	    
	    Response resp = request.post(Entity.text(data));
	    assertThat(resp, notNullValue());
	    assertThat(resp.getStatus(), is(Status.ACCEPTED.getStatusCode()));
	    resp.close();
	}

}