package com.hacktoolkit.android.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;

// LOL, I wrote my own FileUtils, but there is an existing one.
// Somehow I thought I was supposed to use android.os.FileUtils, which doesn't exist
// http://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FileUtils.html
// http://commons.apache.org/proper/commons-io/download_io.cgi

public class FileUtils {
	public static ArrayList<String> readLines(Activity activity, String filename) throws IOException {
		Context context = activity.getApplicationContext();
		ArrayList<String> lines = new ArrayList<String>();
		try {
			FileInputStream fin = context.openFileInput(filename);
			if (fin != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(fin);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String line;
				while (( line = bufferedReader.readLine() ) != null) {
					lines.add(line);
				}
				fin.close();
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		return lines;
	}
	
	public static int writeLines(Activity activity, String filename, ArrayList<String> lines) throws IOException {
		int linesWritten = 0;
		Context context = activity.getApplicationContext();
		try {
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			for (String line : lines) {
				// add terminal character so that it doesn't get written as one line
				fos.write((line + "\n").getBytes());
				++linesWritten;
			}
			fos.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		return linesWritten;
	}
}
