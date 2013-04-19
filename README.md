persistencexml-manager
======================

Find and add entities to persistence.xml

Just add a compile time dependency to your pom:

<dependency>
	<groupId>net.mttechsolutions</groupId>
    <artifactId>persistencexml-manager</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>

Optionally, you can inform one package to restrict the search of entities using the argument: persistencexmlmanager.packageToScan

<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-compiler-plugin</artifactId>
	<configuration>
		<compilerArgument>-Apersistencexmlmanager.packageToScan=net.mttechsolutions.entities</compilerArgument>
	</configuration>
</plugin>