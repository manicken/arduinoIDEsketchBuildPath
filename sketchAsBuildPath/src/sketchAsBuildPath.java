/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2008 Ben Fry and Casey Reas
  Copyright (c) 2020 Jannik LS Svensson (1984)- Sweden

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.manicken;

// all import is replaced by direct def. calls

/**
 * 
 */
public class sketchAsBuildPath implements processing.app.tools.Tool {
	
	boolean useSeparateExtensionsMainMenu = true;
	boolean activated = false;
	String sketchBuildDirName = "build";

	processing.app.Editor editor;// for the plugin
	processing.app.Sketch sketch;
	
	String thisToolTitle = "Sketch As Build Path";
	String thisToolMenuTitle = "(Activate/Deactivate)";
	
	public String getMenuTitle() {// required by tool loader
		return thisToolTitle + " " + thisToolMenuTitle;
	}

	public void init(processing.app.Editor editor) { // required by tool loader
		this.editor = editor;
		sketch = this.editor.getSketch();

		// workaround to make sure that init is run after the Arduino IDE gui has loaded
		// otherwise any System.out(will never be shown at the init phase) 
		editor.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent e) {
			  init();
			}
		});
		
	}

	/**
	 *  required by tool loader (called when user presses the tools-"thisToolMenuTitle")
	 *  this will toggle if used or not
	 */
	public void run() {
		if (activated)
		{
			activated = false;
			Deactivate();
			System.out.println("!!!" + thisToolTitle + " Deactivated !!!");
		}
		else
		{
			activated = true;
			Activate();
			System.out.println("!!!" + thisToolTitle + " Activated !!!");
		}
		processing.app.PreferencesData.setBoolean("manicken.sketchAsBuildPath.activated", activated);
	}

	/**
	 * This is the code that runs after the Arduino IDE GUI has been loaded
	 */
	private void init() {
		System.out.println("\n*** starting " + thisToolTitle + " ***\n");

		activated = processing.app.PreferencesData.getBoolean("manicken.sketchAsBuildPath.activated", activated); // default value is defined at top.

		if (activated == true)
		{
			Activate();
		}
	}

	private void Activate()
	{
		String newBuildPath = sketch.getFolder() + "/" + sketchBuildDirName;

		// using code from Arduino IDE src @ Sketch.java -> public File getBuildPath()
		java.io.File buildPath = processing.app.BaseNoGui.absoluteFile(newBuildPath); 
		
		try { java.nio.file.Files.createDirectories(buildPath.toPath()); } catch (java.io.IOException ioe) { ioe.printStackTrace(); }
		
		// this field is that the compiler uses by sketch.getBuildPath()
		ReflectSetField("buildPath", sketch, buildPath); 

		//PreferencesData.set("build.path", newBuildPath); // never have to set this
		System.out.println("build.path is set to: " + buildPath.toPath());
	}

	private void Deactivate()
	{
		// first set to null
		ReflectSetField("buildPath", sketch, null); 
		
		// when the internal field is null this will reset to default
		try { sketch.getBuildPath(); } catch (java.io.IOException ioe) { ioe.printStackTrace(); }
	}

	/**
	 * Reflect Set Field wrapper method
	 */
	public void ReflectSetField(String name, Object obj, Object value) {
		try {
			java.lang.reflect.Field f = obj.getClass().getDeclaredField(name);
			f.setAccessible(true);
			f.set(obj, value);

		} catch (Exception e) {
			System.err.println("****************************************");
			System.err.println("************cannot reflect**************");
			System.err.println("****************************************");
			e.printStackTrace();
		}
	}
}
