package wyq.tool.services;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ServiceLibClassloader {

	public static void inject(String libdir, ClassLoader currentLoader) {
		new ServiceLibClassloader(libdir).addLibsIntoLoader(currentLoader);
	}

	protected void addLibsIntoLoader(ClassLoader loader) {
		try {
			URLClassLoader urlLoader = (URLClassLoader) loader;
			Class<?> urlLoaderCls = URLClassLoader.class;
			Method m = urlLoaderCls.getDeclaredMethod("addURL",
					new Class[] { URL.class });
			m.setAccessible(true);
			for (URL u : urlList) {
				m.invoke(urlLoader, u);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	List<URL> urlList = new ArrayList<URL>();
	private FileFilter libFilter = new DefaultLibFileFilter();

	public ServiceLibClassloader(String libdir) {
		File dir = new File(libdir);
		if (dir.exists()) {
			File[] listFiles = dir.listFiles(libFilter);
			for (File lib : listFiles) {
				try {
					urlList.add(lib.toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class DefaultLibFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			String fileName = pathname.getName();
			return pathname.isFile()
					&& (fileName.endsWith(".jar") || fileName.endsWith(".zip")
							|| fileName.endsWith(".JAR") || fileName
								.endsWith(".ZIP"));
		}

	}

	/**
	 * @return the libFilter
	 */
	public FileFilter getLibFilter() {
		return libFilter;
	}

	/**
	 * @param libFilter
	 *            the libFilter to set
	 */
	public void setLibFilter(FileFilter libFilter) {
		this.libFilter = libFilter;
	}

}
