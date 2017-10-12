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

package jhi.germinatebuilder.server.database;

import jhi.database.server.*;
import jhi.database.server.parser.*;
import jhi.database.shared.exception.*;
import jhi.database.shared.util.*;

/**
 * @author Sebastian Raubach
 */
public class User extends DatabaseObject
{
	public static final String ID       = "id";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";

	private String username;
	private String password;

	public User()
	{
	}

	public User(Long id)
	{
		super(id);
	}

	public User(Long id, String username, String password)
	{
		super(id);
		this.username = username;
		this.password = password;
	}

	public User setId(String id)
	{
		this.id = Long.parseLong(id);
		return this;
	}

	public String getUsername()
	{
		return username;
	}

	public User setUsername(String username)
	{
		this.username = username;
		return this;
	}

	public String getPassword()
	{
		return password;
	}

	public User setPassword(String password)
	{
		this.password = password;
		return this;
	}

	@Override
	public String toString()
	{
		return "User{" +
				"username='" + username + '\'' +
				"} " + super.toString();
	}

	public static class Parser extends DatabaseObjectParser<User>
	{
		private Parser()
		{
		}

		@Override
		public User parse(DatabaseResult row, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
				return new User(id)
						.setUsername(row.getString(USERNAME))
						.setPassword(row.getString(PASSWORD));
		}

		public static final class Instance
		{
			public static Parser getInstance()
			{
				return InstanceHolder.INSTANCE;
			}

			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Instance#getInstance()} or the first access to {@link
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
