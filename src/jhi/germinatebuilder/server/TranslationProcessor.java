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

package jhi.germinatebuilder.server;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import jhi.germinatebuilder.server.database.*;

/**
 * The TranslationProcessor is used to move and rename the translation files to the target locations
 *
 * @author Sebastian Raubach
 */
public class TranslationProcessor
{
	private File sourcePath;
	private File germinatePath;
	private List<Instance> germinateInstancesInput = new ArrayList<>();

	/**
	 * Creates a new instance of the TranslationProcessor
	 *
	 * @param sourcePath              The source directory containing the translation files
	 * @param germinatePath           The path to the Germinate installation
	 * @param germinateInstancesInput The List of GerminateInstances to process
	 */
	public TranslationProcessor(File sourcePath, File germinatePath, List<Instance> germinateInstancesInput)
	{
		this.sourcePath = sourcePath;
		this.germinatePath = germinatePath;
		this.germinateInstancesInput = germinateInstancesInput;
	}

	/**
	 * Starts the extraction
	 *
	 * @throws IOException Thrown if copying the files fails
	 */
	public String run() throws IOException
	{
		StringBuilder builder = new StringBuilder();
		Map<String, InstanceData> instanceToLocale = getInstanceToLocaleData();

		for (String instance : instanceToLocale.keySet())
		{
			File i18n = new File(new File(new File(germinatePath, "instance-stuff"), instance), "i18n");

			InstanceData instanceData = instanceToLocale.get(instance);

			for (String locale : instanceData.localeToFile.keySet())
			{
				for (File file : instanceData.localeToFile.get(locale))
				{
					String name = file.getName();

					locale = locale.replace("-", "_");

					int index = name.lastIndexOf(".");

					if (!locale.equalsIgnoreCase("en_gb"))
						name = name.substring(0, index) + "_" + locale + name.substring(index);

					File target = new File(i18n, name);

					builder.append("     [copy] Copying translation file: '")
						   .append(file.getAbsolutePath())
						   .append("' to '")
						   .append(target.getAbsolutePath())
						   .append("'<br />");

					Files.copy(file.toPath(), target.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}

		if (builder.length() > 0)
			builder.insert(0, "<pre>")
				   .append("</pre>");
		return builder.toString();
	}

	private Map<String, InstanceData> getInstanceToLocaleData()
	{
		Map<String, InstanceData> instanceData = new HashMap<>();

		String[] locales = sourcePath.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return new File(dir, name).isDirectory();
			}
		});

		for (String locale : locales)
		{
			File localeFolder = new File(sourcePath, locale);

			String[] instances = localeFolder.list(new FilenameFilter()
			{
				@Override
				public boolean accept(File dir, String name)
				{
					return new File(dir, name).isDirectory();
				}
			});

			for (String instance : instances)
			{
				boolean found = false;

				for (Instance germinateInstance : germinateInstancesInput)
				{
					if (germinateInstance.getInstanceName().equals(instance))
					{
						found = true;
						break;
					}
				}

				if (!found)
					continue;

				File instanceFolder = new File(localeFolder, instance);

				String[] files = instanceFolder.list(new FilenameFilter()
				{
					@Override
					public boolean accept(File dir, String name)
					{
						return new File(dir, name).isFile() && name.startsWith("Text") && name.endsWith(".properties");
					}
				});

				InstanceData data = instanceData.get(instance);

				if (data == null)
				{
					data = new InstanceData();
					data.name = instance;
				}

				for (String file : files)
					data.addFile(locale, new File(instanceFolder, file));

				instanceData.put(instance, data);
			}
		}

		return instanceData;
	}

	private static class InstanceData
	{
		String name;
		Map<String, List<File>> localeToFile = new HashMap<>();

		void addFile(String locale, File file)
		{
			List<File> files = localeToFile.get(locale);

			if (files == null)
				files = new ArrayList<>();

			files.add(file);

			localeToFile.put(locale, files);
		}

		@Override
		public String toString()
		{
			return "InstanceData{" +
					"name='" + name + '\'' +
					", localeToFile=" + localeToFile +
					'}';
		}
	}
}