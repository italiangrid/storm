package it.grid.storm.ea;


public class AttributeNotFoundException extends ExtendedAttributesException {

    private static final long serialVersionUID = -7093256442208513239L;

    public AttributeNotFoundException() {
    }

    public AttributeNotFoundException(String message) {
        super(message);
    }

    public AttributeNotFoundException(Throwable cause) {
        super(cause);
    }

    public AttributeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
