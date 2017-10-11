/**
 * Copyright 2017 Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */

package jhi.germinatebuilder.server.database;

import java.util.*;

import jhi.database.server.*;
import jhi.database.server.parser.*;
import jhi.database.shared.exception.*;
import jhi.database.shared.util.*;

/**
 * @author Sebastian Raubach
 */
public class Instance extends DatabaseObject
{
	public static final String ID               = "id";
	public static final String DEPLOY_NAME      = "deploy_name";
	public static final String INSTANCE_NAME    = "instance_name";
	public static final String DISPLAY_NAME     = "display_name";
	public static final String URL              = "url";
	public static final String URL_DESCRIPTION  = "url_description";
	public static final String BROWSER_OPT      = "browser_opt";
	public static final String COMPILE_OPT      = "compile_opt";
	public static final String GOOGLE_ANALYTICS = "google_analytics";
	public static final String TOMCAT_URL       = "tomcat_url";
	public static final String TOMCAT_USERNAME  = "tomcat_username";
	public static final String TOMCAT_PASSWORD  = "tomcat_password";
	public static final String ENABLED          = "enabled";
	public static final String INSTANCE_TYPE_ID = "type_id";

	private static final String KEY_DEPLOY_NAME    = "project.name";
	private static final String KEY_INSTANCE_STUFF = "instance.files";

	private static final String KEY_BROWSER_OPT      = "settings.browseropt";
	private static final String KEY_COMPILE_OPT      = "settings.compileopt";
	private static final String KEY_GOOGLE_ANALYTICS = "settings.googleanalytics";

	private static final String KEY_TOMCAT_URL      = "tomcat.manager.url";
	private static final String KEY_TOMCAT_USERNAME = "tomcat.manager.username";
	private static final String KEY_TOMCAT_PASSWORD = "tomcat.manager.password";

	private String       deployName;
	private String       instanceName;
	private String       displayName;
	private String       url;
	private String       urlDescription;
	private String       browserOpt;
	private boolean      compileOpt;
	private boolean      googleAnalytics;
	private String       tomcatUrl;
	private String       tomcatUsername;
	private String       tomcatPassword;
	private boolean      enabled;
	private InstanceType type;

	public Instance()
	{
	}

	public Instance(Long id)
	{
		super(id);
	}

	public Instance(Long id, String deployName, String instanceName, String displayName, String url, String urlDescription, String browserOpt, boolean compileOpt, boolean googleAnalytics, String tomcatUrl, String tomcatUsername, String tomcatPassword, boolean enabled, InstanceType type)
	{
		super(id);
		this.deployName = deployName;
		this.instanceName = instanceName;
		this.displayName = displayName;
		this.url = url;
		this.urlDescription = urlDescription;
		this.browserOpt = browserOpt;
		this.compileOpt = compileOpt;
		this.googleAnalytics = googleAnalytics;
		this.tomcatUrl = tomcatUrl;
		this.tomcatUsername = tomcatUsername;
		this.tomcatPassword = tomcatPassword;
		this.enabled = enabled;
		this.type = type;
	}

	public String getDeployName()
	{
		return deployName;
	}

	public Instance setDeployName(String deployName)
	{
		this.deployName = deployName;
		return this;
	}

	public String getInstanceName()
	{
		return instanceName;
	}

