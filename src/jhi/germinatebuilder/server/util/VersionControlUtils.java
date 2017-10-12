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

/**
 * @author Sebastian Raubach
 */
public class VersionControlUtils
{
	public static String getInfo(String path)
	{
		String result = null;

		if (!StringUtils.isEmpty(path))
		{
			File folder = new File(path);

			if (folder.exists() && folder.isDirectory())
			{
				try
				{
					/* Check the SVN information */
					result = getSvn(folder);
				}
				catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
				}

				try
				{
					/* If there is no SVN information, try checking Git */
					if (result == null)
						result = getGit(folder);
				}
				catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		if (result == null)
			result = "VCS info not available";

		return result;
	}

	private static String getGit(File folder) throws InterruptedException, IOException
	{
		StringBuilder builder = new StringBuilder();

		ProcessBuilder pb = new ProcessBuilder("git", "--git-dir=" + folder.getAbsolutePath() + "/.git", "remote", "show", "origin");
		Process process = pb.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		for (String line; (line = br.readLine()) != null; )
		{
			builder.append("<p>")
				   .append(line)
				   .append("</p>");
		}

		process.waitFor();
		if (process.exitValue() == 0)
		{
			return builder.toString();
		}
		else
		{
			throw new IOException("No Git information found");
		}
	}

	private static String getSvn(File folder) throws InterruptedException, IOException
	{
		StringBuilder builder = new StringBuilder();

		ProcessBuilder pb = new ProcessBuilder("svn", "info", folder.getAbsolutePath());
		Process process = pb.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		for (String line; (line = br.readLine()) != null; )
		{
			builder.append("<p>")
				   .append(line)
				   .append("</p>");
		}

		process.waitFor();
		if (process.exitValue() == 0)
		{
			return builder.toString();
		}
		else
		{
			throw new IOException("No SVN information found");
		}
	}
}
