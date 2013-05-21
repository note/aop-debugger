package helpers;

import java.io.File;

public class PathHelpers {
	public static String getUploadsURL() {
		return getPwdURL() + "uploads" + File.separator;
	}

	public static String getPwdURL() {
		return "file://" + System.getProperty("user.dir") + File.separator;
	}
}
