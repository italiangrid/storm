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

package it.grid.storm.namespace;

import org.apache.xerces.parsers.SAXParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class NamespaceValidator {

	private Logger log = LoggerFactory.getLogger(NamespaceValidator.class);

	public boolean validateSchema(String SchemaUrl, String XmlDocumentUrl) {
		boolean valid = false;
		SAXParser parser = new SAXParser();
		try {
			parser.setFeature("http://xml.org/sax/features/validation", true);
			parser.setFeature("http://apache.org/xml/features/validation/schema",
				true);
			parser.setFeature(
				"http://apache.org/xml/features/validation/schema-full-checking", true);
			parser
				.setProperty(
					"http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
					SchemaUrl);
			Validator handler = new Validator();
			parser.setErrorHandler(handler);
			parser.parse(XmlDocumentUrl);
			if (handler.validationError == true) {
				manageErrorWithinNamespace(handler);
			} else {
				log.info("Namespace Document is valid with Schema");
				valid = true;
			}
		} catch (Throwable e) {
		  log.error(e.getMessage(), e);
		}
		return valid;
	}

	private void manageErrorWithinNamespace(
		it.grid.storm.namespace.NamespaceValidator.Validator handler) {

	  log.error("namespace.xml validation error.");
	  log.error("Error: {} line: {}, column: {}, entity: {}", 
	    handler.saxParseException.getMessage(),
	    handler.saxParseException.getLineNumber(),
	    handler.saxParseException.getColumnNumber(),
	    handler.saxParseException.getSystemId());
	}

	private class Validator extends DefaultHandler {

		public boolean validationError = false;
		public SAXParseException saxParseException = null;

		@Override
		public void error(SAXParseException exception) throws SAXException {

		  log.error("XML error: {}. Line: {}, column: {}, entity: {}",
		    exception.getMessage(),
		    exception.getLineNumber(),
		    exception.getColumnNumber(),
		    exception.getSystemId());
		  
			validationError = true;
			saxParseException = exception;
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {

		  log.error("XML FATAL error: {}. Line: {}, column: {}, entity: {}",
		    exception.getMessage(),
		    exception.getLineNumber(),
		    exception.getColumnNumber(),
		    exception.getSystemId());

			validationError = true;
			saxParseException = exception;
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {

		  log.warn("XML warning: {}. Line: {}, column: {}, entity: {}",
		    exception.getMessage(),
		    exception.getLineNumber(),
		    exception.getColumnNumber(),
		    exception.getSystemId());

		}
	}
}
