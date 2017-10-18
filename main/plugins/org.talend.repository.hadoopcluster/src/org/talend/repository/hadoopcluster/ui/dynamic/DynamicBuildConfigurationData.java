// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.hadoopcluster.ui.dynamic;

import org.talend.core.runtime.dynamic.IDynamicPlugin;
import org.talend.hadoop.distribution.dynamic.DynamicConfiguration;
import org.talend.hadoop.distribution.dynamic.IDynamicDistributionsGroup;

/**
 * DOC cmeng  class global comment. Detailled comment
 */
public class DynamicBuildConfigurationData {

    private IDynamicDistributionsGroup dynamicDistributionsGroup;

    private IDynamicPlugin dynamicPlugin;

    private DynamicConfiguration newDistrConfigration;

    private ActionType actionType;

    public IDynamicDistributionsGroup getDynamicDistributionsGroup() {
        return this.dynamicDistributionsGroup;
    }

    public void setDynamicDistributionsGroup(IDynamicDistributionsGroup dynamicDistributionsGroup) {
        this.dynamicDistributionsGroup = dynamicDistributionsGroup;
    }

    public IDynamicPlugin getDynamicPlugin() {
        return this.dynamicPlugin;
    }

    public void setDynamicPlugin(IDynamicPlugin dynamicPlugin) {
        this.dynamicPlugin = dynamicPlugin;
    }

    public DynamicConfiguration getNewDistrConfigration() {
        return this.newDistrConfigration;
    }

    public void setNewDistrConfigration(DynamicConfiguration newDistrConfigration) {
        this.newDistrConfigration = newDistrConfigration;
    }

    public ActionType getActionType() {
        return this.actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public static enum ActionType {
        EditExisting,
        NewConfig,
        Import
    }

}