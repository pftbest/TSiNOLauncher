package net.minecraft.launcher.ui.bottombar;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

public abstract class SidebarGridForm extends JPanel {
	protected void createInterface() {
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		setLayout(layout);

		populateGrid(constraints);
	}

	protected abstract void populateGrid(
			GridBagConstraints paramGridBagConstraints);

	protected <T extends Component> T add(T component,
			GridBagConstraints constraints, int x, int y, int weight, int width) {
		return add(component, constraints, x, y, weight, width, 10);
	}

	protected <T extends Component> T add(T component,
			GridBagConstraints constraints, int x, int y, int weight,
			int width, int anchor) {
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.weightx = weight;
		constraints.weighty = 1.0D;
		constraints.gridwidth = width;
		constraints.anchor = anchor;

		add(component, constraints);
		return component;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.bottombar.SidebarGridForm JD-Core Version: 0.6.2
 */