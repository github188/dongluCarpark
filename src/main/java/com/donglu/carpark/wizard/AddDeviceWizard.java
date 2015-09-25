package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;


public class AddDeviceWizard extends Wizard implements AbstractWizard{
	AddDeviceModel model;
	
	public AddDeviceWizard(AddDeviceModel model) {
		this.model=model;
		setWindowTitle("添加固定用户");
	}

	@Override
	public void addPages() {
		addPage(new AddDeviceWizardPage(model));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public Object getModel() {
		
		return model;
	}

}