package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import data.*;
import gui.nav.*;

import doe.*;

public class OrderedResultPanel extends JPanel
{
	private NavPanel navPanel;
	private OrderedResult order;
	
	private TwopointPWDPanel pwdPanel;
	
	private JTabbedPane tabs = new JTabbedPane();
	
	private JButton generate = new JButton("Generate Map");
	
	public OrderedResultPanel(OrderedResult order, NavPanel navPanel)
	{
		this.order = order;
		this.navPanel = navPanel;
		
//		JScrollPane sp1 = new JScrollPane(getTextArea(order.tp1.toString()));
		JScrollPane sp2 = new JScrollPane(getTextArea(order.tp2.toString()));
		JScrollPane sp3 = new JScrollPane(getTextArea(order.sm1.toString()));
		JScrollPane sp4 = new JScrollPane(getTextArea(order.sm2.toString()));
		JScrollPane sp5 = new JScrollPane(getTextArea(order.phases.toString()));
		
		pwdPanel = new TwopointPWDPanel(order);
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.add(pwdPanel);
		
		tabs.addTab("twopoint.pwd", p1);
		tabs.addTab("twopoint.out", sp2);
//		tabs.addTab("sim.txt", sp3);
//		tabs.addTab("sim.out", sp4);		
//		tabs.addTab("Phases", sp5);
		
		setLayout(new BorderLayout());
		add(new GradientPanel("Ordered Results"), BorderLayout.NORTH);
		add(tabs);
	}
	
	private JTextArea getTextArea(String str)
	{
		JTextArea text = new JTextArea(str);
		
		text.setFont(new Font("Monospaced", Font.PLAIN, 11));;
		text.setMargin(new Insets(2, 5, 2, 5));
		
		return text;
	}
}