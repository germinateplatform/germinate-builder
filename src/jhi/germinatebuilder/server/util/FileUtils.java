/**
 * Copyright 2017 Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */

package jhi.germinatebuilder.server.util;

import java.io.*;
import java.nio.file.*;

import javax.servlet.http.*;

/**
 * {@link FileUtils} is a utility class for {@link File} interactions
 *
 * @author Sebastian Raubach
 */
public class FileUtils
{
	public static File concat(String base, String... subs)
	{
		File result = new File(base);

		if (subs != null && subs.length > 0)
		{
			for (String p : subs)
			{
				result = new File(result, p);
			}
		}

		return result;
	}

	public static void copy(Path source, Path target) throws IOException
	{
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	public static void move(Path source, Path target) throws IOException
	{
		try
		{
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		}
		catch (AtomicMoveNotSupportedException | UnsupportedOperationException e)
		{
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * Returns the path to the temporary file folder of the application
	 *
	 * @param req The {@link HttpServletRequest}
	 * @return The path to the temporary file folder of the application
	 */
	public static File getTemporaryFileFolder(HttpServletRequest req, String path)
	{
		makeSureTempFolderExists(req, path);

		return new File(new File(System.getProperty("java.io.tmpdir") + PropertyReader.getContextPath(req)), path);
	}

	/**
	 * Makes sure that the temporary folder of this instance exists
	 *
	 * @param req The request
	 */
	private static void makeSureTempFolderExists(HttpServletRequest req, String path)
	{
		File file = new File(new File(System.getProperty("java.io.tmpdir") + PropertyReader.getContextPath(req) + File.separator), path);

		if (!file.exists())
			file.mkdirs();
	}
}
