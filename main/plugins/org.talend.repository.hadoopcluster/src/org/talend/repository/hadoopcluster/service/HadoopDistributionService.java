// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.hadoopcluster.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.core.hadoop.IHadoopDistributionService;
import org.talend.core.runtime.hd.IDistributionsManager;
import org.talend.core.runtime.hd.IHDistribution;
import org.talend.core.runtime.hd.IHDistributionVersion;
import org.talend.hadoop.distribution.constants.apache.IApacheDistribution;
import org.talend.hadoop.distribution.constants.emr.IAmazonEMRDistribution;
import org.talend.hadoop.distribution.helper.DistributionHelper;
import org.talend.hadoop.distribution.helper.DistributionsManager;
import org.talend.hadoop.distribution.helper.HadoopDistributionsHelper;

/**
 * created by cmeng on Jan 15, 2016 Detailled comment
 *
 */
public class HadoopDistributionService implements IHadoopDistributionService {

    public IHDistribution[] getDistributions(String service) {
        if (service != null) {
            DistributionsManager helper = new DistributionsManager(service);
            return helper.getDistributions();
        }
        return new IHDistribution[0];
    }

    @Override
    public IHDistribution[] getOozieDistributions() {
        IHDistribution[] hadoopDistributions = HadoopDistributionsHelper.HADOOP.getDistributions();
        List<IHDistribution> oozieDistributions = new ArrayList<IHDistribution>();
        for (IHDistribution d : hadoopDistributions) {
            if (IApacheDistribution.DISTRIBUTION_NAME.equals(d.getName())
                    || IAmazonEMRDistribution.DISTRIBUTION_NAME.equals(d.getName())) {
                continue;
            }
            oozieDistributions.add(d);
        }
        return oozieDistributions.toArray(new IHDistribution[0]);
    }

    @Override
    public IDistributionsManager getHadoopDistributionManager() {
        return HadoopDistributionsHelper.HADOOP;
    }

    @Override
    public IDistributionsManager getHBaseDistributionManager() {
        return HadoopDistributionsHelper.HBASE;
    }

    @Override
    public IDistributionsManager getSparkDistributionManager() {
        return HadoopDistributionsHelper.SPARK;
    }

    @Override
    public IDistributionsManager getHiveDistributionManager() {
        return HadoopDistributionsHelper.HIVE;
    }

    @Override
    public boolean doSupportService(IHDistributionVersion distributionVersion, String service) {
        return DistributionHelper.doSupportService(distributionVersion, service);
    }

    @Override
    public Map<String, Boolean> doSupportMethods(IHDistributionVersion distributionVersion, String... methods) {
        return DistributionHelper.doSupportMethods(distributionVersion, methods);
    }

    public IHDistribution getHadoopDistribution(String name, boolean byDisplay) {
        return HadoopDistributionsHelper.HADOOP.getDistribution(name, byDisplay);
    }

    public IHDistributionVersion getHadoopDistributionVersion(String version, boolean byDisplay) {
        return HadoopDistributionsHelper.HADOOP.getDistributionVersion(version, byDisplay);
    }

}
