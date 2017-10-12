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

package jhi.germinatebuilder.server.manager;

import java.util.*;

import jhi.database.server.parser.*;
import jhi.database.server.query.*;
import jhi.database.shared.exception.*;
import jhi.germinatebuilder.server.database.*;

/**
 * @author Sebastian Raubach
 */
public class InstanceManager extends AbstractManager
{
	private static final String COMMON_TABLES           = " instances LEFT JOIN instance_types ON instance_types.id = instances.type_id LEFT JOIN user_permissions ON user_permissions.instance_id = instances.id ";
	private static final String SELECT_ALL              = "SELECT DISTINCT instances.*, instance_types.* FROM " + COMMON_TABLES + " WHERE enabled = 1 AND user_id LIKE ? ORDER BY instance_types.id, instances.display_name";
	private static final String SELECT_BY_TYPE          = "SELECT DISTINCT instances.*, instance_types.* FROM " + COMMON_TABLES + " WHERE enabled = 1 AND user_id LIKE ? AND instance_types.description = ?";
	private static final String SELECT_BY_TYPE_AND_NAME = "SELECT DISTINCT instances.*, instance_types.* FROM " + COMMON_TABLES + " WHERE enabled = 1 AND user_id LIKE ? AND instance_types.description = ? AND instances.instance_name IN (%s)";

	public static InstanceManager get()
	{
		return InstanceManager.Inst.INSTANCE;
	}

	public static List<Instance> getAll(User user) throws DatabaseException
	{
		return new DatabaseObjectQuery<Instance>(SELECT_ALL)
				.setString(user == null ? "%" : Long.toString(user.getId()))
				.run()
				.getObjects(Instance.Parser.Inst.getInstance(), true);
	}

	public static List<Instance> getAllForType(InstanceType type, User user) throws DatabaseException
	{
		return new DatabaseObjectQuery<Instance>(SELECT_BY_TYPE)
				.setString(user == null ? "%" : Long.toString(user.getId()))
				.setString(type.getName())
				.run()
				.getObjects(Instance.Parser.Inst.getInstance(), true);
	}

	public static List<Instance> getFromNames(String[] instanceNames, InstanceType type, User user) throws DatabaseException
	{
		String formatted = String.format(SELECT_BY_TYPE_AND_NAME, generateSqlPlaceholderString(instanceNames.length));
		return new DatabaseObjectQuery<Instance>(formatted)
				.setString(user == null ? "%" : Long.toString(user.getId()))
				.setString(type.getName())
				.setStrings(instanceNames)
				.run()
				.getObjects(Instance.Parser.Inst.getInstance());
	}

	@Override
	protected DatabaseObjectParser getParser()
	{
		return Instance.Parser.Inst.getInstance();
	}

	private static class Inst
	{
		private static final InstanceManager INSTANCE = new InstanceManager();
	}
}
