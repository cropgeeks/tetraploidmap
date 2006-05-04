package analyses.order;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import gui.*;

import doe.*;

public class OrderSettingsDialog extends JDialog implements ActionListener
{
	private JTabbedPane tabs;
	private JCheckBox cTwoPoint, cCustom, cRipple, cSim;
	private JTextField rtField, tField, ntField, epsField;
	
	private JButton bOK, bCancel;
	private boolean isOK = false;
	
	public OrderSettingsDialog(AppFrame appFrame)
	{
		super(appFrame, "Marker Ordering", true);
		
		add(new GradientPanel("Marker Ordering"), BorderLayout.NORTH);
		add(createControls(), BorderLayout.CENTER);
		add(createButtons(), BorderLayout.SOUTH);
		
		pack();
		
		setLocationRelativeTo(appFrame);
		setResizable(false);
		setVisible(true);
	}
	
	private JTabbedPane createControls()
	{
		tabs = new JTabbedPane();
		tabs.addTab("General Settings", null, createGenPanel(), null);
		tabs.addTab("Simulated Annealing Settings", null, createSimPanel(), null);
		tabs.setEnabledAt(1, cSim.isSelected());
		
		return tabs;
	}
	
	private JPanel createGenPanel()
	{
		cTwoPoint = new JCheckBox("1) Two-point linkage analysis (always runs)", true);
		cTwoPoint.addActionListener(this);
		cCustom = new JCheckBox("2) Initial-run 'custom' marker ordering (always runs)", true);
		cCustom.addActionListener(this);
		cRipple = new JCheckBox("3) Ripple ordering", Prefs.sim_run_ripple);
		cRipple.addActionListener(this);
		cSim = new JCheckBox("4) Simulated Annealing ordering", Prefs.sim_run_sim);
		cSim.addActionListener(this);
		
		DoeLayout layout = new DoeLayout();
		
		layout.add(new JLabel("Select which stages of marker ordering should "
			+ "run using the options below:"), 0, 0, 1, 1, new Insets(5, 5, 5, 5));
			
		layout.add(cTwoPoint, 0, 1, 1, 1, new Insets(5, 5, 5, 5));
		layout.add(cCustom, 0, 2, 1, 1, new Insets(0, 5, 5, 5));
		layout.add(cRipple, 0, 3, 1, 1, new Insets(0, 5, 5, 5));
		layout.add(cSim, 0, 4, 1, 1, new Insets(0, 5, 5, 5));
		
		return layout.getPanel();
	}
	
	private JPanel createButtons()
	{
		bOK = new JButton("OK");
		bOK.addActionListener(this);
		bCancel = new JButton("Cancel");
		bCancel.addActionListener(this);
		
		JPanel p1 = new JPanel(new GridLayout(1, 2, 5, 5));
		p1.add(bOK);
		p1.add(bCancel);
		
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		p2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p2.add(p1);
		
		return p2;
	}
	
	private JPanel createSimPanel()
	{		
		rtField = new JTextField("" + Prefs.sim_rt_value, 5);
		tField = new JTextField("" + Prefs.sim_t_value, 5);
		ntField = new JTextField("" + Prefs.sim_nt_value, 5);
		epsField = new JTextField("" + Prefs.sim_eps_value, 5);
		
		DoeLayout layout = new DoeLayout();
		layout.add(new JLabel("Temperature reduction factor: "),
			0, 0, 1, 1, new Insets(5, 5, 0, 5));
		layout.add(rtField, 1, 0, 1, 1, new Insets(5, 5, 0, 5));
		layout.add(new JLabel("Initial temperature: "),
			0, 1, 1, 1, new Insets(5, 5, 0, 5));
		layout.add(tField, 1, 1, 1, 1, new Insets(5, 5, 0, 5));
		layout.add(new JLabel("Number of iterations before temperature reduction: "),
			0, 2, 1, 1, new Insets(5, 5, 0, 5));
		layout.add(ntField, 1, 2, 1, 1, new Insets(5, 5, 0, 5));
		layout.add(new JLabel("Error tolerance for termination: "),
			0, 3, 1, 1, new Insets(5, 5, 5, 5));
		layout.add(epsField, 1, 3, 1, 1, new Insets(5, 5, 5, 5));
		
		return layout.getPanel();		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == cTwoPoint)
			cTwoPoint.setSelected(true);
		else if (e.getSource() == cCustom)
			cCustom.setSelected(true);
		
		else if (e.getSource() == cRipple && cRipple.isSelected() == false)
		{
			cSim.setSelected(false);
			tabs.setEnabledAt(1, false);
		}
		
		else if (e.getSource() == cSim)
		{
			if (cSim.isSelected())
				cRipple.setSelected(true);
			tabs.setEnabledAt(1, cSim.isSelected());
		}
		
		else if (e.getSource() == bOK)
		{
			// Only process simanneal settings if it was selected
			if (cSim.isSelected())
			{
				try
				{
					Prefs.sim_rt_value = Float.parseFloat(rtField.getText());
					Prefs.sim_t_value = Float.parseFloat(tField.getText());
					Prefs.sim_nt_value = Float.parseFloat(ntField.getText());
					Prefs.sim_eps_value = Float.parseFloat(epsField.getText());
				}
				catch (Exception ex)
				{
					tabs.setSelectedIndex(1);
					MsgBox.msg("Error in input: " + ex, MsgBox.ERR);
					return;
				}
			}
			
			Prefs.sim_run_ripple = cRipple.isSelected();
			Prefs.sim_run_sim = cSim.isSelected();
			
			isOK = true;
			setVisible(false);
		}
		
		else if (e.getSource() == bCancel)
			setVisible(false);
	}
	
	public boolean isOK()
		{ return isOK; }
}