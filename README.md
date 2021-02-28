## The Snake
One of my first games, writen solely using the deffault Java libraries. The GUI is created using the Swing library. The whole game is separeted into 5 different classes, each serving a different purpose:

* SnakePanel - A class, that is responsible for drawing out the game field. It is also responsible for drawing the text at the wellcome screen, the number counter at the start of every game and the game over text. It draws the text by overriding the paint() method of JComponent, and using the comp2d object.

* SettingsPanel - A class, that like the previous, extends JPanel, but this one, as the name states handles the settings screen. The class itself is simpler compared to the other ones, since it doesent have much functionality (yet).

* InfoPanel - A class, which also extebds JPanel. It hold the main information about the game. It's text is fully drawn out by overriding the painComponent() method (no JComponents were used for drawing it). It also has one thread object that is responsible for the rainbow effect on the developer's name. The thread is active only when the info page is opened and is shut down when the user gets off the page.

* SnakeBrain - A class that is responsible for most of the logic inside of the game. It's responsible for alot of stuff like generating the snake on the wollcome screen and in-game, mooving it in both as well, extending the snake when the apple has been eaten and ends the game if the snake bites its own tail. The snake's head and tail coordinates are being stored inside of HashMaps (one for the X and one for the Y coordinate), which makes it easy to move the snake in all directions (every part of the snake follows the next, starting from the head). This class is also responsible for handling all of the user input, and it does so by implementing the KeyListener and ActionListener interfaces. It also has one thread, which turns the snake arround when the KeyPressed method recives an event.

* SnakeFrame - The class that assembles everything together. This is the class responsible for the window of the game appering on the screen.