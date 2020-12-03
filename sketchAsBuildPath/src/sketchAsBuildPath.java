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

// all other import is replaced by direct def. calls

import static processing.app.I18n.tr; // translate (multi language support)
import com.manicken.CustomMenu;
import com.manicken.Reflect;
/**
 * 
 */
public class sketchAsBuildPath implements processing.app.tools.Tool {
	
	boolean useSeparateExtensionsMainMenu = true;
	boolean activated = false;
	String sketchBuildDirName = "build";

	processing.app.Editor editor;// for the plugin
	processing.app.Sketch sketch;
	CustomMenu customMenu = null;
	//javax.swing.JMenu toolsMenu; // for the plugin, uses reflection to get
	
	String thisToolTitle = "Sketch As Build Path";
	//String thisToolMenuTitle = "(Activate/Deactivate)";
	
	public String getMenuTitle() {// required by tool loader
		return thisToolTitle;// + " " + thisToolMenuTitle;
	}

	public void init(processing.app.Editor editor) { // required by tool loader
		this.editor = editor;

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
	 *  replaced by Custom Menu
	 */
	public void run() {

	}

	/**
	 * This is the code that runs after the Arduino IDE GUI has been loaded
	 */
	private void init() {
		System.out.println("\n*** starting " + thisToolTitle + " ***\n");
		sketch = this.editor.getSketch(); // this must be here because in init(processing.app.Editor editor) it's not yet initiated

		activated = processing.app.PreferencesData.getBoolean("manicken.sketchAsBuildPath.activated", activated); // default value is defined at top.

		customMenu = new CustomMenu(this.editor, thisToolTitle, 
				new javax.swing.JMenuItem[] {
					 CustomMenu.Item("Activate", event -> Activate())
					,CustomMenu.Item("Deactivate", event -> Deactivate())
					,CustomMenu.Item("Clear Build", event -> ClearBuild(true))
					//,CustomMenu.Item("Settings", event -> ShowSettingsDialog())
				});
			customMenu.Init(useSeparateExtensionsMainMenu);

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
		
		try {
			java.nio.file.Files.createDirectories(buildPath.toPath());
			// this field is that the compiler uses by sketch.getBuildPath()
			if (!Reflect.SetField("buildPath", sketch, buildPath))
			{
				System.err.println("\n!!!" + thisToolTitle + "NOT Activated !!!\n");
				return;
			}
			//PreferencesData.set("build.path", newBuildPath); // never have to set this
			System.out.println("\nbuild.path is set to: " + buildPath.toPath());

			System.out.println("    !!!" + thisToolTitle + " Activated !!!\n");

			processing.app.PreferencesData.setBoolean("manicken.sketchAsBuildPath.activated", true);
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace();
			System.err.println("\n!!!" + thisToolTitle + "NOT Deactivated !!!\n");
		}
		
		
	}

	private void Deactivate()
	{
		ClearBuild(false); // 
		// first set to null
		if (!Reflect.SetField("buildPath", sketch, null))
		{
			System.err.println("\n!!!" + thisToolTitle + "NOT Deactivated !!!\n");
			return;
		}
		// when the internal field is null this will reset to default
		try { 
			java.io.File buildPath = sketch.getBuildPath(); 
			System.out.println("\nbuild.path is set to: " + buildPath.toPath());
			System.out.println("    !!!" + thisToolTitle + " Deactivated !!!\n");

			processing.app.PreferencesData.setBoolean("manicken.sketchAsBuildPath.activated", false);
		} 
		catch (java.io.IOException ioe) {
			ioe.printStackTrace();
			System.err.println("\n!!!" + thisToolTitle + "NOT Deactivated !!!\n");
		}
	}

	private void ClearBuild(boolean recreateFolder)
	{
		try {
			java.io.File buildPath = sketch.getBuildPath(); 
			processing.app.helpers.FileUtils.recursiveDelete(buildPath);
			if (recreateFolder)
				java.nio.file.Files.createDirectories(buildPath.toPath());
			System.err.println("\n!!! Build Is now clean\n");
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace();
			System.err.println("\n!!!" + thisToolTitle + " Could not clean the build output !!!\n");
		}
	}
}
