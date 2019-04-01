/*
 * Copyright 2019 Chocohead
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.chocohead.mm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.Permission;
import java.util.Collections;
import java.util.Map;

public class CasualStreamHandler extends URLStreamHandler {
	private static class CasualConnection extends URLConnection {
		private final byte[] realStream;

		public CasualConnection(URL url, byte[] realStream) {
			super(url);

			this.realStream = realStream;
		}

		@Override
		public void connect() throws IOException {
			System.out.println("Connection attempt");
			throw new UnsupportedOperationException();
		}

		@Override
		public InputStream getInputStream() {
			return new ByteArrayInputStream(realStream);
		}

		@Override
		public Permission getPermission() {
			return null;
		}
	}

	private final Map<String, byte[]> providers;

	/*static void preload() {
		//Entertaining hack to ensure CasualConnection is classloaded
		new CasualConnection(null, null);
	}*/

	public static URL create(String name, byte[] stream) {
		return create(Collections.singletonMap('/' + name.replace('.', '/') + ".class", stream));
	}

	public static URL create(Map<String, byte[]> mixins) {
		try {
			return new URL("magic-at", null, -1, "/", new CasualStreamHandler(mixins));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Unexpected error creating URL", e);
		}
	}

	//There is a proper way to do this too https://stackoverflow.com/questions/26363573/registering-and-using-a-custom-java-net-url-protocol
	public CasualStreamHandler(Map<String, byte[]> providers) {
		this.providers = providers;
	}

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		//System.out.println(providers.keySet());
		//System.out.println("Open connection on " + url.getPath());
		if (!providers.containsKey(url.getPath())) throw new IOException(); //Who?
		//System.out.println("### PASSED ###");
		return new CasualConnection(url, providers.get(url.getPath()));
	}

	/*@Override
	protected boolean sameFile(URL u1, URL u2) {
		System.out.println("Comparing files " + u1.getFile() + " and " + u2.getFile());
		return super.sameFile(u1, u2);
	}

	@Override
	protected boolean equals(URL u1, URL u2) {
		System.out.println("Comparing " + u1 + " and " + u2);
		return super.equals(u1, u2);
	}

	@Override
	protected void parseURL(URL u, String spec, int start, int limit) {
		System.out.println("Parsing URL: " + u + " with " + spec);
		super.parseURL(u, spec, start, limit);
	}*/
}