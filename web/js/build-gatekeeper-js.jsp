<%@ page language ="java" contentType ="application/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
	<%@ page import="jhi.germinatebuilder.server.*" %>

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

	function getStorage(key_prefix) {
		if (window.localStorage) {
			return {
				set: function (id, data) {
					localStorage.setItem(key_prefix + id, data);
				},
				get: function (id) {
					return localStorage.getItem(key_prefix + id);
				}
			};
		} else {
			return {
				set: function (id, data) {
					document.cookie = key_prefix + id + '=' + encodeURIComponent(data);
				},
				get: function (id) {
					var cookies = document.cookie, parsed = {};
					cookies.replace(/([^=]+)=([^;]*);?\s*/g, function (whole, key, value) {
						parsed[key] = decodeURI(value);
					});
					return parsed[key_prefix + id];
				}
			};
		}
	}

	function guid() {
		function s4() {
			return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
		}

		return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
	}

	function scrollToBottom(id) {
		$(id).scrollTop($(id)[0].scrollHeight);
	}

	$(document).ready(function () {
		// Restore checkbox state on page load
		$('#builder-form').find('input:checkbox').each(function () {
			// get storage
			var storedData = getStorage('germinate-builder-');

			// for each checkbox, load the state and check it if state is "checked"
			var state = storedData.get(this.id);

			if (state == 'checked') {
				$(this).attr('checked', 'checked');
			}
		});

		// Add a new email field
		$('#add-email').click(function (event) {
			event.preventDefault();

			$('#emails').append('<input type="email" class="form-control" name="<%= GerminateBuilderServlet.QUERY_PARAM_EMAIL %>" placeholder="Email">');
		});

		// Listen for form submit events
		$('#builder-form').submit(function (event) {

			// Prevent the default behaviour
			event.preventDefault();

			var selectedInstances = $('select[name=<%= GatekeeperBuilderServlet.QUERY_PARAM_INSTANCES %>] option:selected').size();

			// Check if at least one instance is selected
			if (selectedInstances < 1) {
				$('#notifications').empty().append('<div class="alert alert-warning" role="alert">Please select at least one Gatekeeper instance.</div>');
			}
			else {

				// get storage
				var storedData = getStorage('germinate-builder-');

				// save checkbox states to cookie
				$('#builder-form').find('input:checkbox').each(function () {
					// for each checkbox, save the state in storage with this.id as the key
					storedData.set(this.id, $(this).is(':checked') ? 'checked' : 'not');
				});

				// Set the hidden UUID
				$("#uuid").val(guid());

				// Disable the select button
				$('#submit').prop('disabled', true);

				var url = $(this).closest('form').attr('action');
				var data = $(this).closest('form').serialize();

				// Send GA event
				ga('send', 'event', 'Build', 'build', data);

				// Set up an ajax call to the servlet
				$.ajax({
					url: url,
					type: 'post',
					data: data,
					beforeSend: function () {
						$('#notifications').empty();
						$('#progress').show();
					},
					success: function () {
						// Check the progress after one second
						setTimeout(updateProgress, 1000);
					},
					error: function (request) {
						updateProgress('<div class="alert alert-danger" role="alert">' + request.responseText + '</div>');
						$('#submit').removeAttr('disabled');
					}
				});


				// Function that checks the progress
				function updateProgress(postfix) {
					$.ajax({
						url: './gatekeeper-progress',
						type: 'get',
						data: data,
						success: function (data) {
							if (data.endsWith("<%= GerminateProgressServlet.STATUS_DONE %>")) // Done
							{
								$('#progress').hide();
								$('#submit').removeAttr('disabled');
								$('#notifications').empty().append('<div class="alert alert-success" role="alert">' + data + '</div>');
							}
							else if (data.indexOf("BUILD FAILED") != -1) {
								$('#progress').hide();
								$('#submit').removeAttr('disabled');
								$('#notifications').empty().append('<div class="alert alert-danger" role="alert">' + data + '</div>');
							}
							else // Continue
							{
								setTimeout(updateProgress, 1000);
								$('#notifications').empty().append('<div class="alert alert-success" role="alert">' + data + '</div>');
							}

							if (typeof postfix !== 'undefined')
								$('#notifications').append(postfix);

							scrollToBottom('#notifications > div');
						},
						error: function (request) {
							$('#progress').hide();
							$('#submit').removeAttr('disabled');

							$('#notifications').empty().append('<div class="alert alert-danger" role="alert">' + request.responseText + '</div>');

							if (typeof postfix !== 'undefined')
								$('#notifications').append(postfix);

							scrollToBottom('#notifications > div');
						}
					});
				}
			}
		});
	});