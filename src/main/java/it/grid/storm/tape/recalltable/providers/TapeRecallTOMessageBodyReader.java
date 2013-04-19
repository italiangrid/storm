package it.grid.storm.tape.recalltable.providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.tape.recalltable.TapeRecallException;
import it.grid.storm.tape.recalltable.model.TapeRecallData;
import it.grid.storm.tape.recalltable.persistence.TapeRecallBuilder;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;


public class TapeRecallTOMessageBodyReader implements MessageBodyReader<TapeRecallTO> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return false;
	}

	@Override
	public TapeRecallTO readFrom(Class<TapeRecallTO> type, Type genericType,
		Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> htttHeaders,
		InputStream entityStream) throws IOException, WebApplicationException {

		// parse the Input Stream
		String inputStr = buildInputString(entityStream);
		
		// build a recall task object out of the input stream
		TapeRecallData rtd;
		
		try {
		
			rtd = TapeRecallData.buildFromString(inputStr);
		
		} catch (TapeRecallException e) {
			
			throw new IOException("Problem in deserializing TapeRecallTO", e);
		}
		
		// Store the new Recall Task if it is all OK.
		TapeRecallTO task = TapeRecallBuilder.buildFromPOST(rtd);

		return task;
	}

	/**
	 * Utility method.
	 */
	private String buildInputString(InputStream input) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		
		try {
		
			while ((line = reader.readLine()) != null) {
				
				sb.append(line + "\n");
			}
		
		} catch (IOException e) {
			
			e.printStackTrace();
		
		} finally {
			
			try {
			
				input.close();
			
			} catch (IOException e) {
			
				e.printStackTrace();
			}
		}
		
		String inputStr = sb.toString();
		
		return inputStr;
	}

}
