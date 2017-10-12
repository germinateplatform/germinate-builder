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

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import jhi.germinatebuilder.server.util.*;

/**
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate-progress"})
public class GerminateProgressServlet extends HttpServlet
{
	public static final String QUERY_PARAM_UUID = "uuid";
	public static final String STATUS_DONE      = "DONE";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String uuid = req.getParameter(QUERY_PARAM_UUID);

		resp.setContentType("text/html");

		PrintWriter out = resp.getWriter();

		/* Check if we have a uuid value */
		if (!StringUtils.isEmpty(uuid))
		{
			/* Get the associated log file */
			File logFile = new File(FileUtils.getTemporaryFileFolder(req, "Germinate"), uuid + ".log");

			/* Check if it exists */
			if (logFile.exists())
			{
				/* Read the log file and write it to the output */
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "UTF-8")))
				{
					for (String line; (line = reader.readLine()) != null; )
					{
						out.write(line);
					}
				}
			}
		}

		if (!StringUtils.isEmpty(GerminateBuilderServlet.getError()))
		{
			out.write(GerminateBuilderServlet.getError());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		else if (!GerminateBuilderServlet.isRunning())
		{
			out.write(STATUS_DONE);
			resp.setStatus(HttpServletResponse.SC_OK);
		}
	}
}
