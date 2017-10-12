/**
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

/**
 * @author Sebastian Raubach
 */
public enum InstanceType
{
	GERMINATE("Germinate"),
	GATEKEEPER("Germinate Gatekeeper");

	String name;

	InstanceType(String name)
	{
		this.name = name;
	}

	public static InstanceType getFromName(String name)
	{
		for (InstanceType type : values())
		{
			if (type.getName().equals(name))
				return type;
		}

		return null;
	}

	public String getName()
	{
		return name;
	}
}
