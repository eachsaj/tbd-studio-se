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

package org.talend.designer.hdfsbrowse.manager;

/**
 * DOC ycbai class global comment. Detailled comment
 */
public enum EHadoopParameter {

    DISTRIBUTION,

    VERSION,

    NAMENODE_URI,

    USE_KRB,

    NAMENODE_PRINCIPAL,

    USE_KEYTAB,

    PRINCIPAL,

    KEYTAB_PATH,

    USERNAME,

    GROUP,

    MR_VERSION,

    NAMENODE,

    AUTHENTICATION_MODE,

    HADOOP_CUSTOM_JARS,

    ;

    public String getName() {
        return this.name();
    }

}