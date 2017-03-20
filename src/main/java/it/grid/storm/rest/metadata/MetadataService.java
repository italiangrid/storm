package it.grid.storm.rest.metadata;

import javax.ws.rs.WebApplicationException;

public interface MetadataService<T> {

	public T getMetadata(String stfnPath) throws WebApplicationException;
}
