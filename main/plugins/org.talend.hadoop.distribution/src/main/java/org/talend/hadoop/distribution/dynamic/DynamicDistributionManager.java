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
package org.talend.hadoop.distribution.dynamic;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.utils.workbench.resources.ResourceUtils;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.general.ILibrariesService;
import org.talend.core.model.general.Project;
import org.talend.core.runtime.dynamic.DynamicFactory;
import org.talend.core.runtime.dynamic.DynamicServiceUtil;
import org.talend.core.runtime.dynamic.IDynamicPlugin;
import org.talend.core.runtime.dynamic.IDynamicPluginConfiguration;
import org.talend.designer.maven.aether.IDynamicMonitor;
import org.talend.hadoop.distribution.dynamic.adapter.DynamicDistriConfigAdapter;
import org.talend.hadoop.distribution.dynamic.cdh.DynamicCDHDistributionsGroup;
import org.talend.hadoop.distribution.helper.HadoopDistributionsHelper;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.RepositoryConstants;

/**
 * DOC cmeng  class global comment. Detailled comment
 */
public class DynamicDistributionManager {

    public static final String USERS_DISTRIBUTIONS_ROOT_FOLDER = "dynamicDistributions"; //$NON-NLS-1$

    private static DynamicDistributionManager instance;

    private List<IDynamicPlugin> usersPluginsCache;

    private String usersPluginsCacheVersion;

    private DynamicDistributionManager() {
        // nothing to do
    }

    public static DynamicDistributionManager getInstance() {
        if (instance == null) {
            instance = new DynamicDistributionManager();
        }
        return instance;
    }

    private List<IDynamicDistributionsGroup> dynamicDistributionsGroups;

    public List<IDynamicDistributionsGroup> getDynamicDistributionsGroups() throws Exception {

        if (dynamicDistributionsGroups != null) {
            return dynamicDistributionsGroups;
        }

        dynamicDistributionsGroups = new ArrayList<>();

        dynamicDistributionsGroups.add(new DynamicCDHDistributionsGroup());

        return dynamicDistributionsGroups;

    }

    public List<IDynamicPlugin> getAllBuildinDynamicPlugins(IDynamicMonitor monitor) throws Exception {
        List<IDynamicPlugin> allBuildinPlugins = new LinkedList<>();
        List<IDynamicDistributionsGroup> dynDistrGroups = getDynamicDistributionsGroups();
        if (dynDistrGroups != null && !dynDistrGroups.isEmpty()) {
            for (IDynamicDistributionsGroup dynDistrGroup : dynDistrGroups) {
                try {
                    List<IDynamicPlugin> allBuildinDynamicPlugins = dynDistrGroup.getAllBuildinDynamicPlugins(monitor);
                    if (allBuildinDynamicPlugins != null && !allBuildinDynamicPlugins.isEmpty()) {
                        allBuildinPlugins.addAll(allBuildinDynamicPlugins);
                    }
                } catch (Throwable e) {
                    ExceptionHandler.process(e);
                }
            }
        }
        return allBuildinPlugins;
    }

