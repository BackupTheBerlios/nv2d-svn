package nv2d.ui;

import java.awt.*;
import javax.swing.*;

public class NGUI implements ViewInterface {
	private NController _ctl;
	private NMenu _menu;
	private Container _window;

	private Container _historyCtlPanel;
	private Container _layoutCtlPanel;

	private JSplitPane _rootPane;
	private JSplitPane _splitPaneV;

	private JTabbedPane _side;
	private JTabbedPane _center;
	private JTabbedPane _bottom;

	private JComponent _outTextBox, _errTextBox;
	private NPrintStream _err, _out;

	public NGUI(NController ctl, Container window) {
		_ctl = ctl;
		_window = window;
		
		_menu = new NMenu(_ctl, _ctl.getRenderBox());
		_layoutCtlPanel = new BottomPanel(_ctl);
		_historyCtlPanel = new HistoryUI(_ctl.getHistory());

		initComponents();
	}

	public Container gui() {
		return _rootPane;
	}

	public Container getWindow() {
		return _window;
	}
	
	public NMenu getMenu() {
		return _menu;
	}

	public Container getCenterPane() {
		return _center;
	}

	public Container getSidePane() {
		return _side;
	}

	public Container getBottomPane() {
		return _bottom;
	}
	
	public int addComponent(Container component, int location) {
		return 0;
	}

	public boolean removeComponent(Container component) {
		return false;
	}

	public boolean removeComponent(int id) {
		return false;
	}
	
	public void errorPopup(String title, String msg, String extra) {
		System.err.println(msg);
		JOptionPane.showMessageDialog(null,
			msg,
			title,
			JOptionPane.WARNING_MESSAGE);
	}

	private void initComponents() {
		_side = new JTabbedPane();
		_center = new JTabbedPane();
		_bottom = new JTabbedPane();

		_center.add("Main", _ctl.getRenderBox());
		_bottom.add("Layout", _layoutCtlPanel);
		_side.add("History", _historyCtlPanel);

		// Looks like
		// |----------|
		// |        | |
		// | center | |
		// |--------| |
		// |________|_|
		_splitPaneV = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				_center, _bottom);
		_rootPane= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				_splitPaneV, _side);

		_splitPaneV.setDividerSize(2);
		_splitPaneV.setResizeWeight(1.0);
		_rootPane.setDividerSize(2);
		_rootPane.setResizeWeight(0.0);

		// trap output to standard streams and display them in a text box
		JTextArea errTxt = new JTextArea();
		JTextArea outTxt = new JTextArea();
		JScrollPane sp1 = new JScrollPane(errTxt);
		JScrollPane sp2 = new JScrollPane(outTxt);
		_err = new NPrintStream(System.err);
		_out = new NPrintStream(System.out);
		_outTextBox = sp2;
		_errTextBox = sp1;
		System.setOut(_out);
		System.setErr(_err);
		_err.addNotifyClient(errTxt);
		_out.addNotifyClient(outTxt);
		_center.add("Output", sp2);
		_center.add("Errors", sp1);
	}
}
