package it.grid.storm.tape.recalltable.providers;

import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.tape.recalltable.persistence.TapeRecallBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("text/plain")
public class TapeRecallTOListMessageBodyWriter implements
	MessageBodyWriter<List<TapeRecallTO>> {

	@Override
	public long getSize(List<TapeRecallTO> tasks, Class<?> type,
		Type genericType, Annotation[] annotations, MediaType mediaType) {

		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
		Annotation[] annotations, MediaType mediaType) {

		boolean isWritable;

		if (List.class.isAssignableFrom(type)
			&& genericType instanceof ParameterizedType) {

			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			Type[] actualTypeArgs = (parameterizedType.getActualTypeArguments());

			isWritable = (actualTypeArgs.length == 1 && actualTypeArgs[0]
				.equals(TapeRecallTO.class));

		} else {

			isWritable = false;
		}

		return isWritable;
	}

	@Override
	public void writeTo(List<TapeRecallTO> tasks, Class<?> type,
		Type genericType, Annotation[] annotations, MediaType mediaType,
		MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
		throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		for (TapeRecallTO t : tasks) {
			
			sb.append(t.toGEMSS());
			sb.append(TapeRecallBuilder.ELEMENT_SEP);
		}

		sb.append("}");

		entityStream.write(sb.toString().getBytes());
	}

}
