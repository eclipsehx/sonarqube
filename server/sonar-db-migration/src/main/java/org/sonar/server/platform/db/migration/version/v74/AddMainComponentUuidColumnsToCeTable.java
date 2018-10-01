/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.db.migration.version.v74;

import java.sql.SQLException;
import org.sonar.db.Database;
import org.sonar.server.platform.db.migration.def.VarcharColumnDef;
import org.sonar.server.platform.db.migration.sql.AddColumnsBuilder;
import org.sonar.server.platform.db.migration.sql.CreateIndexBuilder;
import org.sonar.server.platform.db.migration.sql.DropColumnsBuilder;
import org.sonar.server.platform.db.migration.sql.DropIndexBuilder;
import org.sonar.server.platform.db.migration.step.DdlChange;

import static org.sonar.server.platform.db.migration.def.VarcharColumnDef.newVarcharColumnDefBuilder;

public abstract class AddMainComponentUuidColumnsToCeTable extends DdlChange {
  private static final VarcharColumnDef COLUMN_COMPONENT_UUID = newVarcharColumnDefBuilder()
    .setColumnName("component_uuid")
    .setLimit(VarcharColumnDef.UUID_SIZE)
    .setIsNullable(true)
    .build();
  private static final VarcharColumnDef COLUMN_MAIN_COMPONENT_UUID = newVarcharColumnDefBuilder()
    .setColumnName("main_component_uuid")
    .setLimit(VarcharColumnDef.UUID_SIZE)
    .setIsNullable(true)
    .build();
  private final String tableName;

  AddMainComponentUuidColumnsToCeTable(Database db, String tableName) {
    super(db);
    this.tableName = tableName;
  }

  @Override
  public void execute(Context context) throws SQLException {
    // drop existing column with wrong values
    context.execute(new DropIndexBuilder(getDialect())
      .setTable(tableName)
      .setName(tableName + "_component_uuid")
      .build());
    context.execute(new DropColumnsBuilder(getDialect(), tableName, COLUMN_COMPONENT_UUID.getName())
      .build());

    // add new columns
    context.execute(new AddColumnsBuilder(getDialect(), tableName)
      .addColumn(COLUMN_COMPONENT_UUID)
      .addColumn(COLUMN_MAIN_COMPONENT_UUID)
      .build());

    // create indexes
    context.execute(new CreateIndexBuilder(getDialect())
      .setTable(tableName)
      .setName(tableName + "_component")
      .addColumn(COLUMN_COMPONENT_UUID)
      .setUnique(false)
      .build());
    context.execute(new CreateIndexBuilder(getDialect())
      .setTable(tableName)
      .setName(tableName + "_main_component")
      .addColumn(COLUMN_MAIN_COMPONENT_UUID)
      .setUnique(false)
      .build());
  }
}