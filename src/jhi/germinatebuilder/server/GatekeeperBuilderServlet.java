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

import org.apache.commons.lang3.*;

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;

import javax.mail.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import jhi.germinatebuilder.server.database.*;
import jhi.germinatebuilder.server.manager.*;
import jhi.germinatebuilder.server.uploader.s3.*;
import jhi.germinatebuilder.server.util.*;
import jhi.germinatebuilder.server.util.StringUtils;

/**
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/gatekeeper-builder"})
public class GatekeeperBuilderServlet extends HttpServlet
{
	public static final String QUERY_PARAM_TEST_BUILD = "test-build";
	public static final String QUERY_PARAM_S3_UPLOAD  = "s3-upload";
	public static final String QUERY_PARAM_EMAIL      = "email";
	public static final String QUERY_PARAM_UUID       = "uuid";
	public static final String QUERY_PARAM_INSTANCES  = "gatekeeper-instances";

	/**  */
	private static final SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");

	private static volatile boolean isRunning = false;
	private static volatile String  error     = null;
	private static volatile Process process;

	public static synchronized boolean isRunning()
	{
		return isRunning;
	}

	public static synchronized String getError()
	{
		return error;
	}

	@Override
	protected void doPost(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		synchronized (this)
		{
			if (isRunning)
			{
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				resp.setContentType("text/html");

				PrintWriter out = resp.getWriter();
				out.write("<h3>Builder already running</h3>");
				out.write("<p>The builder is currently busy building a Gatekeeper instance. This can take a couple of minutes depending on the upload speed.</p><p>Please try starting another build in a couple of minutes time.</p>");

				return;
			}

			isRunning = true;
			error = null;
		}

		User user = (User) req.getSession().getAttribute("user");

		/* Get the parameter values */
		final boolean testBuild = Boolean.parseBoolean(req.getParameter(QUERY_PARAM_TEST_BUILD));
		final boolean uploadToS3 = Boolean.parseBoolean(req.getParameter(QUERY_PARAM_S3_UPLOAD));
		final String[] instanceNames = req.getParameterValues(QUERY_PARAM_INSTANCES);
		final Set<String> emailList = new HashSet<>();
		final String[] emails = req.getParameterValues(QUERY_PARAM_EMAIL);

		if (!ArrayUtils.isEmpty(emails))
			emailList.addAll(Arrays.asList(emails));

		final String uuid = req.getParameter(QUERY_PARAM_UUID);

		final Logger logger = new Logger(req, "Gatekeeper", uuid);

		new Thread(() ->
		{
			File unzipped = null;

			try
			{
				List<Instance> instances = InstanceManager.getFromNames(instanceNames, InstanceType.GATEKEEPER, user);

				/* Run the build script */
				logger.log("<h3>Building Gatekeeper</h3>");
				build(instances, logger, testBuild, uploadToS3);
				logger.log("<p>Done</p>");
			}
			catch (Exception e)
			{
				e.printStackTrace();

				error = "<h3>Exception Details</h3>"
						+ "<h4>" + e.getClass().getName() + "</h4>"
						+ "<h4>" + e.getClass().getName() + "</h4>"
						+ "<p>" + e.getLocalizedMessage() + "</p>";
			}
			finally
			{
				logger.close();
				if (unzipped != null)
				{
					try
					{
						org.apache.commons.io.FileUtils.deleteDirectory(unzipped);
					}
					catch (Exception ignored)
					{

					}
				}

				Email.EmailProperties properties = Email.EmailProperties.get();

				if (properties != null)
					emailList.add(properties.mailAddress);

				/* Try to send the emails */
				if (!CollectionUtils.isEmpty(emailList))
				{
					File logFile = logger.getFileAsHtml();

					String date = SDF_DATETIME.format(new Date(System.currentTimeMillis()));

					for (String email : emailList)
					{
						if (StringUtils.isEmpty(email))
							continue;

						try
						{
							if (StringUtils.isEmpty(error))
								Email.send(properties, email, "Gatekeeper Builder report (successful)", "<p>Hello</p>%s<p>The latest Gatekeeper build was successful.</p><p>Please find attached the log file for the latest Germinate build (" + date + ").</p><p></p><p>The Germinate Team</p>", "Gatekeeper", logFile);
							else
								Email.send(properties, email, "Gatekeeper Builder report (failed)", "<p>Hello</p>%s<p>The latest Gatekeeper build failed.</p><p>Please find attached the log file for the latest Germinate build (" + date + ").</p><p></p><p>The Germinate Team</p>", "Gatekeeper", logFile);
						}
						catch (MessagingException e)
						{
							e.printStackTrace();

							error = "<h3>Failed to send email</h3>"
									+ "<h4>" + e.getClass().getName() + "</h4>"
									+ "<p>" + e.getLocalizedMessage() + "</p>";
						}
					}
				}

				isRunning = false;
				process = null;
			}
		}).start();

		new Thread(() ->
		{
			/* Delete files older than 24 hours */
			long purgeTime = System.currentTimeMillis() - (24 * 1000 * 60 * 60);

			File[] files = FileUtils.getTemporaryFileFolder(req, "Gatekeeper").listFiles();

			if (files != null)
			{
				for (File file : files)
				{
					if (file.lastModified() < purgeTime)
						file.delete();
				}
			}
		}).start();
	}

	/**
	 * Starts the actual build process of Germinate
	 *
	 * @param instances The List of GerminateInstances
	 * @param logger    The logger instance
	 * @param testBuild Should the resulting war file be deployed or just built?
	 * @throws IOException          Thrown if the creation of files fails
	 * @throws InterruptedException Thrown if ???
	 */
	private void build(List<Instance> instances, Logger logger, boolean testBuild, boolean uploadToS3) throws IOException, InterruptedException
	{
		File warFolder = new File(PropertyReader.getProperty(PropertyReader.PATH_GERMINATE), "wars");

		if (!warFolder.exists())
			warFolder.mkdirs();

		/* For each instance */
		for (Instance instance : instances)
		{
			logger.log("<h4>Building: \"")
				  .log(instance.getDisplayName())
				  .log("\"</h4>");

			/* Copy the config file to the base directory */
			Path source = FileUtils.concat(PropertyReader.getProperty(PropertyReader.PATH_GATEKEEPER), "instances", instance.getInstanceName(), "config.properties").toPath();
			Path target = new File(PropertyReader.getProperty(PropertyReader.PATH_GATEKEEPER), "config.properties").toPath();
			FileUtils.copy(source, target);

			/* Build up the ant command line arguments */
			List<String> arguments = new ArrayList<>();
			arguments.add(PropertyReader.getProperty(PropertyReader.PATH_ANT));
			arguments.add("-f");
			arguments.add(new File(PropertyReader.getProperty(PropertyReader.PATH_GATEKEEPER), "build.xml").getAbsolutePath());
			arguments.addAll(instance.getAntParameters());

			/* Add the target if required */
			if (testBuild)
				arguments.add("testbuild");

			logger.log("<h5>")
				  .log(CollectionUtils.join(getSafeArguments(arguments), " "))
				  .log("</h5>")
				  .log("<pre>");

			/* Create the process and listen to the output */
			ProcessBuilder builder = new ProcessBuilder(arguments);
			builder.redirectErrorStream(true);
			process = builder.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			for (String line; (line = br.readLine()) != null; )
			{
				logger.log(StringEscapeUtils.escapeHtml4(line))
					  .log("<br />");
			}

			logger.log("</pre>");

			if (process != null)
			{
				process.waitFor();

				if (process.exitValue() != 0)
				{
					throw new IOException("Build script failed. See previous error messages");
				}
			}

			if (uploadToS3)
			{
				File file = new File(PropertyReader.getProperty(PropertyReader.PATH_GATEKEEPER), instance.getDeployName() + ".war");
				logger.log("<h5>Uploading war file to S3 folder</h5>");

				UploadObjectSingleOperation.upload(logger, file);
			}
		}
	}

	/**
	 * Returns the ant command line arguments less the usernames/passwords
	 *
	 * @param input The List of individual arguments
	 * @return The List of individual arguments with censored usernames/passwords
	 */
	private List<String> getSafeArguments(List<String> input)
	{
		List<String> output = new ArrayList<>();

		for (String value : input)
		{
			if (value.contains("username") || value.contains("password"))
			{
				String[] parts = value.split("=");
				value = parts[0] + "=***HIDDEN***";
			}

			output.add(value);
		}

		return output;
	}
}
