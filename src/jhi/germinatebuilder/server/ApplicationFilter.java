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
import java.util.logging.*;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import jhi.germinatebuilder.server.util.*;

@WebFilter("*.jsp")
public class ApplicationFilter implements Filter
{
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException
	{
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(false);

		/* Don't cache .jsp files */
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.

		String server = PropertyReader.getProperty(PropertyReader.GATEKEEPER_SERVER);
		String database = PropertyReader.getProperty(PropertyReader.GATEKEEPER_DATABASE);

		if (StringUtils.isEmpty(server, database))
		{
			// No Gatekeeper configured, so allow anyone in.
			Logger.getLogger("").log(Level.WARNING, "Gatekeeper configuration not found, allowing public access!");
			chain.doFilter(request, response);
		}
		else
		{
			String loginURI = request.getContextPath() + "/login";

			boolean loggedIn = session != null && session.getAttribute("user") != null;
			boolean loginRequest = request.getRequestURI().startsWith(loginURI);

			if (loggedIn || loginRequest)
			{
				chain.doFilter(request, response);
			}
			else
			{
				response.sendRedirect(loginURI);
			}
		}
	}

	@Override
	public void destroy()
	{
	}
}