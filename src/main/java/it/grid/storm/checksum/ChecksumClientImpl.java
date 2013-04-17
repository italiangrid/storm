/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.checksum;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChecksumClientImpl implements ChecksumClient {

	private static Logger log = LoggerFactory.getLogger(ChecksumClientImpl.class);

	private static final String GET_CHECKSUM_SERVICE = "storm/checksum.json";
	private static final String PING_SERVICE = "storm/ping.json";
	private static final String STATUS_SERVICE = "status";

	private String endpoint = null;

	public ChecksumClientImpl() {

	}

	public ChecksumClientImpl(String endpoint) throws MalformedURLException {

		setEndpoint(endpoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.checksum.ChecksumClient#getChecksum(java.lang.String,
	 * java.lang.String)
	 */
	public String getChecksum(String fileAbsolutePath, String algorithm)
		throws IOException {

		String body = "filePath=" + fileAbsolutePath + "&algorithm=" + algorithm;

		URL url = new URL(endpoint + GET_CHECKSUM_SERVICE);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setReadTimeout(0);
		connection.setDoOutput(true);

		OutputStream output = connection.getOutputStream();
		output.write(body.getBytes());

		connection.connect();

		String responseBody = getResponse(connection);

		connection.disconnect();

		if (connection.getResponseCode() != 201) {
			throw new IOException("Cannot retrieve checksum from URL: "
				+ url.toString());
		}

		JSONObject jsonResponse;

		try {
			jsonResponse = new JSONObject(responseBody);
			int status = jsonResponse.getInt("status");

			if (status == 0) {
				return jsonResponse.getString("checksum");
			}

			throw new ChecksumRuntimeException("Error computing checksum ("
				+ url.toString() + "): " + jsonResponse.getString("explanation"));

		} catch (JSONException e) {
			throw new ChecksumRuntimeException("Malformed result from URL: "
				+ url.toString() + " Response=" + responseBody);
		}
	}

	private String getResponse(HttpURLConnection connection) throws IOException {

		InputStream responseBodyStream = connection.getInputStream();
		StringBuffer responseBody = new StringBuffer();

		byte buffer[] = new byte[connection.getContentLength()];
		int read = 0;
		while ((read = responseBodyStream.read(buffer)) != -1) {
			responseBody.append(new String(buffer, 0, read));
		}

		return responseBody.toString();
	}

	public ChecksumServerStatus getStatus(String filePath) throws IOException {

		String input = "";

		if (filePath != null) {
			input = "?filePath=" + filePath;
		}
		URL url = new URL(endpoint + STATUS_SERVICE + input);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		connection.connect();

		String responseBody = getResponse(connection);

		connection.disconnect();

		log.trace("Checksum server status response: " + responseBody);

		if (connection.getResponseCode() != 200) {
			return new ChecksumServerStatus(false, "HTML error: "
				+ connection.getResponseCode(), -1, -1);
		}

		try {
			JSONObject jsonServerStatus = (new JSONObject(responseBody))
				.getJSONObject("ChecksumServerStatus");

			ChecksumServerStatus serverStatus = new ChecksumServerStatus(true);

			serverStatus.setStatusString(jsonServerStatus
				.getString(ChecksumServerStatus.STATUS_STRING_KEY));
			serverStatus.setRequestQueue(jsonServerStatus
				.getInt(ChecksumServerStatus.REQUEST_QUEUE_KEY));
			serverStatus.setIdleThreads(jsonServerStatus
				.getInt(ChecksumServerStatus.IDLE_THREADS_KEY));

			return serverStatus;

		} catch (JSONException e) {
			throw new ChecksumRuntimeException("Malformed result from URL: "
				+ url.toString() + " Response=" + responseBody);
		}
	}

	public String[] getSupportedAlgorithms() throws IOException {

		URL url = new URL(endpoint + GET_CHECKSUM_SERVICE);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		connection.connect();

		String responseBody = getResponse(connection);

		connection.disconnect();

		if (connection.getResponseCode() != 200) {
			throw new IOException("Cannot retrieve supported algorithms from URL: "
				+ url.toString());
		}

		try {
			JSONArray jsonArray = (new JSONObject(responseBody)).getJSONArray(null);

			String[] algArray = new String[jsonArray.length()];

			for (int i = 0; i < jsonArray.length(); i++) {
				algArray[i] = jsonArray.getString(i);
			}

			return algArray;

		} catch (JSONException e) {
			throw new ChecksumRuntimeException("Malformed result from URL: "
				+ url.toString() + " Response=" + responseBody);
		}
	}

	public boolean ping() {

		URL url;
		try {
			url = new URL(endpoint + PING_SERVICE);
		} catch (MalformedURLException e) {
			// Never thrown...
			return false;
		}

		HttpURLConnection connection;
		try {

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			connection.disconnect();

			if (connection.getResponseCode() != 200) {
				return false;
			}

			return true;

		} catch (IOException e) {
			return false;
		}
	}

	public void setEndpoint(String endpoint) throws MalformedURLException {

		// Check for malformed URL
		@SuppressWarnings("unused")
		URL url = new URL(endpoint);

		if (endpoint.endsWith("/")) {
			this.endpoint = endpoint;
		} else {
			this.endpoint = endpoint + "/";
		}
	}
}
