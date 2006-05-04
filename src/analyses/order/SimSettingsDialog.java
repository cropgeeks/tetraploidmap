package analyses.order;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import gui.*;

import doe.*;

class SimSettingsDialog extends JDialog implements ActionListener
{
	private JTextField rtField, tField, ntField, epsField;
	private JButton bOK;
	
	SimSettingsDialog(AppFrame appFrame)
	{
		super(appFrame, "Simulated Annealing Settings", true);
		
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
		
		add(layout.getPanel());
		
		bOK = new JButton("OK");
		bOK.addActionListener(this);
		
		JPanel p1 = new JPanel(new FlowLayout());
		p1.add(bOK);
		add(p1, BorderLayout.SOUTH);
		pack();
		
		setLocationRelativeTo(appFrame);
		setResizable(false);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			try
			{
				Prefs.sim_rt_value = Float.parseFloat(rtField.getText());
				Prefs.sim_t_value = Float.parseFloat(tField.getText());
				Prefs.sim_nt_value = Float.parseFloat(ntField.getText());
				Prefs.sim_eps_value = Float.parseFloat(epsField.getText());
				
				setVisible(false);
			}
			catch (Exception ex)
			{
				MsgBox.msg("Error in input: " + ex, MsgBox.ERR);
			}			
		}
	}
}