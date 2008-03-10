package it.grid.storm.namespace;

import it.grid.storm.srm.types.InvalidTTURLAttributesException;


public class InvalidProtocolForTURLException extends InvalidTTURLAttributesException {

  private String protocolSchema;

  public InvalidProtocolForTURLException(String protocolSchema) {
    super();
    this.protocolSchema = protocolSchema;
  }


  public InvalidProtocolForTURLException( Throwable cause ) {
    super( cause );
  }

  public String toString() {
    return ("Impossible to build TURL with the protocol schema '"+protocolSchema+"'" );
}


}
