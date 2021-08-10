package ru.aaa.test.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author adrian_român
 */
public class XmlParser {
	
	private static final Logger LOG = LoggerFactory.getLogger(XmlParser.class);
	
	private String projectBuildDirectory = "<Please set target path before...>";
	private String appResourcesURI = "<Please set uri for application resources before...>";
	
	private DocumentBuilderFactory documentBuilderFactory = null;
	private DocumentBuilder builder;
	private Document document = null;
	private XPath xpath = null;
	
	public void setProjectBuildDirectory(String projectBuildDirectory) {
		this.projectBuildDirectory = projectBuildDirectory;
	}
	
	public String getProjectBuildDirectory() {
		return projectBuildDirectory;
	}

	public String getAppResourcesURI() {
		return appResourcesURI;
	}

	public void setAppResourcesURI(String appResourcesURI) {
		this.appResourcesURI = appResourcesURI;
	}

	private void initParser(String pathFile) {	
		LOG.info("Starting init Xml parser. Test data file name: [" + pathFile + "]..........");
		LOG.info("Thread: [name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId() + "]. ");
		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setXIncludeAware(true);
			builder = documentBuilderFactory.newDocumentBuilder();
			File file = new File(projectBuildDirectory + File.separator + pathFile);
			document = (Document) builder.parse(file);
			xpath = XPathFactory.newInstance().newXPath();
			LOG.info("Ok!");
		} catch (Exception e) {
			LOG.error("Xml parser can not be initialized with the file path: [" + pathFile + "]. \n" + e);
			throw new XmlParserException("Xml parser can not be initialized with the file path: [" + pathFile +"]. \n" + e);
		}
	}
	
	private void close() {
		LOG.info("Closing Xml parser..........");
		documentBuilderFactory = null;
		builder = null;
		document = null;
		xpath = null;
		LOG.info("Ok!");
	}
	
	public synchronized String setDataElement(String pathFile, String pathElement, String textContent, boolean addXIncludeLinkOnApplicationResources) {
		initParser(pathFile);
		try {
			NodeList nodes = (NodeList) xpath.compile(pathElement + "/text()").evaluate(document, XPathConstants.NODESET);
			nodes.item(0).setNodeValue(textContent); // setTextContent(textContent);
			LOG.info("Text: [" + textContent + "] is written to the element with XPATH: [" + pathElement + "]. XML file name: [" + document.getDocumentURI() + "]");
			Reporter.log("Text: [" + textContent +"] is written to the element with XPATH: [" + pathElement +"]. XML file name: [" + document.getDocumentURI() + "] </br>");
			return textContent;
		} catch (Exception e) {
			LOG.error("Text: [" + textContent + "] can not be written to the element with XPATH: [" + pathElement + "]. XML file name: [" + document.getDocumentURI() + "]. \n" + e);
			throw new XmlParserException("Text: [" + textContent +"] can not be written to the element with XPATH: [" + pathElement +"]. XML file name: [" + document.getDocumentURI() + "]. \n" + e);
		} finally {
			saveFile(pathFile, addXIncludeLinkOnApplicationResources);
			close();
		}
	}
	
	public synchronized String getDataElement(String pathFile, String pathElement) {
		initParser(pathFile);
		try {
			Node widgetNode = (Node) xpath.evaluate(pathElement, document, XPathConstants.NODE);
			String data = widgetNode.getTextContent();
			LOG.info("Data: [" + data + "] were read from the test data xml file: [" + document.getDocumentURI() + "] by XPATH: [" + pathElement + "].");
			Reporter.log("Data: [" + data + "] were read from the test data xml file: [" + document.getDocumentURI() + "] by XPATH: [" + pathElement +"]. </br>");
			if("null".equals(data)) {
				return null;
			}
			return data;
		} catch (Exception e) {
			LOG.error("Element data with XPATH: [" + pathElement + "] from the XML file: [" + document.getDocumentURI() + "] has not been received.\n" + e);
			throw new XmlParserException("Element data with XPATH: [" + pathElement +"] from the XML file: [" + document.getDocumentURI() + "] has not been received. \n" + e);
		} finally {
			close();
		}
	}
	
	public synchronized String getDataElementFromNode(String pathFile, String pathElement, Node node) {
		initParser(pathFile);
		try {
			Node widgetNode = (Node) xpath.evaluate(pathElement, node, XPathConstants.NODE);
			String data = widgetNode.getTextContent();
			LOG.info("Data: [" + data + "] were read from the node: [" + node.getNodeName() + "] by XPATH: [" + pathElement + "].");
			Reporter.log("Data: [" + data + "] were read from the node: [" + node.getNodeName() + "] by XPATH: [" + pathElement +"]. </br>");
			return data;
		} catch (Exception e) {
			//Reporter.LOG("Element data with XPATH: [" + pathElement +"] from the node: [" + node.getNodeName() + "] has not been received.\n" + e);
			//throw new XmlParserException("Element data with XPATH: [" + pathElement +"] from the node: [" + node.getNodeName() + "] has not been received. \n" + e);
			return null;
		} finally {
			close();
		}
	}
	
	public synchronized List<String> getListElementsFromNode(String pathFile, String xpathListElements, Node node) {
		List<String> expectedList = new ArrayList<String>();
		String pattern;
		String[] arrElements;
		String data = getDataElementFromNode(pathFile, xpathListElements, node);
		if(data == null) {
			return null;
		}
		pattern = data.trim();
		arrElements = pattern.split("\n");
		int length = arrElements.length;
		for (int i = 0; i < length; i++) {
			if (!arrElements[i].isEmpty()) {
				pattern = arrElements[i].trim();
				expectedList.add(pattern);
			}
		}
		LOG.info("List data: [" + expectedList + "] were read from the test data xml file: [" + pathFile + "] by XPATH: [" + xpathListElements + "].");
		Reporter.log("List data: [" + expectedList + "] were read from the test data xml file: [" + pathFile + "] by XPATH: [" + xpathListElements +"]. </br>");
		return new ArrayList<String>(expectedList);
	}
	
	public synchronized Set<String> getListElements(String pathFile, String xpathListElements) {
		Set<String> expectedList = new HashSet<String>();
		String pattern;
		String[] arrElements;
		pattern = getDataElement(pathFile, xpathListElements).trim();
		arrElements = pattern.split("\n");
		int length = arrElements.length;
		for (int i = 0; i < length; i++) {
			if (!arrElements[i].isEmpty()) {
				pattern = arrElements[i].trim();
				expectedList.add(pattern);
			}
		}
		LOG.info("List data: [" + expectedList + "] were read from the test data xml file: [" + pathFile + "] by XPATH: [" + xpathListElements + "].");
		Reporter.log("List data: [" + expectedList + "] were read from the test data xml file: [" + pathFile + "] by XPATH: [" + xpathListElements +"]. </br>");
		return new HashSet<String>(expectedList);
	}
	
	public synchronized NodeList getNodeList(String pathFile, String xpathListElements) {
		initParser(pathFile);
		try {
			NodeList widgetNodeList = (NodeList) xpath.evaluate(xpathListElements, document, XPathConstants.NODESET);

			LOG.info("Data: [");
			for (int i=0; i<widgetNodeList.getLength(); i++){
				
				LOG.info(widgetNodeList.item(i).getNodeName() + ": " + widgetNodeList.item(i).getTextContent());
			}
			LOG.info("] were read from the test data xml file: [" + document.getDocumentURI() + "] by XPATH: [" + xpathListElements + "].");
			return widgetNodeList;
		} catch (Exception e) {
			LOG.error("Element data with XPATH: [" + xpathListElements + "] from the XML file: [" + document.getDocumentURI() + "] has not been received.\n" + e);
			throw new XmlParserException("Element data with XPATH: [" + xpathListElements +"] from the XML file: [" + document.getDocumentURI() + "] has not been received. \n" + e);
		} finally {
			close();
		}
	}

	public synchronized NodeList getNodeList(String pathFile, String xpathListElements, Node node) {
		initParser(pathFile);
		try {
			NodeList widgetNodeList = (NodeList) xpath.evaluate(xpathListElements, node, XPathConstants.NODESET);

			LOG.info("Data: [");
			for (int i=0; i<widgetNodeList.getLength(); i++){
				
				LOG.info(widgetNodeList.item(i).getNodeName() + ": " + widgetNodeList.item(i).getTextContent());
			}
			LOG.info("] were read from the test data xml file: [" + document.getDocumentURI() + "] by XPATH: [" + xpathListElements + "].");
			return widgetNodeList;
		} catch (Exception e) {
			LOG.error("Element data with XPATH: [" + xpathListElements + "] from the XML file: [" + document.getDocumentURI() + "] has not been received.\n" + e);
			throw new XmlParserException("Element data with XPATH: [" + xpathListElements +"] from the XML file: [" + document.getDocumentURI() + "] has not been received. \n" + e);
		} finally {
			close();
		}
	}
	
	public synchronized String getDataAttribute(String pathFile, String pathElement, String nameAttribute) {
		initParser(pathFile);
		try {
			Node widgetNode = (Node) xpath.evaluate(pathElement, document, XPathConstants.NODE);
			String data = widgetNode.getAttributes().getNamedItem(nameAttribute).getTextContent();
			LOG.info("Data attribute: [" + data + "] were read from the test data xml file: [" + document.getDocumentURI() + "] by XPATH: [" + pathElement + "] and attribute name: [" + nameAttribute + "].");
			Reporter.log("Data attribute: [" + data + "] were read from the test data xml file: [" + document.getDocumentURI() + "] by XPATH: [" + pathElement +"] and attribute name: [" + nameAttribute +"]. </br>");
			return data;
		} catch (Exception e) {
			LOG.error("Attribute data with XPATH: [" + pathElement + "]and attribute name: [" + nameAttribute + "] from the XML file: [" + document.getDocumentURI() + "] has not been received.\n" + e);
			throw new XmlParserException("Attribute data with element XPATH: [" + pathElement +"] and attribute name: [" + nameAttribute +"] from the XML file: [" + document.getDocumentURI() + "] had not been received. \n" + e);
		} finally {
			close();
		}
	}

	private void saveFile(String pathFile, boolean addXIncludeLinkOnApplicationResources) {
		LOG.info("Save XML Document to the file : [" + pathFile + "]. Starting.........");
		try {
			File file = new File(projectBuildDirectory + File.separator + pathFile);
			OutputStream stream = new FileOutputStream(file);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DocumentType documentType = document.getDoctype();
			if (documentType != null) {
				String publicId = documentType.getPublicId();
				if (publicId != null) {
					transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
				}
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());
			}
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			if(addXIncludeLinkOnApplicationResources) {
				// update link for Xinclude 
				updateAppResourcesLink();
			}
			// reload new Document
			Source source = new DOMSource(document);
			Result result = new StreamResult(stream);
			transformer.transform(source, result);
			//reinitializing parser
			initParser(pathFile);
			//
			LOG.info("Ok!");
		} catch (Exception e) {
			LOG.error("The XML Document can not be saved to the file:  [" + pathFile + "]. \n" + e);
			throw new XmlParserException("The XML Document can not be saved to the file:  [" + pathFile +"]. \n" + e);
		}
	}
	
	private void updateAppResourcesLink() {
		String pathAppResourcesFileName = projectBuildDirectory + File.separator + appResourcesURI/*ResourceBundle.getBundle("configuration").getString("file.name.application.resources")*/;
		Node parentNode = document.getFirstChild();		
		Element oldElement = (Element)document.getElementsByTagName("appResources").item(0);
		Element newElement = document.createElement("xi:include");
		newElement.setAttribute("href", pathAppResourcesFileName);
		newElement.setAttribute("parse", "xml");
		parentNode.replaceChild(newElement, oldElement);
	}
}









