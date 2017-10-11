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

package jhi.germinatebuilder.server.manager;

import jhi.database.server.*;
import jhi.database.server.parser.*;
import jhi.database.server.query.*;
import jhi.database.shared.exception.*;
import jhi.germinatebuilder.server.database.*;
import jhi.germinatebuilder.server.util.*;

/**
 * @author Sebastian Raubach
 */
public class UserManager extends AbstractManager
{
	private static final String SELECT_BY_USERNAME_PASSWORD = "SELECT * FROM users WHERE username = ?";

	public static UserManager get()
	{
		return UserManager.Inst.INSTANCE;
	}

	public static User getForUsernamePassword(String username, String password) throws DatabaseException
	{
		String server = PropertyReader.getProperty(PropertyReader.GATEKEEPER_SERVER);
		String database = PropertyReader.getProperty(PropertyReader.GATEKEEPER_DATABASE);
		String port = PropertyReader.getProperty(PropertyReader.GATEKEEPER_PORT);
		String path = server + (StringUtils.isEmpty(port) ? "/" : ":" + port + "/") + database;
		Database db = Database.connect(Database.DatabaseType.MYSQL, path, PropertyReader.getProperty(PropertyReader.GATEKEEPER_USERNAME), PropertyReader.getProperty(PropertyReader.GATEKEEPER_PASSWORD));

		User user = new DatabaseObjectQuery<User>(db, SELECT_BY_USERNAME_PASSWORD)
				.setString(username)
				.run()
				.getObject(User.Parser.Instance.getInstance());

		if (user != null && BCrypt.checkpw(password, user.getPassword()))
		{
			boolean hasAccess = new ValueQuery("SELECT COUNT(1) AS count FROM user_permissions WHERE user_id = ?")
					.setLong(user.getId())
					.run("count")
					.getInt(0) > 0;

			if (!hasAccess)
				user = null;
		}
		else
		{
			user = null;
		}

		return user;
	}

	@Override
	protected DatabaseObjectParser getParser()
	{
		return User.Parser.Instance.getInstance();
	}

	private static class Inst
	{
		private static final UserManager INSTANCE = new UserManager();
	}
}
