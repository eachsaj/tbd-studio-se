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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.hadoop.distribution.dynamic.AbstractDynamicDistributionsGroup;
import org.talend.hadoop.distribution.dynamic.DynamicDistributionManager;
import org.talend.repository.hadoopcluster.i18n.Messages;
import org.talend.repository.hadoopcluster.ui.dynamic.DynamicBuildConfigurationWizard;

/**
 * DOC cmeng  class global comment. Detailled comment
 */
public class DynamicDistributionsForm extends AbstractDynamicDistributionForm {

    private ComboViewer distributionCombo;

    private ComboViewer versionCombo;

    private Button buildConfigBtn;

    private Map<String, AbstractDynamicDistributionsGroup> dynDistriGroupMap = new HashMap<>();

    public DynamicDistributionsForm(Composite parent, int style) {
        super(parent, style);
        createControl();
        loadData();
        addListeners();
    }

    private void createControl() {
        Composite parent = this;

        Composite container = createFormContainer(parent);
        int ALIGN_HORIZON = getAlignHorizon();
        int ALIGN_VERTICAL_INNER = getAlignVerticalInner();

        Group group = new Group(container, SWT.NONE);
        group.setText(Messages.getString("DynamicDistributionsForm.group.existing")); //$NON-NLS-1$
        FormData formData = new FormData();
        formData.left = new FormAttachment(0);
        formData.top = new FormAttachment(0);
        formData.right = new FormAttachment(100);
        group.setLayoutData(formData);
        FormLayout formLayout = new FormLayout();
        formLayout.marginTop = 5;
        formLayout.marginBottom = 5;
        formLayout.marginLeft = 5;
        formLayout.marginRight = 5;
        group.setLayout(formLayout);

        Label distributionLabel = new Label(group, SWT.NONE);
        distributionLabel.setText(Messages.getString("DynamicDistributionsForm.label.existing.distribution")); //$NON-NLS-1$

        distributionCombo = new ComboViewer(group, SWT.READ_ONLY);
        distributionCombo.setContentProvider(ArrayContentProvider.getInstance());
        distributionCombo.setLabelProvider(new LabelProvider());
        formData = new FormData();
        formData.top = new FormAttachment(0);
        int distriAlignHorizon = ALIGN_HORIZON;
        formData.left = new FormAttachment(distributionLabel, distriAlignHorizon, SWT.RIGHT);
        formData.right = new FormAttachment(distributionLabel, distriAlignHorizon + 180, SWT.RIGHT);
        distributionCombo.getControl().setLayoutData(formData);

        formData = new FormData();
        formData.top = new FormAttachment(distributionCombo.getControl(), 0, SWT.CENTER);
        formData.left = new FormAttachment(0);
        distributionLabel.setLayoutData(formData);

        Label versionLabel = new Label(group, SWT.NONE);
        versionLabel.setText(Messages.getString("DynamicDistributionsForm.label.existing.version")); //$NON-NLS-1$
        formData = new FormData();
        formData.top = new FormAttachment(distributionCombo.getControl(), 0, SWT.CENTER);
        formData.left = new FormAttachment(distributionCombo.getControl(), ALIGN_HORIZON * 2, SWT.RIGHT);
        versionLabel.setLayoutData(formData);

        versionCombo = new ComboViewer(group, SWT.READ_ONLY);
        versionCombo.setContentProvider(ArrayContentProvider.getInstance());
        versionCombo.setLabelProvider(new LabelProvider());
        formData = new FormData();
        formData.top = new FormAttachment(versionLabel, 0, SWT.CENTER);
        formData.left = new FormAttachment(versionLabel, ALIGN_HORIZON, SWT.RIGHT);
        formData.right = new FormAttachment(100);
        versionCombo.getControl().setLayoutData(formData);

        buildConfigBtn = new Button(group, SWT.PUSH);
        buildConfigBtn.setText(Messages.getString("DynamicDistributionsForm.button.existing.buildConfig")); //$NON-NLS-1$
        formData = new FormData();
        formData.top = new FormAttachment(versionCombo.getControl(), ALIGN_VERTICAL_INNER, SWT.BOTTOM);
        formData.left = new FormAttachment(0);
        formData.right = new FormAttachment(0, getNewButtonSize(buildConfigBtn).x);
        buildConfigBtn.setLayoutData(formData);

    }

    private void addListeners() {
        buildConfigBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                DynamicBuildConfigurationWizard wizard = new DynamicBuildConfigurationWizard();
                WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        wizard);
                wizardDialog.create();
                if (wizardDialog.open() == IDialogConstants.OK_ID) {
                }
            }

        });
    }

    private void loadData() {
        try {
            dynDistriGroupMap.clear();
            DynamicDistributionManager dynDistriManager = DynamicDistributionManager.getInstance();
            List<AbstractDynamicDistributionsGroup> dynDistriGroups = dynDistriManager.getDynamicDistributionsGroups();
            if (dynDistriGroups != null && !dynDistriGroups.isEmpty()) {
                for (AbstractDynamicDistributionsGroup dynDistriGroup : dynDistriGroups) {
                    String displayName = dynDistriGroup.getDistributionDisplay();
                    dynDistriGroupMap.put(displayName, dynDistriGroup);
                }
                List<String> distributionDisplayNames = new LinkedList<>(dynDistriGroupMap.keySet());
                Collections.sort(distributionDisplayNames);
                distributionCombo.setInput(distributionDisplayNames);
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
    }

    private void refreshVersionList() {
        try {
            IStructuredSelection selection = (IStructuredSelection) distributionCombo.getSelection();
            if (selection != null) {
                Object selectedObject = selection.getFirstElement();
                if (selectedObject != null) {
                    AbstractDynamicDistributionsGroup dynDistriGroup = dynDistriGroupMap.get(selectedObject);
                    if (dynDistriGroup == null) {
                        throw new Exception(Messages.getString("DynamicDistributionsForm.exception.noDistributionGroupFound", //$NON-NLS-1$
                                dynDistriGroup));
                    }
                }
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
    }

}
