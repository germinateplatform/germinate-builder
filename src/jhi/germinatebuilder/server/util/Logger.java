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

public class Logger
{
	private StringBuilder builder = new StringBuilder();
	private File           file;
	private BufferedWriter bw;

	public Logger(HttpServletRequest req, String path, String uuid) throws IOException
	{
		file = new File(FileUtils.getTemporaryFileFolder(req, path), uuid + ".log");
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
	}

	public Logger log(String logEntry) throws IOException
	{
		builder.append(logEntry);
		bw.write(logEntry);
		bw.flush();

		return this;
	}

	public Logger log(Object object) throws IOException
	{
		return log(String.valueOf(object));
	}

	public String getContent()
	{
		return builder.toString();
	}

	public void close()
	{
		try
		{
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public File getFile()
	{
		return file;
	}

	public File getFileAsHtml()
	{
		Path source = file.toPath();
		Path target = new File(file.getAbsolutePath() + ".html").toPath();

		try
		{
			Files.copy(source, target);
			return target.toFile();
		}
		catch (IOException e)
		{
			return file;
		}
	}
}