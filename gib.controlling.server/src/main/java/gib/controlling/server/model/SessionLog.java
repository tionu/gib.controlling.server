package gib.controlling.server.model;

import javax.swing.table.DefaultTableModel;

public class SessionLog extends DefaultTableModel {

	public SessionLog() {
		super(new String[][] { { "-", "-", "-" }, }, new String[] { "Login:", "Keep Alive:", "Minutes:" });
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	Class[] columnTypes = new Class[] { String.class, String.class, String.class };

	@Override
	public Class getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}

	boolean[] columnEditables = new boolean[] { false, false, false };

	@Override
	public boolean isCellEditable(int row, int column) {
		return columnEditables[column];
	}

}
