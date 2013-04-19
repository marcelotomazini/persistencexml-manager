package net.mttechsolutions.apt;

import java.io.File;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PersistenceXmlWriter {

	public static void write(File file, Set<String> classes) throws Exception {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(file);

		Node rootElement = document.getElementsByTagName("persistence-unit").item(0);

		NodeList elementsByTagName = document.getElementsByTagName("class");
		for (int i = 0; i < elementsByTagName.getLength(); i++)
			rootElement.removeChild(elementsByTagName.item(i));

		for (String className : classes) {
			Element classElement = document.createElement("class");
			classElement.setTextContent(className);
			rootElement.insertBefore(classElement, document.getElementsByTagName("properties").item(0));
		}

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(new DOMSource(document), new StreamResult(file));
	}
}
