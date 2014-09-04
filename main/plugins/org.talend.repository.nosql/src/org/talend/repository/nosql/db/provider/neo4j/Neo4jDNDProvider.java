// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.nosql.db.provider.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.designerproperties.ComponentToRepositoryProperty;
import org.talend.core.model.process.IElement;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.core.utils.TalendQuoteUtils;
import org.talend.repository.model.nosql.NoSQLConnection;
import org.talend.repository.nosql.db.common.neo4j.INeo4jAttributes;
import org.talend.repository.nosql.db.common.neo4j.INeo4jConstants;
import org.talend.repository.nosql.ui.dnd.AbstractDNDProvider;

/**
 * 
 * created by ycbai on Jul 22, 2014 Detailled comment
 * 
 */
public class Neo4jDNDProvider extends AbstractDNDProvider {

    @Override
    public Object getRepositoryValue(NoSQLConnection connection, String value, IMetadataTable table, String targetComponent) {
        if (INeo4jAttributes.REMOTE_SERVER.equals(value)) {
            return getCanonicalRepositoryValue(connection, connection.getAttributes().get(INeo4jAttributes.REMOTE_SERVER), false);
        } else if (INeo4jAttributes.DATABASE_PATH.equals(value)) {
            return getCanonicalRepositoryValue(connection, connection.getAttributes().get(INeo4jAttributes.DATABASE_PATH));
        } else if (INeo4jAttributes.SERVER_URL.equals(value)) {
            return getCanonicalRepositoryValue(connection, connection.getAttributes().get(INeo4jAttributes.SERVER_URL));
        } else if (INeo4jAttributes.COLUMN_MAPPING.equals(value)) {
            return getColumnMappingValue(connection, table);
        }

        return null;
    }

    private static List<Map<String, Object>> getColumnMappingValue(NoSQLConnection connection, IMetadataTable metaTable) {
        if (connection == null || metaTable == null) {
            return null;
        }

        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
        List<IMetadataColumn> columns = metaTable.getListColumns();
        for (IMetadataColumn column : columns) {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put(INeo4jConstants.SCHEMA_COLUMN, column.getLabel());
            row.put(INeo4jConstants.RETURN_PARAMETER,
                    TalendQuoteUtils.addQuotesIfNotExist(column.getAdditionalField().get(INeo4jConstants.RETURN_PARAMETER)));
            values.add(row);
        }

        return values;
    }

    @Override
    public void setRepositoryValue(NoSQLConnection connection, INode node, IElementParameter param) {
        if (INeo4jAttributes.REMOTE_SERVER.equals(param.getRepositoryValue())) {
            String value = ComponentToRepositoryProperty.getParameterValue(connection, node, param);
            if (value != null) {
                connection.getAttributes().put(INeo4jAttributes.REMOTE_SERVER, value);
            }
        } else if (INeo4jAttributes.DATABASE_PATH.equals(param.getRepositoryValue())) {
            String value = ComponentToRepositoryProperty.getParameterValue(connection, node, param);
            if (value != null) {
                connection.getAttributes().put(INeo4jAttributes.DATABASE_PATH, value);
            }
        } else if (INeo4jAttributes.SERVER_URL.equals(param.getRepositoryValue())) {
            String value = ComponentToRepositoryProperty.getParameterValue(connection, node, param);
            if (value != null) {
                connection.getAttributes().put(INeo4jAttributes.SERVER_URL, value);
            }
        }
    }

    @Override
    public void handleTableRelevantParameters(NoSQLConnection connection, IElement ele, IMetadataTable metadataTable) {
        if (ele == null || metadataTable == null) {
            return;
        }
        IElementParameter queryParameter = ele.getElementParameter(INeo4jConstants.QUERY);
        if (queryParameter != null) {
            String cypher = metadataTable.getAdditionalProperties().get(INeo4jConstants.CYPHER);
            if (StringUtils.trimToNull(cypher) != null) {
                queryParameter.setValue(TalendQuoteUtils.addQuotesIfNotExist(cypher));
            }
        }
    }

}