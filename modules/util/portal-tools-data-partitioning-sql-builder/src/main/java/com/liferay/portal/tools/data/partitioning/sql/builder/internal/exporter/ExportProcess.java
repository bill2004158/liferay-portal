/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.tools.data.partitioning.sql.builder.internal.exporter;

import com.liferay.portal.tools.data.partitioning.sql.builder.command.ExportProcessCommand;
import com.liferay.portal.tools.data.partitioning.sql.builder.exporter.DBExporter;
import com.liferay.portal.tools.data.partitioning.sql.builder.exporter.context.ExportContext;
import com.liferay.portal.tools.data.partitioning.sql.builder.internal.command.DeleteExportProcessCommand;
import com.liferay.portal.tools.data.partitioning.sql.builder.internal.command.InsertControlExportProcessCommand;
import com.liferay.portal.tools.data.partitioning.sql.builder.internal.command.InsertPartitionedExportProcessCommand;

import java.io.IOException;

import java.util.List;

/**
 * @author Manuel de la Peña
 */
public class ExportProcess {

	public ExportProcess(DBExporter dbExporter) {
		_dbExporter = dbExporter;
	}

	public void export(ExportContext exportContext) throws IOException {
		List<String> partitionedTableNames =
			_dbExporter.getPartitionedTableNames(exportContext.getSchemaName());
		List<String> controlTableNames = _dbExporter.getControlTableNames(
			exportContext.getSchemaName());

		List<Long> companyIds = exportContext.getCompanyIds();

		for (Long companyId : companyIds) {
			ExportProcessCommand deleteExportProcessCommand =
				new DeleteExportProcessCommand(
					companyId, _dbExporter, partitionedTableNames,
					exportContext);

			deleteExportProcessCommand.execute();

			ExportProcessCommand insertPartitionedExportProcessCommand =
				new InsertPartitionedExportProcessCommand(
					companyId, _dbExporter, partitionedTableNames,
					exportContext);

			insertPartitionedExportProcessCommand.execute();

			ExportProcessCommand insertControlExportProcessCommand =
				new InsertControlExportProcessCommand(
					companyId, _dbExporter, controlTableNames, exportContext);

			insertControlExportProcessCommand.execute();
		}
	}

	private final DBExporter _dbExporter;

}