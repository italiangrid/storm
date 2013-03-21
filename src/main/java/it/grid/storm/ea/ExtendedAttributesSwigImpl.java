package it.grid.storm.ea;

import it.grid.storm.filesystem.swig.storm_xattrs;

public class ExtendedAttributesSwigImpl implements ExtendedAttributes {

	public ExtendedAttributesSwigImpl() {
		
	}

	@Override
	public String getXAttr(String fileName, String attributeName){
		try{
			
			return storm_xattrs.get_xattr_value(fileName, attributeName);
		
		}catch(RuntimeException e){
			throw new ExtendedAttributesException(e);
		}
		
	}

	@Override
	public void setXAttr(String filename, String attributeName,
			String attributeValue) throws ExtendedAttributesException {
		try{
			if (attributeValue == null)
				storm_xattrs.set_xattr(filename, attributeName);
			else
				storm_xattrs.set_xattr(filename, attributeName, attributeValue);
		
		}catch (RuntimeException e){
			throw new ExtendedAttributesException(e);
		}

	}

	@Override
	public void rmXAttr(String filename, String attributeName)
			throws ExtendedAttributesException {
		try{
			
			storm_xattrs.remove_xattr(filename, attributeName);
		
		}catch(RuntimeException e){
			throw new ExtendedAttributesException(e);
		}
	}

	@Override
	public boolean hasXAttr(String fileName, String attributeName)
			throws ExtendedAttributesException {
		try{
			
			return storm_xattrs.xattr_is_set(fileName, attributeName);
		
		}catch(RuntimeException e){
			throw new ExtendedAttributesException(e);
		}
	}

}
