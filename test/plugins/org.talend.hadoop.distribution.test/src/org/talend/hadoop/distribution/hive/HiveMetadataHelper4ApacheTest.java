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
package org.talend.hadoop.distribution.hive;

import org.junit.Test;
import org.talend.hadoop.distribution.constants.apache.IApacheDistribution;

/**
 * DOC ggu class global comment. Detailled comment
 */
public class HiveMetadataHelper4ApacheTest extends AbstractTest4HiveMetadataHelper {

    @Override
    protected String getDistribution() {
        return IApacheDistribution.DISTRIBUTION_NAME;
    }

    @Override
    protected String getDistributionDisplay() {
        return IApacheDistribution.DISTRIBUTION_DISPLAY_NAME;
    }

    @Override
    protected String[] getDistributionVersionsDisplay() {
        return new String[] { IApacheDistribution.VERSION_100_HIVE_DISPLAY };
    }

    @Test
    public void testHiveMode_Apache100_WithAll() {
        doTestGetHiveModesDisplay(IApacheDistribution.VERSION_100, HIVE_MODE_DISPLAY_ALL);
    }

    @Test
    public void testHiveServer_Apache100_Server1_Only() {
        doTestGetHiveServersDisplay(IApacheDistribution.VERSION_100, HIVE_SERVER_DISPLAY_SERVER1_ONLY);
    }

}
