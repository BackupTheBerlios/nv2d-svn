package nv2d.render;

public class RenderSettings {
	public static final int ANTIALIAS = 0;
	public static final int SHOW_LABELS = 1;
	public static final int SHOW_STRESS = 2;
	public static final int SHOW_LENGTH = 3;

	public static final String TYPE_BOOLEAN = "bool";

	/* uniqueID, description, setting */
	static String [][] _settings = {
		{/*antialias*/ TYPE_BOOLEAN, "f", "Anti-alias rendering."},
		{/*show_arrows*/ TYPE_BOOLEAN, "t", "Show direction arrows on edges."},
		{/*show_einfo*/ TYPE_BOOLEAN, "t", "Show selected edge information and statistics."},
		{/*show_vinfo*/ TYPE_BOOLEAN, "t", "Show selected vertex information and statistics."},
		{/*show_ginfo*/ TYPE_BOOLEAN, "t", "Show graph information and statistics."},
		{/*show_ginfo*/ TYPE_BOOLEAN, "t", "Show graph information and statistics."}
		//{/**/, "", ""}
		//{/**/, "", ""}
	};

	public void setBoolean(int setting, boolean value) {
		if(setting < 0 || setting >= _settings.length || !_settings[setting][0].equals(TYPE_BOOLEAN)) {
			// programming error
			System.err.print("RenderSettings.setBoolean(int, boolean): index out of range or type not boolean.");
			return;
		}
		_settings[setting][1] = (value ? "t" : "f");
	}

	public boolean getBoolean(int setting) {
		if(setting < 0 || setting >= _settings.length || !_settings[setting][0].equals(TYPE_BOOLEAN)) {
			// programming error
			System.err.print("RenderSettings.setBoolean(int, boolean): index out of range or type not boolean.");
			return false;
		}
		return _settings[setting][1].equals("t") ? true : false;
	}
}
