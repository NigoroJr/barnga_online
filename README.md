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
### Dependencies
These are the dependencies for compiling and running the server.

* JDK (1.7 used for testing)
* Apache Maven (3.0.4 used for testing)
* Web server to host the client web pages

The following browsers has been used to test the functionalities of the
program.

* Google Chrome
* Firefox

### Installing

1. Clone the repository
    * `git clone https://github.com/NigoroJr/culturecode`
2. Go into the `server` directory
    * `cd server`
3. Run Apache Maven
    * `mvn install`
4. Edit `server/culturecode.conf` and change the host address
4. Run the program
    * `mvn exec:java`
5. Type `q` to quit the server
6. Edit `client/client.js` and change `localhost` to whatever your webpage's
   address is

## Configurations
Currently, adding a TOML file in the `server` directory allows customizing
basic behavior of the game. For example, the world size, number of teams, and
how each team can see other teams/food can be defined in this file. See the
provided `sample.toml` to see the various parameters.

## TODO
* detailed installation guide
* configurations
* limitations

## License
MIT License
