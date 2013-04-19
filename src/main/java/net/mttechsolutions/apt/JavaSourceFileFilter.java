package net.mttechsolutions.apt;

import java.io.File;
import java.io.FileFilter;

public class JavaSourceFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		 if (pathname.getName().endsWith(".java") || pathname.isDirectory())
              return true;
		return false;
	}

}
