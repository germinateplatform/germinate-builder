<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="jhi.germinatebuilder.server.database.*" %>

<%--
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

<%
	Calendar cal = Calendar.getInstance();
	cal.setTime(new Date(System.currentTimeMillis()));

	int year = cal.get(Calendar.YEAR);

	String yearString = year == 2015 ? "2015" : "2015-" + year;

	User theUser = (User) request.getSession().getAttribute("user");
%>

<style>
	.footer form {
		display: inline-block;
	}
</style>

<!-- Cookie modal -->
<div class="modal fade" id="cookie" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel">Cookie Policy</h4>
			</div>
			<div class="modal-body">
				<p>This website's sole use of cookies (local storage) is to remember user settings between visits.</p>

				<p>You can reject these cookies if you wish (and it won't affect your usage of the site) by blocking them using the options in your
					browser.</p>

				<p>You may also find the following links useful:</p>
				<ul>
					<li><a href="http://www.ico.org.uk/for_organisations/privacy_and_electronic_communications/the_guide/cookies.aspx">ICO Cookie
						Regulations
						and the EU Cookie Law</a>
					</li>
				</ul>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>

<!-- About modal -->
<div class="modal fade" id="about" tabindex="-1" role="dialog" aria-labelledby="myModalLabel2">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel2">About</h4>
			</div>
			<div class="modal-body">
				<p>The Germinate Builder was written, designed and developed by Sebastian Raubach.</p>
				<p>You can contact us by email at <a
						href="mailto:sebastian.raubach@hutton.ac.uk">sebastian.raubach@hutton.ac.uk</a> or <a href="mailto:germinate@hutton.ac.uk">germinate@hutton.ac.uk</a>.
				</p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>

<footer class="footer">
	<div class="container">
		<div class="float-left truncate seventy">&copy; Information & Computational Sciences, JHI <%= yearString %>
		</div>
		<div class="float-right truncate thirty">

			<%
				if (theUser != null)
				{
			%>

			<form id="logout-form" action="logout" method="post">
				<!-- Your Form -->
				<a href="javascript:{}" onclick="document.getElementById('logout-form').submit(); return false;">Logout</a>
			</form>
			<span>&nbsp;|&nbsp;</span>

			<%
				}
			%>

			<a href="#cookie" data-toggle="modal" data-target="#cookie">Cookie Policy</a>
			<span>&nbsp;|&nbsp;</span>
			<a href="#about" data-toggle="modal" data-target="#about">About</a>
		</div>
	</div>

</footer>