    public void saveUsersDynamicPlugin(IDynamicPlugin dynamicPlugin, IDynamicMonitor monitor) throws Exception {
        FileOutputStream outStream = null;
        IDynamicPluginConfiguration pluginConfiguration = dynamicPlugin.getPluginConfiguration();
        Object obj = pluginConfiguration.getAttribute(DynamicDistriConfigAdapter.ATTR_FILE_PATH);
        try {
            String filePath = (String) obj;
            if (StringUtils.isEmpty(filePath)) {
                IProject eProject = ResourceUtils.getProject(ProjectManager.getInstance().getCurrentProject());
                IFolder usersFolder = ResourceUtils.getFolder(eProject,
                        RepositoryConstants.SETTING_DIRECTORY + "/" + getUsersFolderPath(), //$NON-NLS-1$
                        false);
                if (usersFolder == null || !usersFolder.exists()) {
                    ResourceUtils.createFolder(usersFolder);
                }
                String folderPath = usersFolder.getLocation().toPortableString();
                String fileName = pluginConfiguration.getId();
                filePath = folderPath + "/" + fileName + ".json"; //$NON-NLS-1$
            }
            obj = pluginConfiguration.removeAttribute(DynamicDistriConfigAdapter.ATTR_FILE_PATH);
            String content = DynamicServiceUtil.formatJsonString(dynamicPlugin.toXmlJson().toString());
            File outFile = new File(filePath);
            outStream = new FileOutputStream(outFile);
            outStream.write(content.getBytes("UTF-8")); //$NON-NLS-1$
            outStream.flush();
        } finally {
            pluginConfiguration.setAttribute(DynamicDistriConfigAdapter.ATTR_FILE_PATH, obj);
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (Exception e) {
                    ExceptionHandler.process(e);
                }
            }
        }
    }

    public List<IDynamicPlugin> getAllUsersDynamicPlugins(IDynamicMonitor monitor) throws Exception {
        if (usersPluginsCache != null) {
            String systemCacheVersion = HadoopDistributionsHelper.getCacheVersion();
            if (StringUtils.equals(systemCacheVersion, usersPluginsCacheVersion)) {
                return usersPluginsCache;
            }
        }
        usersPluginsCacheVersion = HadoopDistributionsHelper.getCacheVersion();

        List<IDynamicPlugin> dynamicPlugins = new LinkedList<>();

        ProjectManager projectManager = ProjectManager.getInstance();

        List<IDynamicPlugin> tempDynPlugins = getAllUsersDynamicPluginsForProject(projectManager.getCurrentProject(), monitor);
        if (tempDynPlugins != null && 0 < tempDynPlugins.size()) {
            dynamicPlugins.addAll(tempDynPlugins);
        }

        List<Project> allRefProjects = ProjectManager.getInstance().getAllReferencedProjects();
        if (allRefProjects != null && 0 < allRefProjects.size()) {
            for (Project refProject : allRefProjects) {
                try {
                    tempDynPlugins = getAllUsersDynamicPluginsForProject(refProject, monitor);
                    if (tempDynPlugins != null && 0 < tempDynPlugins.size()) {
                        dynamicPlugins.addAll(tempDynPlugins);
                    }
                } catch (Exception e) {
                    ExceptionHandler.process(e);
                }
            }
        }

        usersPluginsCache = dynamicPlugins;

        return usersPluginsCache;
    }

    protected List<IDynamicPlugin> getAllUsersDynamicPluginsForProject(Project project, IDynamicMonitor monitor)
            throws Exception {
        IProject eProject = ResourceUtils.getProject(project);
        IFolder usersFolder = ResourceUtils.getFolder(eProject,
                RepositoryConstants.SETTING_DIRECTORY + "/" + getUsersFolderPath(), //$NON-NLS-1$
                false);
        if (usersFolder == null || !usersFolder.exists()) {
            return null;
        }

        List<IDynamicPlugin> dynamicPlugins = new ArrayList<>();

        List<IResource> members = getAllFileResource(usersFolder);

        if (members != null && 0 < members.size()) {
            for (IResource member : members) {
                try {
                    String absolutePath = member.getLocation().toPortableString();
                    String jsonContent = DynamicServiceUtil.readFile(new File(absolutePath));
                    IDynamicPlugin dynamicPlugin = DynamicFactory.getInstance().createPluginFromJson(jsonContent);
                    IDynamicPluginConfiguration pluginConfiguration = dynamicPlugin.getPluginConfiguration();
                    pluginConfiguration.setAttribute(DynamicDistriConfigAdapter.ATTR_FILE_PATH, absolutePath);
                    dynamicPlugins.add(dynamicPlugin);
                } catch (Exception e) {
                    ExceptionHandler.process(e);
                }
            }
        }
        return dynamicPlugins;
    }
    
    private List<IResource> getAllFileResource(IResource member) throws CoreException {
        List<IResource> fileList = new ArrayList<>();

        if (member != null) {
            if (member instanceof IFolder) {
                IResource[] members = ((IFolder) member).members();
                if (members != null && 0 < members.length) {
                    for (IResource resource : members) {
                        List<IResource> subResources = getAllFileResource(resource);
                        if (subResources != null && !subResources.isEmpty()) {
                            fileList.addAll(subResources);
                        }
                    }
                }
            } else {
                fileList.add(member);
            }
        }

        return fileList;
    }

    public void registAll(IDynamicMonitor monitor) throws Exception {
        registAllBuildin(monitor, false);
        registAllUsers(monitor, false);
        cleanCache();
    }

    public void registAllBuildin(IDynamicMonitor monitor, boolean cleanCache) throws Exception {

        List<IDynamicDistributionsGroup> dynDistriGroups = getDynamicDistributionsGroups();

        if (dynDistriGroups == null || dynDistriGroups.isEmpty()) {
            return;
        }

        for (IDynamicDistributionsGroup dynDistriGroup : dynDistriGroups) {
            try {
                dynDistriGroup.registAllBuildin(monitor);
            } catch (Throwable e) {
                ExceptionHandler.process(e);
            }
        }

        if (cleanCache) {
            cleanCache();
        }

    }

    public void registAllUsers(IDynamicMonitor monitor, boolean cleanCache) throws Exception {

        List<IDynamicPlugin> allUsersDynamicPlugins = getAllUsersDynamicPlugins(monitor);
        if (allUsersDynamicPlugins == null || allUsersDynamicPlugins.isEmpty()) {
            return;
        }

        List<IDynamicDistributionsGroup> dynDistriGroups = getDynamicDistributionsGroups();
        if (dynDistriGroups == null || dynDistriGroups.isEmpty()) {
            throw new Exception("No dynamic distribution group found.");
        }

        for (IDynamicPlugin dynamicPlugin : allUsersDynamicPlugins) {
            boolean registed = false;
            for (IDynamicDistributionsGroup dynDistriGroup : dynDistriGroups) {
                try {
                    if (dynDistriGroup.canRegist(dynamicPlugin, monitor)) {
                        dynDistriGroup.regist(dynamicPlugin, monitor);
                        registed = true;
                    }
                } catch (Throwable e) {
                    ExceptionHandler.process(e);
                }
            }
            if (!registed) {
                ExceptionHandler.process(new Exception(
                        "Can't regist dynamic distribution: " + dynamicPlugin.getPluginConfiguration().getTemplateId()));
            }
        }

        if (cleanCache) {
            cleanCache();
        }

    }

    public void unregistAll(IDynamicMonitor monitor) throws Exception {
        unregistAllUsers(monitor, false);
        unregistAllBuildin(monitor, false);
        cleanCache();
    }

    public void unregistAllBuildin(IDynamicMonitor monitor, boolean cleanCache) throws Exception {

        List<IDynamicDistributionsGroup> dynDistriGroups = getDynamicDistributionsGroups();

        if (dynDistriGroups == null || dynDistriGroups.isEmpty()) {
            return;
        }

        for (IDynamicDistributionsGroup dynDistriGroup : dynDistriGroups) {
            try {
                dynDistriGroup.unregistAllBuildin(monitor);
            } catch (Throwable e) {
                ExceptionHandler.process(e);
            }
        }

        if (cleanCache) {
            cleanCache();
        }

    }

    public void unregistAllUsers(IDynamicMonitor monitor, boolean cleanCache) throws Exception {

        List<IDynamicPlugin> allUsersDynamicPlugins = getAllUsersDynamicPlugins(monitor);
        if (allUsersDynamicPlugins == null || allUsersDynamicPlugins.isEmpty()) {
            return;
        }

        List<IDynamicDistributionsGroup> dynDistriGroups = getDynamicDistributionsGroups();
        if (dynDistriGroups == null || dynDistriGroups.isEmpty()) {
            throw new Exception("No dynamic distribution group found.");
        }

        for (IDynamicPlugin dynamicPlugin : allUsersDynamicPlugins) {
            boolean registed = false;
            for (IDynamicDistributionsGroup dynDistriGroup : dynDistriGroups) {
                try {
                    if (dynDistriGroup.canRegist(dynamicPlugin, monitor)) {
                        dynDistriGroup.unregist(dynamicPlugin, monitor);
                        registed = true;
                    }
                } catch (Throwable e) {
                    ExceptionHandler.process(e);
                }
            }
            if (!registed) {
                ExceptionHandler.process(new Exception(
                        "Can't regist dynamic distribution: " + dynamicPlugin.getPluginConfiguration().getTemplateId()));
            }
        }

        if (cleanCache) {
            cleanCache();
        }

    }

    public void cleanCache() throws Exception {

        // 1. reset modulesNeeded cache
        getLibrariesService().resetModulesNeeded();

    }

    private static ILibrariesService getLibrariesService() {
        return (ILibrariesService) GlobalServiceRegister.getDefault().getService(ILibrariesService.class);
    }

    private String getUsersFolderPath() {
        return USERS_DISTRIBUTIONS_ROOT_FOLDER;
    }
}
