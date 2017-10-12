<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

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

<!DOCTYPE html>
<html lang="en">

<head>
	<%@include file="common-scripts.html" %>

	<link rel="stylesheet" href="css/jquery.fancybox.css" type="text/css" media="screen"/>
	<script type="text/javascript" src="js/jquery.fancybox.js"></script>
</head>

<body>

<%@include file="header.html" %>

<div class="container">
	<div class="page-header">
		<h1>Crowdin
			<small>How to upload translations</small>
		</h1>
	</div>

	<div class="page-header">
		<h3>Upload English source file
			<small>(if changed)</small>
		</h3>
	</div>

	<p>Click on the "Settings" button below the Germinate logo.</p>

	<a href="img/crowdin/01-settings.png" class="thumbnail fancybox">
		<img src="img/crowdin/01-settings.png">
	</a>

	<p>Click on "Files" in the top navigation. Then select your project (e.g. "germinate-maize"). Click on the "Update" button for each of the
		original English source files you changed. In the file dialog that opens, select the file on your local machine and hit OK.</p>

	<a href="img/crowdin/02-upload-english.png" class="thumbnail fancybox">
		<img src="img/crowdin/02-upload-english.png">
	</a>

	<div class="page-header">
		<h3>Upload translations</h3>
	</div>

	<p>Click on "Translations" in the top navigation. You'll see an overview of the available target languages for Germinate. Select the language for
		which you wish to upload translations. For each of the files you've changed, click on the gear icon button and then on "Upload Translations".
		In the dialog that opens, ignore the checkboxes and click on "Choose File". Select the file containing the translation for the project and
		file you have selected.</p>

	<a href="img/crowdin/03-upload-translations.png" class="thumbnail fancybox">
		<img src="img/crowdin/03-upload-translations.png">
	</a>

	<div class="page-header">
		<h3>Upload English text as a translation
			<small>(if changed)</small>
		</h3>
	</div>

	<p>This step may be counter-intuitive. However, based on how the Crowdin API works, we need to define English as a target language as well.
		Otherwise, the Germinate Builder will not be able to get hold of the latest version of the English text.</p>

	<p>So, in case you changed the original English source text, please repeat the previous step for the target language "English, United
		Kingdom".</p><b>Please make sure to tick "Import Suggestions That Match the Original String" in the "Upload translations" dialog.</b></p>

	<a href="img/crowdin/04-upload-english-text.png" class="thumbnail fancybox">
		<img src="img/crowdin/04-upload-english-text.png">
	</a>

	<div class="page-header">
		<h1>Go to Crowdin</h1>
	</div>

	<p>Now that you know how to upload your translations, go ahead and upload them to the Crowdin website.</p>

	<p><a class="btn btn-primary" target="_blank" href="https://crowdin.com/project/germinate" role="button">Crowdin website »</a>
	</p>

</div>

<%@include file="footer.jsp" %>

</body>

</html>