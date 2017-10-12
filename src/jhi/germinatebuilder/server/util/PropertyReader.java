/*
 *  Copyright 2017 Sebastian Raubach from the Information
 *  and Computational Sciences Group at JHI Dundee
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.germinatebuilder.server.util;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;

import javax.servlet.http.*;

import jhi.database.server.*;

/**
 * {@link PropertyReader} is a wrapper around {@link Properties} to read properties.
 *
 * @author Sebastian Raubach
 */
public class PropertyReader
{
	/** path.ant: The path to the Ant installation */
	public static final String PATH_ANT = "path.ant";

	/** path.germinate: The path to the Germinate installation */
	public static final String PATH_GERMINATE  = "path.germinate";
	public static final String PATH_GATEKEEPER = "path.gatekeeper";

	public static final String API_KEY = "api.key";

	public static final String BUILD_ENABLED     = "build.enabled";
	public static final String S3_UPLOAD_ENABLED = "s3.upload.enabled";

	public static final String EMAIL_SERVER   = "email.server";
	public static final String EMAIL_ADDRESS  = "email.address";
	public static final String EMAIL_PORT     = "email.port";
	public static final String EMAIL_USERNAME = "email.username";
	public static final String EMAIL_PASSWORD = "email.password";

	public static final String BUILDER_SERVER   = "builder.server";
	public static final String BUILDER_DATABASE = "builder.database";
	public static final String BUILDER_PORT     = "builder.port";
	public static final String BUILDER_USERNAME = "builder.username";
	public static final String BUILDER_PASSWORD = "builder.password";

	public static final String GATEKEEPER_SERVER   = "gatekeeper.server";
	public static final String GATEKEEPER_DATABASE = "gatekeeper.database";
	public static final String GATEKEEPER_PORT     = "gatekeeper.port";
	public static final String GATEKEEPER_USERNAME = "gatekeeper.username";
	public static final String GATEKEEPER_PASSWORD = "gatekeeper.password";

	public static final String AMAZON_S3_ACCESS_KEY        = "amazon.s3.access.key";
	public static final String AMAZON_S3_ACCESS_KEY_SECRET = "amazon.s3.access.key.secret";
	public static final String AMAZON_S3_BUCKET_NAME       = "amazon.s3.bucket.name";
	public static final String AMAZON_S3_TARGET_FOLDER     = "amazon.s3.target.folder";

	/** The name of the properties file */
	private static final String PROPERTIES_FILE = "config.properties";

	private static Properties properties = new Properties();
	private static PropertyChangeListenerThread fileWatcher;

	private static Thread   fileWatcherThread;
	private static WatchKey watchKey;

