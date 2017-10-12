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

package jhi.germinatebuilder.server.util;

/**
 * Crowdin is a utility class containing methods that make the interaction with the Crowdin API easier.
 *
 * @author Sebastian Raubach
 */
public class Crowdin
{
	public static final String BASE_URL = "https://api.crowdin.com/api/project/%s/%s?key=%s";
	private static      String project  = "germinate";

	public static String getUrl(Action action, String key)
	{
		return String.format(BASE_URL, project, action.getUrl(), key);
	}

	public enum Action
	{
		EXPORT("export"),
		DOWNLOAD("download", "all.zip");

		private String url      = null;
		private String appendix = null;

		Action(String url)
		{
			this.url = url;
		}

		Action(String url, String appendix)
		{
			this(url);
			this.appendix = appendix;
		}

		public String getUrl()
		{
			return url + (StringUtils.isEmpty(appendix) ? "" : "/" + appendix);
		}
	}
}
