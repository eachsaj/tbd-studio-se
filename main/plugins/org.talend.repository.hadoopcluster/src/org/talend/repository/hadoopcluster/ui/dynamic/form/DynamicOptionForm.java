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
package org.talend.repository.hadoopcluster.ui.dynamic.form;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.runtime.dynamic.IDynamicPlugin;
import org.talend.core.runtime.dynamic.IDynamicPluginConfiguration;
import org.talend.designer.maven.aether.IDynamicMonitor;
import org.talend.hadoop.distribution.dynamic.DynamicDistributionManager;
import org.talend.hadoop.distribution.dynamic.IDynamicDistributionsGroup;
import org.talend.hadoop.distribution.dynamic.util.DynamicDistributionComparator;
import org.talend.repository.hadoopcluster.i18n.Messages;
import org.talend.repository.ui.login.LoginDialogV2;

/**
 * DOC cmeng  class global comment. Detailled comment
 */
public class DynamicOptionForm extends AbstractDynamicDistributionForm {

    private Button newConfigBtn;

    private Button editExistingConfigBtn;

    private Button importConfigBtn;

    private Text configNameText;

    private ComboViewer existingConfigsComboViewer;

    private Text importConfigText;

    private Button importConfigBrowseBtn;

    private Text descriptionText;

    private Composite newConfigGroup;

    private Composite editExistingGroup;

    private Composite importConfigGroup;

    private IDynamicDistributionsGroup dynamicDistributionsGroup;

    private Set<String> existingConfigurationNames;

    public DynamicOptionForm(Composite parent, int style, IDynamicDistributionsGroup dynamicDistributionsGroup,
            IDynamicMonitor monitor) {
        super(parent, style);
        this.dynamicDistributionsGroup = dynamicDistributionsGroup;
        createControl();
        initData(monitor);
        addListeners();
    }

