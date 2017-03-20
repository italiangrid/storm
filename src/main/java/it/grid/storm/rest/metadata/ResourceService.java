package it.grid.storm.rest.metadata;

import javax.ws.rs.WebApplicationException;

public interface ResourceService<T> {

	public T getResource(String stfnPath) throws WebApplicationException;
}
