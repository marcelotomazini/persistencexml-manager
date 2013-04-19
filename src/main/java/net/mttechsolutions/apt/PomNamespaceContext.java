package net.mttechsolutions.apt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class PomNamespaceContext implements NamespaceContext {
	private final Map<String, String> namespaces = new HashMap<String, String>();
	private final String defaultNamespaceURI = "http://maven.apache.org/POM/4.0.0";

	public PomNamespaceContext() {
		namespaces.put("pom", "http://maven.apache.org/POM/4.0.0");
	}

	@Override
	public Iterator<?> getPrefixes(String namespaceURI) {
		throw new IllegalStateException("Not Implemented.");
	}

	@Override
	public String getPrefix(String namespaceURI) {
		throw new IllegalStateException("Not Implemented.");
	}

	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix == null)
			throw new IllegalArgumentException();

		if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE))
			return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

		if (prefix.equals(XMLConstants.XML_NS_PREFIX))
			return XMLConstants.XML_NS_URI;

		if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
			return defaultNamespaceURI;

		return namespaces.get(prefix) == null ? XMLConstants.NULL_NS_URI : namespaces.get(prefix);
	}
}
