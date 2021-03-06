// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.hadoop.distribution.emr450.test.modulegroup;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.emr450.modulegroup.EMR450PigModuleGroup;
import org.talend.hadoop.distribution.emr450.modulegroup.EMR450PigOutputModuleGroup;

public class EMR450PigOutputModuleGroupTest {

    @Test
    public void testModuleGroups() throws Exception {
        Map<String, String> results = new HashMap<>();

        results.put(EMR450PigModuleGroup.PIG_HCATALOG_GROUP_NAME, "(STORE=='HCATSTORER')"); //$NON-NLS-1$
        //results.put(EMR450PigModuleGroup., "(STORE=='HBASESTORAGE')"); //$NON-NLS-1$
        //results.put(CDH570Constant.PIG_HBASE_MODULE_GROUP.getModuleName(), "(STORE=='HBASESTORAGE')"); //$NON-NLS-1$
        results.put(EMR450PigModuleGroup.PIG_AVRO_GROUP_NAME, "(STORE=='AVROSTORAGE')"); //$NON-NLS-1$
        results.put(EMR450PigModuleGroup.PIG_RCFILE_GROUP_NAME, "(STORE=='RCFILEPIGSTORAGE')"); //$NON-NLS-1$
        results.put(EMR450PigModuleGroup.PIG_SEQUENCEFILE_GROUP_NAME, "(STORE=='SEQUENCEFILESTORAGE')"); //$NON-NLS-1$

        Set<DistributionModuleGroup> moduleGroups = EMR450PigOutputModuleGroup.getModuleGroups();
        assertEquals(4, moduleGroups.size());
        for (DistributionModuleGroup module : moduleGroups) {
            assertTrue("Should contain module " + module.getModuleName(), results.containsKey(module.getModuleName())); //$NON-NLS-1$
            if (results.get(module.getModuleName()) == null) {
                assertTrue("The condition of the module " + module.getModuleName() + " is not null.", //$NON-NLS-1$ //$NON-NLS-2$
                        results.get(module.getModuleName()) == null);
            } else {
                assertTrue("The condition of the module " + module.getModuleName() + " is null, but it should be " //$NON-NLS-1$ //$NON-NLS-2$
                        + results.get(module.getModuleName()) + ".", results.get(module.getModuleName()) != null); //$NON-NLS-1$
                assertEquals(results.get(module.getModuleName()), module.getRequiredIf().getConditionString());
            }
        }
    }
}
