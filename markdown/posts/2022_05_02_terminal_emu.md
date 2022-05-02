# Summary: Anatomy of a Terminal Emulator

From the article [Anatomy of a Terminal Emulator](https://poor.dev/terminal-anatomy/) by Aram Drevekenin, which offers "a gentle and broad introduction to the terminal emulator as a platform for development."

## The Terminal, the Psuedoterminal, and the Shell
The terminal and shell are both executable programs. Modern examples of the terminal are alacritty, kitty and gnome-terminal. Three examples of shells are bash, zsh and fish.

The **shell** is a program which allows a user to interface with the computer's operating system. It can interpret commands passed to it, and makes decisions about running other programs and commands.

The **terminal's** job is purely to display what the shell tells it to.

The **pseudoterminal** (or PTY) forms a "bridge" between the terminal and the shall. It consists of two channels, STDIN and STDOUT. The terminal sends data to the STDIN channel, which the PTY forwards to the shell. The shell sends output to the STDOUT channel, which the PTY passes on to the terminal.

The PTY also _buffers_ input from the terminal. That is, it holds off on sending characters the user types into the shell until it receives an `<ENTER>` character. Then it sends all the buffered characters.

## ANSI Escape Codes: The Language of the Terminal
The shell can obviously send text to the terminal to display. However it can also give it various commands about _how_ to display those characters, such as the color and style, and also _where_ to display, by commands which move the cursor around the screen.

The 'language' of these commands are "ANSI Escape Codes". Here's a (slightly simplified) version of what the "fish" shell actually sends to the terminal on starting up, and what some of these code mean. In short, this sequence: Displays a welcome message; goes to a new line; sets the cursor; and prints the time on the right hand side of the screen.

```
^[(B^[[m\r^[[KWelcome to fish, the friendly interactive shell\r\n
^[]0;fish /home/aram^[[m^[[97m^[[46m⋊>^[[m^[[33m ~^[[m^[[K^[[67C^[[38;2;85;85;85m10:21:15
^[[m\r^[[5C
```

* `^[(B`: change char set to ASCII
* `^[[m`: reset styles
* `\r`: cursor to line start
* `^[[K`: clear line after cursor
* `\r\n` cursor to next line start
* `^[]0;fish /home/aram`: Change terminal title
* `^[[97m`: Foreground bright white
* `^[[46m`: Background Cyan
* `^[[67C`: cursor fwd 67 chars

This language can be used directly from the terminal, e.g `echo -e "I am some \033[38;5;9mred text!"`

## Terminal UIs
The above example sticks to the ASCII character set. There is also the "special characters" character set (`^[(0`). This set includes box drawing characters that can be used for drawing terminal UIs. For example `echo -e "\033(0lqqqk\nx   x\nmqqqj"` draws a box.

A key attribute of a modern UI is responsiveness - changing dynamically in response to users resizing windows. You can do this in a terminal UI by listing to the `SIGWINCH` signal, and using the `ioctl` syscall to get the new size of the terminal.

## Raw mode
Consider a scenario where you want to show a `Would you like to quit? [y/n]` message, and quit when the user hits `y`. We saw above that the PTY buffers until you hit enter, which you don't want in this case: you just want the user to hit "y" and exit the program.

You can accomplish this by putting the terminal into "raw mode". This does 3 things:

* STDIN is read char by char instead of buffering
* Echo is disabled, so the terminal doesn't print what you input
* Special processing is disabled (e.g. Ctrl C will no longer send SIGINT)

Raw mode is used in many modern terminals because it allows much more dynamism. For example, if you type an invalid command into a modern terminal, it might 'highlight' it in red to indicate that it's invalid _as you type_. It can do this because your terminal is sending data character by character.

## PTY as a file
Everything in Linux is a file, and the PTY is no exception. You can run `ps` from your terminal and you might see something like:

```
   PID TTY          TIME CMD
 426886 pts/1    00:00:01 zsh
 427154 pts/1    00:00:00 ps
```

The "TTY" here points to your PTY, which here would be the file `/dev/pts/1`. So you can do `echo "Hi!" > /dev/pts/1` and your terminal will assume that this is coming directly from the shell. You can also do `echo "Hi from new window!" > /dev/pts/1` from a _different_ terminal, and 'hijack' the terminal of someone else's process! This is one reason why you shouldn't have multiple people logging onto a production server as a single user.