    protected void createControl() {
        Composite parent = this;

        Composite container = createFormContainer(parent);

        int ALIGN_VERTICAL = getAlignVertical();
        int ALIGN_VERTICAL_INNER = getAlignVerticalInner();
        int ALIGN_HORIZON = getAlignHorizon();
        int HORZON_WIDTH = getHorizonWidth();

        newConfigBtn = new Button(container, SWT.RADIO);
        newConfigBtn.setText(Messages.getString("DynamicOptionForm.form.newConfigBtn")); //$NON-NLS-1$
        FormData formData = new FormData();
        formData.top = new FormAttachment(0);
        formData.left = new FormAttachment(0);
        formData.right = new FormAttachment(100);
        newConfigBtn.setLayoutData(formData);

        newConfigGroup = new Composite(container, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(newConfigBtn, ALIGN_VERTICAL_INNER, SWT.BOTTOM);
        formData.left = new FormAttachment(newConfigBtn, 0, SWT.LEFT);
        formData.right = new FormAttachment(newConfigBtn, 0, SWT.RIGHT);
        newConfigGroup.setLayoutData(formData);
        newConfigGroup.setLayout(new FormLayout());

        configNameText = new Text(newConfigGroup, SWT.BORDER);
        formData = new FormData();
        formData.top = new FormAttachment();
        formData.left = new FormAttachment(0, HORZON_WIDTH);
        formData.right = new FormAttachment(100);
        configNameText.setLayoutData(formData);

        Label nameLabel = new Label(newConfigGroup, SWT.NONE);
        nameLabel.setText(Messages.getString("DynamicOptionForm.form.nameLabel")); //$NON-NLS-1$
        formData = new FormData();
        formData.top = new FormAttachment(configNameText, 0, SWT.CENTER);
        formData.right = new FormAttachment(configNameText, -1 * ALIGN_HORIZON, SWT.LEFT);
        nameLabel.setLayoutData(formData);

        editExistingConfigBtn = new Button(container, SWT.RADIO);
        editExistingConfigBtn.setText(Messages.getString("DynamicOptionForm.form.editExistingConfigBtn")); //$NON-NLS-1$
        formData = new FormData();
        formData.top = new FormAttachment(newConfigGroup, ALIGN_VERTICAL, SWT.BOTTOM);
        formData.left = new FormAttachment(newConfigGroup, 0, SWT.LEFT);
        formData.right = new FormAttachment(newConfigGroup, 0, SWT.RIGHT);
        editExistingConfigBtn.setLayoutData(formData);

        editExistingGroup = new Composite(container, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(editExistingConfigBtn, ALIGN_VERTICAL_INNER, SWT.BOTTOM);
        formData.left = new FormAttachment(editExistingConfigBtn, 0, SWT.LEFT);
        formData.right = new FormAttachment(editExistingConfigBtn, 0, SWT.RIGHT);
        editExistingGroup.setLayoutData(formData);
        editExistingGroup.setLayout(new FormLayout());

        existingConfigsComboViewer = new ComboViewer(editExistingGroup, SWT.READ_ONLY);
        existingConfigsComboViewer.setContentProvider(ArrayContentProvider.getInstance());
        existingConfigsComboViewer.setLabelProvider(new ExistingConfigsLabelProvider());
        formData = new FormData();
        formData.top = new FormAttachment(0);
        formData.left = new FormAttachment(0, HORZON_WIDTH);
        formData.right = new FormAttachment(100);
        existingConfigsComboViewer.getCombo().setLayoutData(formData);

        importConfigBtn = new Button(container, SWT.RADIO);
        importConfigBtn.setText(Messages.getString("DynamicOptionForm.form.importConfigBtn")); //$NON-NLS-1$
        formData = new FormData();
        formData.top = new FormAttachment(editExistingGroup, ALIGN_VERTICAL, SWT.BOTTOM);
        formData.left = new FormAttachment(editExistingGroup, 0, SWT.LEFT);
        formData.right = new FormAttachment(editExistingGroup, 0, SWT.RIGHT);
        importConfigBtn.setLayoutData(formData);

        importConfigGroup = new Composite(container, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(importConfigBtn, ALIGN_VERTICAL_INNER, SWT.BOTTOM);
        formData.left = new FormAttachment(importConfigBtn, 0, SWT.LEFT);
        formData.right = new FormAttachment(importConfigBtn, 0, SWT.RIGHT);
        importConfigGroup.setLayoutData(formData);
        importConfigGroup.setLayout(new FormLayout());

        importConfigText = new Text(importConfigGroup, SWT.BORDER);
        importConfigText.setEditable(false);

        importConfigBrowseBtn = new Button(importConfigGroup, SWT.PUSH);
        importConfigBrowseBtn.setText(Messages.getString("DynamicOptionForm.form.importConfig.browse")); //$NON-NLS-1$
        formData = new FormData();
        formData.top = new FormAttachment(0);
        formData.left = new FormAttachment(100, -1 * getNewButtonSize(importConfigBrowseBtn).x);
        formData.right = new FormAttachment(100);
        importConfigBrowseBtn.setLayoutData(formData);

        formData = new FormData();
        formData.top = new FormAttachment(importConfigBrowseBtn, 0, SWT.CENTER);
        formData.left = new FormAttachment(0, HORZON_WIDTH);
        formData.right = new FormAttachment(importConfigBrowseBtn, -1 * ALIGN_HORIZON, SWT.LEFT);
        importConfigText.setLayoutData(formData);

        descriptionText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
        formData = new FormData();
        formData.top = new FormAttachment(importConfigGroup, ALIGN_VERTICAL, SWT.BOTTOM);
        formData.left = new FormAttachment(importConfigGroup, 0, SWT.LEFT);
        formData.right = new FormAttachment(importConfigGroup, 0, SWT.RIGHT);
        formData.bottom = new FormAttachment(100);
        descriptionText.setLayoutData(formData);

        onNewConfigSelected(false);
        onEditExistingSelected(false);
        onImportConfigSelected(false);
    }

    protected void addListeners() {
        
        newConfigBtn.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                onNewConfigSelected(newConfigBtn.getSelection());
                updateButtons();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                onNewConfigSelected(newConfigBtn.getSelection());
                updateButtons();
            }

        });
        
        editExistingConfigBtn.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                onEditExistingSelected(editExistingConfigBtn.getSelection());
                updateButtons();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                onEditExistingSelected(editExistingConfigBtn.getSelection());
                updateButtons();
            }

        });

        importConfigBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onImportConfigSelected(importConfigBtn.getSelection());
                updateButtons();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                onImportConfigSelected(importConfigBtn.getSelection());
                updateButtons();
            }

        });

        configNameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateButtons();
            }

        });

        existingConfigsComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                existingConfigsComboViewer.getControl()
                        .setToolTipText(existingConfigsComboViewer.getCombo().getText());
                updateButtons();
            }

        });

        importConfigBrowseBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onImportConfigBrowseBtnSelected();
                updateButtons();
            }

        });

    }

    private void onNewConfigSelected(boolean selected) {
        newConfigGroup.setEnabled(selected);
        configNameText.setEnabled(selected);
        descriptionText.setEnabled(selected);
        if (selected) {
            configNameText.selectAll();
            configNameText.forceFocus();
        }
    }

    private void onEditExistingSelected(boolean selected) {
        editExistingGroup.setEnabled(selected);
        existingConfigsComboViewer.getControl().setEnabled(selected);
    }

    private void onImportConfigSelected(boolean selected) {
        importConfigGroup.setEnabled(selected);
        importConfigText.setEnabled(selected);
        importConfigBrowseBtn.setEnabled(selected);
    }

    private void onImportConfigBrowseBtnSelected() {
        FileDialog fileDialog = new FileDialog(getShell());
        fileDialog.setFilterExtensions(new String[] { "*.json" }); //$NON-NLS-1$
        String filePath = fileDialog.open();
        if (StringUtils.isNotEmpty(filePath)) {
            importConfigText.setText(filePath);
        }
    }

    private void initData(IDynamicMonitor monitor) {
        try {
            List<IDynamicPlugin> distriDynamicPlugins = new LinkedList<>();
            List<IDynamicPlugin> allBuildinDynamicPlugins = this.dynamicDistributionsGroup.getAllBuildinDynamicPlugins(monitor);
            if (allBuildinDynamicPlugins != null && !allBuildinDynamicPlugins.isEmpty()) {
                distriDynamicPlugins.addAll(allBuildinDynamicPlugins);
            }
            List<IDynamicPlugin> allUsersDynamicPlugins = DynamicDistributionManager.getInstance()
                    .getAllUsersDynamicPlugins(monitor);
            if (allUsersDynamicPlugins != null && !allUsersDynamicPlugins.isEmpty()) {
                List<IDynamicPlugin> filterDynamicPlugins = this.dynamicDistributionsGroup
                        .filterDynamicPlugins(allUsersDynamicPlugins, monitor);
                if (filterDynamicPlugins != null && !filterDynamicPlugins.isEmpty()) {
                    distriDynamicPlugins.addAll(filterDynamicPlugins);
                }
            }

            Collections.sort(distriDynamicPlugins, Collections.reverseOrder(new DynamicDistributionComparator()));
            existingConfigsComboViewer.setInput(distriDynamicPlugins);
            if (0 < distriDynamicPlugins.size()) {
                existingConfigsComboViewer.setSelection(new StructuredSelection(distriDynamicPlugins.get(0)));
            }

        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
    }

    private boolean checkNewConfigNameValid() {
        if (!configNameText.isEnabled()) {
            configNameText.setBackground(null);
            return true;
        }
        String configName = configNameText.getText();
        if (configName.trim().isEmpty()) {
            String errorMessage = Messages.getString("DynamicDistributionsForm.newConfigName.check.empty"); //$NON-NLS-1$
            showMessage(errorMessage, WizardPage.ERROR);
            configNameText.setBackground(LoginDialogV2.RED_COLOR);
            configNameText.setToolTipText(errorMessage);
            return false;
        }
        try {
            if (isConfigurationNameExist(configName)) {
                String errorMessage = Messages.getString("DynamicDistributionsForm.newConfigName.check.exist", configName); //$NON-NLS-1$
                showMessage(errorMessage, WizardPage.ERROR);
                configNameText.setBackground(LoginDialogV2.RED_COLOR);
                configNameText.setToolTipText(errorMessage);
                return false;
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            showMessage(errorMessage, WizardPage.ERROR);
            configNameText.setBackground(LoginDialogV2.RED_COLOR);
            configNameText.setToolTipText(errorMessage);
            return false;
        }
        configNameText.setBackground(null);
        configNameText.setToolTipText(configNameText.getText());
        return true;
    }

    private boolean isConfigurationNameExist(String name) throws Exception {
        if (existingConfigurationNames == null) {
            existingConfigurationNames = new HashSet<>();
            IDynamicMonitor monitor = new IDynamicMonitor() {

                @Override
                public void writeMessage(String message) {
                    // nothing to do
                }
            };
            DynamicDistributionManager dynDistrManager = DynamicDistributionManager.getInstance();
            List<IDynamicPlugin> allBuildinDynamicPlugins = dynDistrManager.getAllBuildinDynamicPlugins(monitor);
            if (allBuildinDynamicPlugins != null && !allBuildinDynamicPlugins.isEmpty()) {
                Iterator<IDynamicPlugin> iter = allBuildinDynamicPlugins.iterator();
                while (iter.hasNext()) {
                    IDynamicPlugin dynPlugin = iter.next();
                    existingConfigurationNames.add(dynPlugin.getPluginConfiguration().getName());
                }
            }
        }
        return existingConfigurationNames.contains(name);
    }

    private boolean checkImportConfigText() {
        if (!importConfigText.isEnabled()) {
            importConfigText.setBackground(null);
            return true;
        }
        String importConfig = importConfigText.getText();
        if (StringUtils.isEmpty(importConfig)) {
            String errorMessage = Messages.getString("DynamicDistributionsForm.importConfigText.check.empty"); //$NON-NLS-1$
            showMessage(errorMessage, WizardPage.ERROR);
            importConfigText.setBackground(LoginDialogV2.RED_COLOR);
            importConfigText.setToolTipText(errorMessage);
            return false;
        }
        importConfigText.setBackground(null);
        importConfigText.setToolTipText(importConfigText.getText());
        return true;
    }

    @Override
    public boolean isComplete() {
        if (!checkNewConfigNameValid()) {
            return false;
        }
        if (!checkImportConfigText()) {
            return false;
        }
        showMessage(null, WizardPage.NONE);
        return true;
    }

    @Override
    public boolean canFlipToNextPage() {
        if (!isComplete()) {
            return false;
        }
        if (importConfigBtn.getSelection()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canFinish() {
        if (isComplete()) {
            if (importConfigBtn.getSelection()) {
                return true;
            }
        }
        return false;
    }

    private class ExistingConfigsLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {
            if (element instanceof IDynamicPlugin) {
                IDynamicPluginConfiguration pluginConfiguration = ((IDynamicPlugin) element).getPluginConfiguration();
                String name = pluginConfiguration.getName();
                return name;
            } else {
                return element == null ? "" : element.toString();//$NON-NLS-1$
            }
        }

    }

}
