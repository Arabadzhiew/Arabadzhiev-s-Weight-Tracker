# Arabadzhiev's Weight tracker - Track your weight like never before!


## To run this application, download the .exe installer inside of this repository, then run it and configure to your liking. When you are done, shortcuts will be created on your desktop and start menu, use them to access the application.

![Arabadzhiev's Weight Tracker](https://i.imgur.com/X5KWa5j.png)

## This application was built using:
* Eclipse IDE Version: 2020-12 (4.18.0);
* Java SE JDK 15.0.1;
* JavaFX SDK 11.0.2;
* Apache Maven 3.6.3;
* JLink;
* JPackage(from JDK 16).

## What does this application do?
It does everything, that you would ever need from a weight tracker. It does:
* Keep track of your weight (obviously);
* Represent your weight inside of a graph. You can set up the duration of the graph according to your needs;
* Give information about your weight, according to the graph;
* List all of your weight entries, inside of a table-like list;
* Convert all of your weight entries, and display them inside of a average table/spreadsheet, which you can configure to your liking (it can convert your entries into rows of weeks, months or years).
 
However it can't:
* Make you lose OR gain weight;
* Give you a six pack;
* Make you bench 100 kg - Sorry.

## Why was this application created?

Like many others who do sports, I keep track of my weight. Before creating this program, I used a bunch of different tools to track it, and it was a hassle. And that's how the idea behind it was born. I decided that I will combine everything that I need inside of a single app, so I would no longer have to spend half of my day tracking my weight.

## About the code:
This is a modular project. It is almost entirely written in Java. The Java code consists of:
* MainWindow.java: This class is the entry point of the program. It is also the biggest of all classes, consisting of around 90% of all the code. It is responsible for displaying the main window, all of it's nodes and it is also responsible for pretty much all the program's logic. ;
* ConfirmationBox.java: This class is responsible for the confirmation box window, which is shown when necessary (example: when you want to remove an entry for a set date). ; 
* AverageTableRow.java: This class is used to create row objects for the average table inside of the main window. 

\
However, there is also some non-Java code. That code is:
* SceneTheame.css: This CSS file, which is located inside of the resources folder, is responsible for most of the program's design. It is there for a reason - by using CSS there are alot more customizations available, and it also divides the code in different part, which by itself makes it more reliable. ;
* pom.xml: The Maven xml file, which is responsible for everything Maven-related (dependencies, plugins, etc.).

## How was the JPackage installer created?

### This section is intended to serve as a reference for anyone that is looking for a "how to" regarding the creation of a JPackage installer. 

To start with, since there is a bug inside of JDK 15's JPackage, which prevents the application from running after distributing on other machines, it is recommended to use JDK 16 (or later) for creating a installer for your application. \
The installer of this program was created, using an already existing JLink runtime image, which was created using Maven's javafx-maven-plugin (version 0.0.4). After creating the image inside of the folder where it is located using command prompt the following command was used: 
```
jpackage --name Arabadzhiev\'s\ Weight\ Tracker --description "JavaFX\ application\ that\ gets\ entries\ of\ your\ daily\ weight\ and\ represents\ them\ in\ visual\ format(s)" --vendor "Petar Z. Arabadzhiev" --copyright "© 2021 Petar Z. Arabadzhiev" --app-version 1.0.4 --dest D:\\Java\\Projects\\AverageWeightTracker --runtime-image AWT_Image --module MeineFitnessPal/com.petar.pal.MainWindow --icon C:\\Users\\Petar\\Desktop\\fitnessIcon.ico --install-dir Arabadzhiev\'s\ WT --win-dir-chooser --win-shortcut --win-menu --win-menu-group Arabadzhiev\ Apps
```
The command above is an example. If you intend to use it you should modify it according to your project.