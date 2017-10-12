<%--~
  ~  Copyright 2017 Sebastian Raubach from the Information
  ~  and Computational Sciences Group at JHI Dundee
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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>
	<%@include file="common-scripts.html" %>

	<style>
		.form-signin {
			max-width: 330px;
			margin: 0 auto;
		}

		.form-signin input[type="text"] {
			margin-bottom: -1px;
			border-bottom-right-radius: 0;
			border-bottom-left-radius: 0;
		}

		.form-signin input[type="password"] {
			margin-bottom: 10px;
			border-top-left-radius: 0;
			border-top-right-radius: 0;
		}
	</style>
</head>

<body>

<%@include file="header.html" %>

<%
	boolean error = Boolean.parseBoolean(request.getParameter("show-error"));
%>

<div class="container">
	<%
		if (error)
		{
	%>
	<div class="alert alert-danger" role="alert">Invalid username or password.</div>
	<%
		}
	%>
	<div class="well">
		<div class="form-signin">
			<form method="post" action="login">
				<div class="<%= error ? "has-error" : "" %>">
					<h2>Please log in</h2>
					<div>
						<label class="sr-only" for="login-form-username">Username</label>
						<input type="text" id="login-form-username" name="germinate-builder-username" class="form-control" required="" autofocus=""
							   placeholder="Username">
					</div>
					<div>
						<label class="sr-only" for="login-form-password">Password</label>
						<input type="password" id="login-form-password" name="germinate-builder-password" class="form-control" required=""
							   placeholder="Password">
					</div>
					<button type="submit" class="btn btn-lg btn-block btn-primary">Login</button>
				</div>
			</form>
		</div>
	</div>
</div>


<%@include file="footer.jsp" %>

</body>

</html>