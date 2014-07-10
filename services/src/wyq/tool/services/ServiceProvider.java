package wyq.tool.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

public class ServiceProvider {

	private static ServiceProvider single = null;

	private static final String SERVICE_CONFIG_PROPERTIES = "services.properties";
	private static final String EXT_FOLDER = "lib";

	protected static final String LIB_SUFFIX = ".jar";

	private Service[] allServices = null;

	private ServiceProvider() {
		Properties p = new Properties();
		InputStream stream = loadResStream(SERVICE_CONFIG_PROPERTIES);
		if (stream == null) {
			stream = loadResStream("\\" + SERVICE_CONFIG_PROPERTIES);
		}
		if (stream == null) {
			stream = loadFileStream(SERVICE_CONFIG_PROPERTIES);
		}
		if (stream != null) {
			try {
				p.load(stream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<Service> services = new ArrayList<Service>();
		if (!p.isEmpty()) {
			// load ext libs
			ClassLoader loader = ClassLoader.getSystemClassLoader();
			ServiceLibClassloader.inject(EXT_FOLDER, loader);

			for (Object objkey : p.keySet()) {
				String key = objkey.toString();
				String className = p.getProperty(key);

				Object o = null;
				Class<?> c = null;
				if (loader != null) {
					try {
						c = loader.loadClass(className);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (c == null) {
					try {
						c = Class.forName(className);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (c != null) {
					try {
						o = c.newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (o != null && (o instanceof Service)) {
						services.add((Service) o);
					}
				}

			}
		} else {
			ServiceLoader<Service> load = ServiceLoader.load(Service.class);
			for (Service s : load) {
				services.add(s);
			}
		}

		allServices = new Service[services.size()];
		allServices = services.toArray(allServices);

	}

	private InputStream loadResStream(String name) {
		return ServiceProvider.class.getClassLoader().getResourceAsStream(name);
	}

	private InputStream loadFileStream(String name) {
		File f = new File(name);
		InputStream in = null;
		if (f.exists()) {
			try {
				in = new BufferedInputStream(new FileInputStream(f));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
		return in;
	}

	public static Service[] availableServices() {
		if (single == null) {
			single = new ServiceProvider();
		}
		return single.list();
	}

	public static void forceReload() {
		single = new ServiceProvider();
	}

	private Service[] list() {
		return Arrays.copyOf(allServices, allServices.length);
	}

}
