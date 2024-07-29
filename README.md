<h1>A text editor GUI for Mario Party games</h1>
(currently only for Mario Party 4)

<h2>Implemented functionalities:</h2>

- Batch edit (loads a .dat file and displays all content, allows editing)

<h2>WIP:</h2>
- Change character names (loads .dat file from a folder and replaces character names with user input)<br>
- Change minigame names (defining arrays for different sections)<br>
- BMP edit for GCRebuilder (adding GameCube hex header to a user-made bmp file)<br>

![App screenshot](/src/main/resources/mpeditor_screenshot.png?raw=true "App screenshot")

<br>
<h2>How it works</h2>
The batch edit mode allows users to load a .dat file, reads the content and translates it to text using Mario Party-specific encoding.<br>
It separates editable content (strings) from non-editable content (data and pointers for the game).<br>
<br>
Non-editable character: *<br>
Null character (0x00): _<br>
String separator (0x0B): |<br>
<br>
If some editable characters are missing, you can add them to the marioPartyMapping hash map in …/model/HexFileUtils.java.<br>
<br>
Batch edit truncates strings that are longer than the original to avoid issues in game and adds null padding if the new string is shorter.<br>