	/**
	 * Attempts to reads the properties file and then checks the required properties.
	 */
	public static void initialize()
	{
		/* Start to listen for file changes */
		Path path = new File(PropertyReader.class.getClassLoader().getResource(PROPERTIES_FILE).getPath()).getParentFile().toPath();
		FileSystem fs = path.getFileSystem();

		try
		{
			WatchService service = fs.newWatchService();
			/* start the file watcher thread below */
			fileWatcher = new PropertyChangeListenerThread(service);
			fileWatcherThread = new Thread(fileWatcher, "PropertyFileWatcher");
			fileWatcherThread.start();

            /* Register events */
			watchKey = path.register(service, StandardWatchEventKinds.ENTRY_MODIFY);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		loadProperties();
	}

	private static void loadProperties()
	{
		InputStream stream = null;
		try
		{
			stream = PropertyReader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			properties.load(stream);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		checkRequiredProperties();

		Database.init(getServerString(), getProperty(BUILDER_USERNAME), getProperty(BUILDER_PASSWORD));
	}

	/**
	 * Returns a String of the form <SERVER>:<PORT>/<DATABASE>
	 *
	 * @return A String of the form <SERVER>:<PORT>/<DATABASE>
	 */
	private static String getServerString()
	{
		String server = PropertyReader.getProperty(PropertyReader.BUILDER_SERVER);
		String database = PropertyReader.getProperty(PropertyReader.BUILDER_DATABASE);
		String port = PropertyReader.getProperty(PropertyReader.BUILDER_PORT);

		return getServerString(server, database, port);
	}

	/**
	 * Returns a String of the form <SERVER>:<PORT>/<DATABASE>
	 *
	 * @param server   The server string
	 * @param database The database string
	 * @param port     The port string
	 * @return A String of the form <SERVER>:<PORT>/<DATABASE>
	 */
	private static String getServerString(String server, String database, String port)
	{
		if (!StringUtils.isEmpty(port))
		{
			return server + ":" + port + "/" + database;
		}
		else
		{
			return server + "/" + database;
		}
	}

	public static void stopFileWatcher()
	{
		if (watchKey != null)
			watchKey.cancel();
		if (fileWatcher != null)
			fileWatcher.stop();
		if (fileWatcherThread != null)
			fileWatcherThread.interrupt();
	}

	/**
	 * Checks the required properties
	 */
	private static void checkRequiredProperties()
	{
		if (StringUtils.isEmpty(getProperty(PATH_ANT)))
			throwException(PATH_ANT);

		if (StringUtils.isEmpty(getProperty(PATH_GERMINATE)))
			throwException(PATH_GERMINATE);

		if (StringUtils.isEmpty(getProperty(PATH_GATEKEEPER)))
			throwException(PATH_GATEKEEPER);

//		if (StringUtils.isEmpty(getProperty(GATEKEEPER_SERVER)))
//			throwException(GATEKEEPER_SERVER);
//		if (StringUtils.isEmpty(getProperty(GATEKEEPER_DATABASE)))
//			throwException(GATEKEEPER_DATABASE);
//		if (StringUtils.isEmpty(getProperty(GATEKEEPER_USERNAME)))
//			throwException(GATEKEEPER_USERNAME);

		if (StringUtils.isEmpty(getProperty(BUILDER_SERVER)))
			throwException(BUILDER_SERVER);
		if (StringUtils.isEmpty(getProperty(BUILDER_DATABASE)))
			throwException(BUILDER_DATABASE);
		if (StringUtils.isEmpty(getProperty(BUILDER_USERNAME)))
			throwException(BUILDER_USERNAME);
	}

	/**
	 * Throws a {@link RuntimeException} for the given property
	 *
	 * @param property The name of the property.
	 */
	private static void throwException(String property)
	{
		throw new RuntimeException("Germinate Builder failed to start: Non-optional property not set: '" + property + "'");
	}

	/**
	 * Reads a property from the .properties file
	 *
	 * @param propertyName The property to read
	 * @return The property or <code>null</code> if the property is not found
	 */
	public static String getProperty(String propertyName)
	{
		return properties.getProperty(propertyName);
	}

	/**
	 * Reads a property from the .properties file. The fallback will be used if there is no such property.
	 *
	 * @param propertyName The property to read
	 * @param fallback     The value that is returned if the property isn't set
	 * @return The property or the fallback if the property is not found
	 */
	public static String getProperty(String propertyName, String fallback)
	{
		String property = getProperty(propertyName);

		return StringUtils.isEmpty(property) ? fallback : property;
	}

	/**
	 * Reads an {@link Integer} property from the .properties file
	 *
	 * @param propertyName The property to read
	 * @return The {@link Integer} property
	 */
	public static Integer getPropertyInteger(String propertyName)
	{
		return Integer.parseInt(getProperty(propertyName));
	}

	/**
	 * Reads an {@link Integer} property from the .properties file. The fallback will be used if there is no such property.
	 *
	 * @param propertyName The property to read
	 * @param fallback     The value that is returned if the property isn't set
	 * @return The {@link Integer} property
	 */
	public static Integer getPropertyInteger(String propertyName, int fallback)
	{
		try
		{
			return Integer.parseInt(getProperty(propertyName));
		}
		catch (Exception e)
		{
			return fallback;
		}
	}

	/**
	 * Reads an {@link Boolean} property from the .properties file
	 *
	 * @param propertyName The property to read
	 * @return The {@link Boolean} property
	 */
	public static Boolean getPropertyBoolean(String propertyName)
	{
		return Boolean.parseBoolean(getProperty(propertyName));
	}

	/**
	 * Reads an {@link Boolean} property from the .properties file. The fallback will be used if there is no such property.
	 *
	 * @param propertyName The property to read
	 * @param fallback     The value that is returned if the property isn't set
	 * @return The {@link Boolean} property
	 */
	public static Boolean getPropertyBoolean(String propertyName, boolean fallback)
	{
		try
		{
			return Boolean.parseBoolean(getProperty(propertyName));
		}
		catch (Exception e)
		{
			return fallback;
		}
	}

	/**
	 * Reads an {@link Long} property from the .properties file
	 *
	 * @param propertyName The property to read
	 * @return The {@link Long} property
	 */
	public static Long getPropertyLong(String propertyName)
	{
		return Long.parseLong(getProperty(propertyName));
	}

	/**
	 * Reads an {@link Long} property from the .properties file. The fallback will be used if there is no such property.
	 *
	 * @param propertyName The property to read
	 * @param fallback     The value that is returned if the property isn't set
	 * @return The {@link Long} property
	 */
	public static Long getPropertyLong(String propertyName, long fallback)
	{
		try
		{
			return Long.parseLong(getProperty(propertyName));
		}
		catch (Exception e)
		{
			return fallback;
		}
	}

	/**
	 * Reads an {@link Double} property from the .properties file
	 *
	 * @param propertyName The property to read
	 * @return The {@link Double} property
	 */
	public static Double getPropertyDouble(String propertyName)
	{
		return Double.parseDouble(getProperty(propertyName));
	}

	/**
	 * Reads an {@link Double} property from the .properties file. The fallback will be used if there is no such property.
	 *
	 * @param propertyName The property to read
	 * @param fallback     The value that is returned if the property isn't set
	 * @return The {@link Double} property
	 */
	public static Double getPropertyDouble(String propertyName, double fallback)
	{
		try
		{
			return Double.parseDouble(getProperty(propertyName));
		}
		catch (Exception e)
		{
			return fallback;
		}
	}

	/**
	 * Reads an {@link Float} property from the .properties file
	 *
	 * @param propertyName The property to read
	 * @return The {@link Float} property
	 */
	public static Float getPropertyFloat(String propertyName)
	{
		return Float.parseFloat(getProperty(propertyName));
	}

	/**
	 * Reads an {@link Float} property from the .properties file. The fallback will be used if there is no such property.
	 *
	 * @param propertyName The property to read
	 * @param fallback     The value that is returned if the property isn't set
	 * @return The {@link Float} property
	 */
	public static Float getPropertyFloat(String propertyName, float fallback)
	{
		try
		{
			return Float.parseFloat(getProperty(propertyName));
		}
		catch (Exception e)
		{
			return fallback;
		}
	}

	/**
	 * Reads a property from the .properties file and substitutes parameters
	 *
	 * @param propertyName The property to read
	 * @param parameters   The parameters to substitute
	 * @return The property or null if the property is not found
	 */
	public static String getProperty(String propertyName, Object... parameters)
	{
		String property = getProperty(propertyName);
		if (parameters.length > 0)
			return String.format(property, parameters);
		else
			return property;
	}

	public static <T> List<T> getPropertyList(String propertyName, Class<T> type)
	{
		List<T> result = new ArrayList<>();

		String line = getProperty(propertyName);

		if (!StringUtils.isEmpty(line))
		{
			for (String part : line.split(","))
			{
				if (type.equals(Integer.class))
					result.add(type.cast(Integer.parseInt(part)));
				else if (type.equals(String.class))
					result.add(type.cast(part));
				else if (type.equals(Double.class))
					result.add(type.cast(Double.parseDouble(part)));
				else if (type.equals(Float.class))
					result.add(type.cast(Float.parseFloat(part)));
			}
		}

		return result;
	}

	public static <T> Set<T> getPropertySet(String propertyName, Class<T> type)
	{
		Set<T> result = new HashSet<>();

		String line = getProperty(propertyName);

		if (!StringUtils.isEmpty(line))
		{
			for (String part : line.split(","))
			{
				if (type.equals(Integer.class))
					result.add(type.cast(Integer.parseInt(part)));
				else if (type.equals(String.class))
					result.add(type.cast(part));
				else if (type.equals(Double.class))
					result.add(type.cast(Double.parseDouble(part)));
				else if (type.equals(Float.class))
					result.add(type.cast(Float.parseFloat(part)));
			}
		}

		return result;
	}

	/**
	 * Returns the context path of the app i.e. given "http://ics.hutton.ac.uk:80/germinate-baz/genotype?dummyParam=3" it will return:
	 * "/germinate-baz"
	 *
	 * @param req The current request
	 * @return The context path of the app (see description) or <code>"null"</code> if req is <code>null</code>
	 */
	public static String getContextPath(HttpServletRequest req)
	{
		if (req == null)
			return "null";
		else
			return req.getContextPath();
	}

	/**
	 * This Runnable is used to constantly attempt to take from the watch queue, and will receive all events that are registered with the fileWatcher
	 * it is associated.
	 */
	private static class PropertyChangeListenerThread implements Runnable
	{

		/** the watchService that is passed in from above */
		private WatchService watcher;

		private boolean stopped = false;

		public PropertyChangeListenerThread(WatchService watcher)
		{
			this.watcher = watcher;
		}

		public void stop()
		{
			stopped = true;
		}

		/**
		 * In order to implement a file watcher, we loop forever ensuring requesting to take the next item from the file watchers queue.
		 */
		@Override
		public void run()
		{
			while (!stopped)
			{
				/* Wait for key to be signaled */
				WatchKey key;
				try
				{
					key = watcher.take();
				}
				catch (InterruptedException x)
				{
					return;
				}

                /*
				 * We have a polled event, now we traverse it and receive all
                 * the states from it
                 */
				for (WatchEvent<?> event : key.pollEvents())
				{
					WatchEvent.Kind<?> kind = event.kind();

					if (kind == StandardWatchEventKinds.OVERFLOW)
					{
						continue;
					}

                    /*
					 * The filename is the context of the event
                     */
					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path filename = ev.context();

					if (!stopped && PROPERTIES_FILE.equals(filename.getFileName().toString()))
						loadProperties();
				}

                /*
				 * Reset the key -- this step is critical if you want to receive
                 * further watch events. If the key is no longer valid, the
                 * directory is inaccessible so exit the loop.
                 */
				boolean valid = key.reset();

				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					throw new RuntimeException(e);
				}

				if (!valid)
				{
					break;
				}
			}
		}
	}
}
