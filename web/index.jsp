<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jhi.database.shared.exception.*" %>
<%@ page import="jhi.germinatebuilder.server.manager.*" %>
<%@ page import="jhi.germinatebuilder.server.util.*" %>

<%--
  ~ Copyright 2017 Information and Computational Sciences,
  ~ The James Hutton Institute.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~  http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%
	boolean buildEnabled = PropertyReader.getPropertyBoolean(PropertyReader.BUILD_ENABLED, true);
	User user = (User) request.getSession().getAttribute("user");
	List<Instance> instances = new ArrayList<>();

	try
	{
		instances = InstanceManager.getAll(user);
	}
	catch (DatabaseException e)
	{
		e.printStackTrace();
	}
%>

<!DOCTYPE html>
<html lang="en">

<head>
	<%@include file="common-scripts.html" %>
</head>

<body>

<%@include file="header.html" %>

<div id="main" class="container">
	<div class="row">
		<div class="col-xs-12 col-sm-6 col-md-3">
			<h3>Upload translations</h3>

			<p>The first step is to upload your translation files to Crowdin.</p>

			<p>Click on the button below to get a detailed information on how to upload the translations.</p>

			<p><a class="btn btn-primary" href="crowdin.jsp" role="button">Crowdin tutorial »</a>
			</p>
		</div>
		<div class="col-xs-12 col-sm-6 col-md-3">
			<h3>Build Germinate</h3>

			<p>In the second step, we build Germinate locally using the translation files pulled from Crowdin.</p>

			<p>The compilation process can take a couple of minutes, so please be patient.</p>

			<p><a class="btn btn-primary" <%= buildEnabled ? "" : "disabled" %> href="build-germinate.jsp" role="button" data-toggle="tooltip"
				  data-placement="bottom" title="<%= buildEnabled ? "" : "The build feature has temporarily been disabled." %>">Start building »</a>
			</p>
		</div>
		<div class="col-xs-12 col-sm-6 col-md-3">
			<h3>Build Gatekeeper</h3>

			<p>In the third step, we build Germinate Gatekeeper locally.</p>

			<p>The compilation process can take a couple of minutes, so please be patient.</p>

			<p><a class="btn btn-primary" <%= buildEnabled ? "" : "disabled" %> href="build-gatekeeper.jsp" role="button" data-toggle="tooltip"
				  data-placement="bottom" title="<%= buildEnabled ? "" : "The build feature has temporarily been disabled." %>">Start building »</a>
			</p>
		</div>
		<div class="col-xs-12 col-sm-6 col-md-3">
			<h3>Review results</h3>

			<p>After Germinate has been built, we deploy it to your server.</p>

			<p>All you have to do is follow the link below and check it out.</p>

			<p><a class="btn btn-primary" href="#" role="button" data-toggle="modal" data-target="#instance-modal">View results »</a>
			</p>
		</div>
	</div>
</div>

<!-- Modal -->
<div class="modal fade" id="instance-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel">Germinate instances</h4>
			</div>
			<div class="modal-body">
				<div class="list-group">
					<%
						boolean atLeastOne = false;
						for (Instance inst : instances)
						{
							if (!StringUtils.isEmpty(inst.getUrl()))
							{
								if (StringUtils.isEmpty(inst.getUrlDescription()))
									out.print("<a target='_blank' href='" + inst.getUrl() + "' class='list-group-item close-modal'>" + inst.getDisplayName() + "</a>");
								else
									out.print("<a target='_blank' href='" + inst.getUrl() + "' class='list-group-item close-modal'>" + inst.getUrlDescription() + "</a>");
								atLeastOne = true;
							}
						}

						if (!atLeastOne)
						{
							out.print("No Germinate instance with valid URL found.");
						}
					%>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>

<%@include file="footer.jsp" %>

</body>

</html>