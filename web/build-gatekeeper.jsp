<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jhi.database.shared.exception.*" %>
<%@ page import="jhi.germinatebuilder.server.*" %>
<%@ page import="jhi.germinatebuilder.server.manager.*" %>
<%@ page import="jhi.germinatebuilder.server.util.*" %>

<%--~
  ~  Copyright 2017 Sebastian Raubach and Paul Shaw from the
  ~  Information and Computational Sciences Group at JHI Dundee
  ~
  ~  Licensed under the Apache License, Version 2.0 (the "License");
  ~  you may not use this file except in compliance with the License.
  ~  You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~  Unless required by applicable law or agreed to in writing, software
  ~  distributed under the License is distributed on an "AS IS" BASIS,
  ~  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~  See the License for the specific language governing permissions and
  ~  limitations under the License.
  --%>

<%
	boolean buildEnabled = PropertyReader.getPropertyBoolean(PropertyReader.BUILD_ENABLED, true);

	String emailAddress = PropertyReader.getProperty(PropertyReader.EMAIL_ADDRESS);
	String emailServer = PropertyReader.getProperty(PropertyReader.EMAIL_SERVER);
	String emailUsername = PropertyReader.getProperty(PropertyReader.EMAIL_USERNAME);

	boolean emailConfigured = !StringUtils.isEmpty(emailAddress, emailServer, emailUsername);

	boolean s3UploadEnabled = PropertyReader.getPropertyBoolean(PropertyReader.S3_UPLOAD_ENABLED, false);

	String svnInfo = VersionControlUtils.getInfo(PropertyReader.getProperty(PropertyReader.PATH_GATEKEEPER));
%>

<!DOCTYPE html>
<html lang="en">

<head>
	<%@include file="common-scripts.html" %>

	<script src="js/build-gatekeeper-js.jsp"></script>
	<link rel="stylesheet" href="css/builder.css">
</head>

<body>

<%@include file="header.html" %>

<div class="container">
	<div id="notifications">
	</div>
</div>

<div class="container" style="position: relative;">
	<div id="progress">
		<div class="progress-indicator-indeterminate"></div>
		<div class="progress-indicator-background"></div>
	</div>

	<div class="page-header">
		<h1>Build Gatekeeper</h1>
	</div>

	<%
		if (!StringUtils.isEmpty(svnInfo))
		{
	%>

	<div class="panel-group" id="svn-accordion" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingOne">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#svn-accordion" href="#collapseOne" aria-expanded="false"
					   aria-controls="collapseOne">
						Show Gatekeeper version information
					</a>
				</h4>
			</div>
			<div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
				<div class="panel-body">
					<%= svnInfo %>
				</div>
			</div>
		</div>
	</div>

	<%
		}
	%>

	<%
		if (buildEnabled)
		{
	%>

	<form id="builder-form" action="gatekeeper-builder">

		<div class="form-group">
			<label for="germinate-instances">Select which Germinate instances to build</label>

			<select multiple class="form-control" id="germinate-instances" name="<%= GatekeeperBuilderServlet.QUERY_PARAM_INSTANCES %>">
				<%
					try
					{
						User user = (User) request.getSession().getAttribute("user");

						for (Instance instance : InstanceManager.getAllForType(InstanceType.GATEKEEPER, user))
						{
							if (instance.isEnabled())
							{
				%>
				<option value="<%= instance.getInstanceName() %>"><%= instance.getDisplayName() %>
				</option>
				<%
							}
						}
					}
					catch (DatabaseException e)
					{
						e.printStackTrace();
					}
				%>
			</select>
		</div>

		<div class="form-group">
			<label for="test-build">Check if you want to run a test build (A test build will only build but not deploy the application)</label>

			<div class="checkbox">
				<label> <input type="checkbox" id="test-build" name="<%= GerminateBuilderServlet.QUERY_PARAM_TEST_BUILD %>" value="true">Test
					build?</label>
			</div>
		</div>

		<% if (s3UploadEnabled)
		{ %>

		<div class="form-group">
			<label for="test-build">Check if you want to upload the final .war file to the Amazon S3 AWS storage</label>

			<div class="checkbox">
				<label> <input type="checkbox" id="s3-upload" name="<%= GerminateBuilderServlet.QUERY_PARAM_S3_UPLOAD %>" value="true">Upload .war
					files?</label>
			</div>
		</div>

		<% } %>

		<% if (emailConfigured)
		{ %>

		<div class="form-group">
			<label for="email">Send build report to</label>

			<div id="emails">
				<input type="email" class="form-control" id="email" name="<%= GerminateBuilderServlet.QUERY_PARAM_EMAIL %>" placeholder="Email">
			</div>
			<p><a id="add-email" class="btn btn-default" href="#" role="button">Add recipient</a></p>
		</div>

		<% } %>

		<input type="text" id="uuid" name="<%= GerminateBuilderServlet.QUERY_PARAM_UUID %>" hidden>

		<button id="submit" type="submit" class="btn btn-primary">Build now »</button>
	</form>
	<% }
	else
	{ %>
	<h3>The build feature has temporarily been disabled.</h3>
	<% } %>

</div>

<%@include file="footer.jsp" %>

</body>

</html>