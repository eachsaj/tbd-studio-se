// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.designer.hdfsbrowse.model;

import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.designer.hdfsbrowse.manager.HadoopOperationManager;
import org.talend.designer.hdfsbrowse.ui.provider.FileSelectorTreeViewerProvider;

/**
 * DOC ycbai class global comment. Detailled comment
 * 
 * DFS Path handling for DFS
 * 
 */
public abstract class HDFSPath extends HDFSNode {

    protected FileSelectorTreeViewerProvider provider;

    protected HDFSConnectionBean connection;

    protected Object fileSystem;

    protected ClassLoader classLoader;

    public HDFSPath(FileSelectorTreeViewerProvider provider, HDFSConnectionBean connection) {
        this.provider = provider;
        this.connection = connection;
        try {
            fileSystem = getOperationManager().getDFS(connection);
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
        classLoader = getOperationManager().getClassLoader(connection);
    }

    protected HDFSPath(HDFSPath parent) {
        this.setParent(parent);
        this.provider = parent.provider;
        this.connection = parent.connection;
        fileSystem = parent.fileSystem;
        classLoader = parent.classLoader;
    }

    protected void dispose() {
        // Free the DFS connection
    }

    @Override
    public String toString() {
        if (PATH_SEPARATOR.equals(getPath())) { //$NON-NLS-1$
            return connection.getNameNodeURI();
        } else {
            return getPath();
        }
    }

    public abstract void refresh();

    /**
     * Refresh the UI element for this content
     */
    public void doRefresh() {
        provider.refresh(this);
    }

    public HadoopOperationManager getOperationManager() {
        return HadoopOperationManager.getInstance();
    }
}
