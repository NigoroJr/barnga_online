# Culturecode
## Description
This game is meant to simulate advantages and disadvantages of knowing certain
information in a society or not. For example, when submersing yourself into a
different culture than what you belong to, you are sometimes unprivileged from
the starting point because of the fact that you don't know the implicit
"rules" of the society. In this game, by giving some players various
"privileges" and disadvantages to some, the players can experience the
frustration (or maybe the pleasure) of being in those positions.

This game was inspired by the game Barnga, created by Sivasailam (Thiagi)
Thiagarajan and Barbara Steinwachs (1990).

## Installation
### Requirements
* JDK (1.7 used for testing)
* Web server to host the client web pages

#### When compiling from source
* Apache Maven (3.0.4 used for testing)

The following browsers have been used to test the functionalities of the
program.

* Google Chrome
* Firefox

### Installing
#### Using the pre-built jar file
1. Download the jar file from
   [releases](https://github.com/NigoroJr/culturecode/releases/latest)

#### Compiling from source
1. `git clone https://github.com/NigoroJr/culturecode`
2. `cd culturecode/server`
3. `mvn assembly:single`

### Running
1. Edit `server/culturecode.conf`
2. Run the program
    * `java -jar path/to/jar/file`
3. Edit `client/client.js` and change `localhost` to whatever your webpage's
   address is, and put it to a publicly-accessibly directory.

## Configurations
Adding a TOML file in the same directory as the jar file allows customizing
basic behavior of the game. For example, the world size, number of teams, and
how each team can see other teams/food can be defined in this file. See the
provided `sample.toml` for various parameters.

## TODO
* detailed installation guide
* configurations
* limitations

## License
MIT License
