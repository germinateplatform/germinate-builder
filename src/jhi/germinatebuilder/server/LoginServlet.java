/**
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

import jhi.database.shared.exception.*;
import jhi.germinatebuilder.server.database.*;
import jhi.germinatebuilder.server.manager.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		sendRedirect(request, response, false);
	}

	private void sendRedirect(HttpServletRequest request, HttpServletResponse response, boolean showError) throws ServletException, IOException
	{
		String path = "/login.jsp?show-error=" + showError;
		request.getRequestDispatcher(path).forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String username = request.getParameter("germinate-builder-username");
		String password = request.getParameter("germinate-builder-password");

		try
		{
			User user = UserManager.getForUsernamePassword(username, password);

			if (user != null)
			{
				request.getSession().setAttribute("user", user);
				response.sendRedirect(request.getContextPath() + "/");
			}
			else
			{
				sendRedirect(request, response, true);
			}
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
			sendRedirect(request, response, true);
		}
	}

}