# arduino IDE Sketch Build Path

This Extension Make It possible to have the compiler output the files<br>
to a sketch subfolder called build.<br>
This make it possible to save the build output for every sketch,
so that recompile is faster for all allready compiled sketches.

## Features

First all my extensions will use a new "main menubar" menu called Extensions.

In the above there is a item called "Sketch As Build Path"

That have the following features
* It's not using global preferences.txt build.path
  instead it changes the buildpath internally.
* preferences.txt is only used to store the state of this extension (activated or not) as boolean
* Activate (changes the build output path and create a new folder inside the sketch folder called build)
* Deactivate (removes the build folder and contents created by activate)
* Clear Build (removes the build folder and contents and then create a new build folder)

## Install

* global:<br>
&nbsp;&nbsp;download this repository by either Code-Download Zip or<br>
&nbsp;&nbsp;&nbsp;&nbsp;by git clone https://github.com/manicken/arduinoIDEsketchBuildPath.git<br>
&nbsp;&nbsp;then extract/open the repository<br>

* global (into sketchbook folder (defined in Arduino IDE - Preferenses):<br>
&nbsp;&nbsp;make a new folder in the above defined sketchbook folder<br>
&nbsp;&nbsp;called tools<br>
&nbsp;&nbsp;then copy the folder sketchAsBuildPath from the repository into this new "tools" folder.<br>

### Alternative install

* on windows / linux (into Arduino IDE install dir):<br>
&nbsp;&nbsp;copy folder sketchAsBuildPath to [Arduino IDE install location]/tools directory<br>
&nbsp;&nbsp;ex: /Arduino-1.8.13/tools<br>

* on mac (into Arduino IDE package):<br>
&nbsp;&nbsp;In Applications right click and click on "Show Package Contents", then browse Contents -> Java -> tools<br>
&nbsp;&nbsp;by holding the Option key(copy) drag folder sketchAsBuildPath from the downloaded repository to the open tools folder above<br>
&nbsp;&nbsp;select replace it you allready have an older version<br>

Last restart Arduino IDE.
(On mac you have to right click at the Arduino icon in the Dock and select Quit)

## Compiling

Download and Install Java SDK8 (1.8) 32bit<br>
(Arduino IDE uses Java 8 (1.8))<br>

two script is provided:<br>
&nbsp;&nbsp;for windows the .bat file<br>
&nbsp;&nbsp;for linux the .sh file<br>
&nbsp;&nbsp;for mac os maybe also the .sh file (have not tried it yet)<br>

## Release Notes

### 1.0.0

Initial release of arduinoIDEsketchBuildPath