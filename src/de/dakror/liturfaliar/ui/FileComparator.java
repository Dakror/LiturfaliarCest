package de.dakror.liturfaliar.ui;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File>
{
	public int compare(File p1, File p2)
	{
		return (p1.isDirectory() && p2.isDirectory()) ? -1 : 1;
	}
}
