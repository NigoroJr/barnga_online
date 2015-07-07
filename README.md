# Culturecode
## Description
TODO

## Installation
### Dependencies
These are the dependencies for compiling and running the server.

* JDK (1.7 used for testing)
* Apache Maven (3.0.4 used for testing)

The following browsers has been used to test the functionalities of the
program.

* Google Chrome

### Installing

1. Clone the repository
    * `git clone https://github.com/NigoroJr/barnga_online`
2. Go into the `server` directory
    * `cd server`
3. Run Apache Maven
    * `mvn install`
4. Run the program
    * `mvn exec:java`
5. Type `q` to quit the server

## Configurations
Currently, `sample.toml` in the `server` directory allows customizing basic
behavior of the game. For example, the world size, number of teams, and how
each team can see other teams/food can be defined in this file. See the
provided `sample.toml` to see the various parameters.

## License
MIT License
