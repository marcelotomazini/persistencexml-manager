package net.mttechsolutions.apt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class PersistenceXmlProcessor extends AbstractProcessor {

	private String projectRootPath;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		try {
			FileObject fileObject = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, "", "x");
			projectRootPath = fileObject.toUri().getPath().replace("/src/main/java/x", "");
			Set<String> dependencies = findDependencies("");
			Set<String> entities = findEntities(dependencies);
			writePersistence(entities);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		return true;
	}

	private Set<String> findDependencies(String dependency) throws IOException, XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new PomNamespaceContext());

		if(!new File(projectRootPath + dependency).exists())
			return new HashSet<String>();
		
		InputSource inputSource = new InputSource(projectRootPath + dependency + "/pom.xml");
		NodeList dependencyNodeList = (NodeList) xpath.evaluate("/pom:project/pom:dependencies/pom:dependency/pom:artifactId", inputSource, XPathConstants.NODESET);
		String projectName = ((Node) xpath.evaluate("/pom:project/pom:artifactId", inputSource, XPathConstants.NODE)).getTextContent();
		if(dependency.isEmpty())
			projectRootPath = projectRootPath.replace(projectName, "");

		Set<String> dependencies = new HashSet<String>();
		dependencies.add(projectName);
		for (int i = 0; i < dependencyNodeList.getLength(); i++) {
			String dependencyName = dependencyNodeList.item(i).getTextContent();
			dependencies.add(dependencyName);
			dependencies.addAll(findDependencies(dependencyName));
		}

		return dependencies;
	}

	private Set<String> findEntities(Set<String> dependencies) throws Exception {
		Set<String> entities = new HashSet<String>();
		for (String dependency : dependencies) {
			List<File> files = findFiles(projectRootPath + dependency + "/src/main/java/" + packageToScan());
			for (File file : files)
				if (isEntity(file))
					entities.add(fileToQualifiedName(file));
		}
		return entities;
	}

	private String packageToScan() {
		String packageToScan = processingEnv.getOptions().get("persistencexmlmanager.packageToScan");
		return packageToScan == null ? packageToScan = "" : packageToScan.replace(".", "/");
	}

	private String fileToQualifiedName(File file) {
		String path = file.toURI().toString();
		return path.substring(path.indexOf("/src/main/java/") + 15).replace(".java", "").replace("/", ".");
	}

	private void writePersistence(Set<String> entities) throws Exception {
		FileObject resource = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/persistence.xml");
		PersistenceXmlWriter.write(new File(resource.toUri()), entities);
	}

	private List<File> findFiles(String path) throws Exception {
		List<File> files = new ArrayList<File>();

		File fileToScan = new File(path);
		if (!fileToScan.exists())
			return files;

		for (File file : fileToScan.listFiles(new JavaSourceFileFilter()))
			if (file.isDirectory())
				files.addAll(findFiles(file.getCanonicalPath()));
			else
				files.add(file);

		return files;
	}

	private boolean isEntity(File file) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
			return scanner.findWithinHorizon("@javax.persistence.Entity", 0) != null || scanner.findWithinHorizon("@Entity", 0) != null;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (scanner != null)
				scanner.close();
		}
	}
}
