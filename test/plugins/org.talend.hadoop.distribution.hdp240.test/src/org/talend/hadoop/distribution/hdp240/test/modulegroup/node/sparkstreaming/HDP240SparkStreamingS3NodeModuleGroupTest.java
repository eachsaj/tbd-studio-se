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
package org.talend.hadoop.distribution.hdp240.test.modulegroup.node.sparkstreaming;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.hdp240.HDP240Constant;
import org.talend.hadoop.distribution.hdp240.modulegroup.node.sparkstreaming.HDP240SparkStreamingS3NodeModuleGroup;

/**
 * created by pbailly on 16 Feb 2016 Detailled comment
 *
 */
public class HDP240SparkStreamingS3NodeModuleGroupTest {

    @Test
    public void testModuleGroups() throws Exception {
        Map<String, String> results = new HashMap<String, String>();

        results.put(
                HDP240Constant.SPARK_S3_MRREQUIRED_MODULE_GROUP.getModuleName(),
                "((#LINK@NODE.STORAGE_CONFIGURATION.DISTRIBUTION=='HORTONWORKS') AND (#LINK@NODE.STORAGE_CONFIGURATION.SPARK_VERSION=='HDP_2_4')) AND (#LINK@NODE.STORAGE_CONFIGURATION.SPARK_LOCAL_MODE=='false')");

        Set<DistributionModuleGroup> moduleGroups = HDP240SparkStreamingS3NodeModuleGroup.getModuleGroups();
        assertEquals(results.size(), moduleGroups.size());
        moduleGroups.iterator();
        for (DistributionModuleGroup module : moduleGroups) {
            assertTrue("Should contain module " + module.getModuleName(), results.containsKey(module.getModuleName()));
            if (results.get(module.getModuleName()) == null) {
                assertTrue("The condition of the module " + module.getModuleName() + " is not null.",
                        results.get(module.getModuleName()) == null);
            } else {
                assertTrue(
                        "The condition of the module " + module.getModuleName() + " is null, but it should be "
                                + results.get(module.getModuleName()) + ".", results.get(module.getModuleName()) != null);
                assertEquals(results.get(module.getModuleName()), module.getRequiredIf().getConditionString());
            }
        }
    }
}
