package net.minecraft.launcher.ui.popups.profile;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.minecraft.launcher.OperatingSystem;
import net.minecraft.launcher.profile.Profile;

public class ProfileJavaPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 131578166330871594L;
	private final ProfileEditorPopup editor;
	private final JCheckBox javaPathCustom = new JCheckBox("Executable:");
	private final JTextField javaPathField = new JTextField();
	private final JCheckBox javaArgsCustom = new JCheckBox("JVM Arguments:");
	private final JTextField javaArgsField = new JTextField();

	public ProfileJavaPanel(ProfileEditorPopup editor) {
		this.editor = editor;

		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Java Settings (Advanced)"));

		createInterface();
		fillDefaultValues();
		addEventHandlers();
	}

	protected void addEventHandlers() {
		this.javaPathCustom.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				ProfileJavaPanel.this.updateJavaPathState();
			}
		});
		this.javaPathField.getDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void changedUpdate(DocumentEvent e) {
						ProfileJavaPanel.this.updateJavaPath();
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						ProfileJavaPanel.this.updateJavaPath();
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						ProfileJavaPanel.this.updateJavaPath();
					}
				});
		this.javaArgsCustom.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				ProfileJavaPanel.this.updateJavaArgsState();
			}
		});
		this.javaArgsField.getDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void changedUpdate(DocumentEvent e) {
						ProfileJavaPanel.this.updateJavaArgs();
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						ProfileJavaPanel.this.updateJavaArgs();
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						ProfileJavaPanel.this.updateJavaArgs();
					}
				});
	}

	protected void createInterface() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.anchor = 17;

		constraints.gridy = 0;

		add(this.javaPathCustom, constraints);
		constraints.fill = 2;
		constraints.weightx = 1.0D;
		add(this.javaPathField, constraints);
		constraints.weightx = 0.0D;
		constraints.fill = 0;

		constraints.gridy += 1;

		add(this.javaArgsCustom, constraints);
		constraints.fill = 2;
		constraints.weightx = 1.0D;
		add(this.javaArgsField, constraints);
		constraints.weightx = 0.0D;
		constraints.fill = 0;

		constraints.gridy += 1;
	}

	protected void fillDefaultValues() {
		String javaPath = this.editor.getProfile().getJavaPath();
		if (javaPath != null) {
			this.javaPathCustom.setSelected(true);
			this.javaPathField.setText(javaPath);
		} else {
			this.javaPathCustom.setSelected(false);
			this.javaPathField.setText(OperatingSystem.getCurrentPlatform()
					.getJavaDir());
		}
		updateJavaPathState();

		String args = this.editor.getProfile().getJavaArgs();
		if (args != null) {
			this.javaArgsCustom.setSelected(true);
			this.javaArgsField.setText(args);
		} else {
			this.javaArgsCustom.setSelected(false);
			this.javaArgsField.setText(Profile.DEFAULT_JRE_ARGUMENTS_64BIT);
		}
		updateJavaArgsState();
	}

	private void updateJavaArgs() {
		if (this.javaArgsCustom.isSelected())
			this.editor.getProfile().setJavaArgs(this.javaArgsField.getText());
		else
			this.editor.getProfile().setJavaArgs(null);
	}

	private void updateJavaArgsState() {
		if (this.javaArgsCustom.isSelected()) {
			this.javaArgsField.setEnabled(true);
			this.editor.getProfile().setJavaArgs(this.javaArgsField.getText());
		} else {
			this.javaArgsField.setEnabled(false);
			this.editor.getProfile().setJavaArgs(null);
		}
	}

	private void updateJavaPath() {
		if (this.javaPathCustom.isSelected())
			this.editor.getProfile().setJavaDir(this.javaPathField.getText());
		else
			this.editor.getProfile().setJavaDir(null);
	}

	private void updateJavaPathState() {
		if (this.javaPathCustom.isSelected()) {
			this.javaPathField.setEnabled(true);
			this.editor.getProfile().setJavaDir(this.javaPathField.getText());
		} else {
			this.javaPathField.setEnabled(false);
			this.editor.getProfile().setJavaDir(null);
		}
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.popups.profile.ProfileJavaPanel JD-Core Version:
 * 0.6.2
 */