	public Instance setInstanceName(String instanceName)
	{
		this.instanceName = instanceName;
		return this;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public Instance setDisplayName(String displayName)
	{
		this.displayName = displayName;
		return this;
	}

	public String getUrl()
	{
		return url;
	}

	public Instance setUrl(String url)
	{
		this.url = url;
		return this;
	}

	public String getUrlDescription()
	{
		return urlDescription;
	}

	public Instance setUrlDescription(String urlDescription)
	{
		this.urlDescription = urlDescription;
		return this;
	}

	public Instance setBrowserOpt(String browserOpt)
	{
		this.browserOpt = browserOpt;
		return this;
	}

	public Instance setCompileOpt(boolean compileOpt)
	{
		this.compileOpt = compileOpt;
		return this;
	}

	public Instance setGoogleAnalytics(boolean googleAnalytics)
	{
		this.googleAnalytics = googleAnalytics;
		return this;
	}

	public Instance setTomcatUrl(String tomcatUrl)
	{
		this.tomcatUrl = tomcatUrl;
		return this;
	}

	public Instance setTomcatUsername(String tomcatUsername)
	{
		this.tomcatUsername = tomcatUsername;
		return this;
	}

	public Instance setTomcatPassword(String tomcatPassword)
	{
		this.tomcatPassword = tomcatPassword;
		return this;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public Instance setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		return this;
	}

	public InstanceType getType()
	{
		return type;
	}

	public Instance setType(InstanceType type)
	{
		this.type = type;
		return this;
	}

	@Override
	public String toString()
	{
		return "Instance{" +
				"deployName='" + deployName + '\'' +
				", instanceName='" + instanceName + '\'' +
				", displayName='" + displayName + '\'' +
				", url='" + url + '\'' +
				", urlDescription='" + urlDescription + '\'' +
				", browserOpt='" + browserOpt + '\'' +
				", compileOpt=" + compileOpt +
				", googleAnalytics=" + googleAnalytics +
				", tomcatUrl='" + tomcatUrl + '\'' +
				", tomcatUsername='" + tomcatUsername + '\'' +
				", tomcatPassword='" + tomcatPassword + '\'' +
				", enabled=" + enabled +
				", type=" + type +
				"} " + super.toString();
	}

	public List<String> getAntParameters()
	{
		List<String> result = new ArrayList<>();

		result.add("-D" + KEY_DEPLOY_NAME + "=" + deployName);
		result.add("-D" + KEY_INSTANCE_STUFF + "=./instance-stuff/" + instanceName);

		result.add("-D" + KEY_BROWSER_OPT + "=" + browserOpt);
		result.add("-D" + KEY_COMPILE_OPT + "=" + compileOpt);
		result.add("-D" + KEY_GOOGLE_ANALYTICS + "=" + googleAnalytics);

		result.add("-D" + KEY_TOMCAT_URL + "=" + tomcatUrl);
		result.add("-D" + KEY_TOMCAT_USERNAME + "=" + tomcatUsername);
		result.add("-D" + KEY_TOMCAT_PASSWORD + "=" + tomcatPassword);

		return result;
	}

	public static class Parser extends DatabaseObjectParser<Instance>
	{
		private Parser()
		{
		}

		@Override
		public Instance parse(DatabaseResult row, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
				return new Instance(id)
						.setDeployName(row.getString(DEPLOY_NAME))
						.setInstanceName(row.getString(INSTANCE_NAME))
						.setDisplayName(row.getString(DISPLAY_NAME))
						.setUrl(row.getString(URL))
						.setUrlDescription(row.getString(URL_DESCRIPTION))
						.setBrowserOpt(row.getString(BROWSER_OPT))
						.setCompileOpt(row.getBoolean(COMPILE_OPT))
						.setGoogleAnalytics(row.getBoolean(GOOGLE_ANALYTICS))
						.setTomcatUrl(row.getString(TOMCAT_URL))
						.setTomcatUsername(row.getString(TOMCAT_USERNAME))
						.setTomcatPassword(row.getString(TOMCAT_PASSWORD))
						.setEnabled(row.getBoolean(ENABLED))
						.setType(InstanceType.getFromName(row.getString("instance_types.description")));
		}

		public static final class Inst
		{
			public static Parser getInstance()
			{
				return InstanceHolder.INSTANCE;
			}

			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#getInstance()} or the first access to {@link
			 * InstanceHolder#INSTANCE}, not before.
			 * <p/>
			 * This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder
			 * idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
			 *
			 * @author Sebastian Raubach
			 */
			private static final class InstanceHolder
			{
				private static final Parser INSTANCE = new Parser();
			}
		}
	}
}
