//This must be done before we try to connect
var screenWidth = window.innerWidth;
var screenHeight = window.innerHeight;

//This starts the server side of socket.io
var socket = io.connect('http://localhost:3000');

//Team Colors
var teamColors = ['#FF0000', '#ffff00', '#00ff00', '#0000ff'];

//Personalized for any screen size
var bufferX = screenWidth / 2;
var bufferY = screenHeight / 2;

//Coordinate Container Class
var coordContainer = {
  x : null,
  y : null
};

//Player Container Class
var playerContainer = {
  id : null,
  teamId : null,
  coord : coordContainer
};

//Food Container Class
var foodContainer = {
  id : null,
  team : null,
  coord : null
};

//Point Container Class
var pointContainer = {
  total : null,
  earners : null
};

//Message Player Coordinate Container
var mpcContainer = {
  player : null,
  newCoord : null
};

//Message Food Coordinate Container
var mfcContainer = {
  food : null,
  newCoord : null,
  gone : null
};

//Message Player Name Container
var mpnContainer = {
  playerName : null,
  teamId : null
};

//Message Point Update Container
var mpuContainer = {
  player : null,
  newPoint : null
};

//The playable area information
var worldInfo = {
  width : 3000,
  height : 3000
};

//The player's information
var playerInfo = {
  size : 25,
  speed : 2
};

//The food information
var foodInfo = {
  size : 12.5 //diameter
};

//The aspects of the grid
var gridInfo = {
  width : 25,
  height : 25
};

//Variables in order to ensure that the connection is open and starts the game
var connected = false;
var startGame = false;

//Variables to hold all of the current details about player and food
var players = {};
var food = {};
// Dummy data
var myPlayer= {id: 42, teamId: 42, coord: {x: 42, y: 42}};

var canvas = document.getElementById('gameCanvas');
canvas.width = worldInfo.width;
canvas.height = worldInfo.height;

var canvasContext = canvas.getContext('2d');

var keys = [];
window.addEventListener('keydown', function(e) {
  keys[e.keyCode] = true;
}, false);

window.addEventListener('keyup', function(e) {
  keys[e.keyCode] = false;
}, false);

/**
  INFO
  1. Checks if a directional key is pressed
  2. If a directional key is pressed then it contacts the server and processes a possible move
  */
function playerControls() {
  var change = false;
  var tempX = myPlayer.coord.x;
  var tempY = myPlayer.coord.y;

  if (keys[37] && validateMove(37)) {
    tempX = myPlayer.coord.x - playerInfo.speed;
    change = true;
  }
  if (keys[38] && validateMove(38)) {
    tempY = myPlayer.coord.y - playerInfo.speed;
    change = true;
  }
  if (keys[39] && validateMove(39)) {
    tempX = myPlayer.coord.x + playerInfo.speed;
    change = true;
  }
  if (keys[40] && validateMove(40)) {
    tempY = myPlayer.coord.y + playerInfo.speed;
    change = true;
  }

  if (change) {
    var mes = {
      player : myPlayer,
      newCoord : {x : tempX, y : tempY}
    };

    socket.emit('move', mes);
  }
}

function validateMove(num) {
  switch (num) {
    case 37:
      if (myPlayer.coord.x >= 1) {
        return true;
      }
      else {
        return false;
      }
      break;
    case 38:
      if (myPlayer.coord.y >= 1) {
        return true;
      }
      else {
        return false;
      }
      break;
    case 39:
      if (myPlayer.coord.x < worldInfo.width - playerInfo.size) {
        return true;
      }
      else {
        return false;
      }
      break;
    case 40:
      if (myPlayer.coord.y < worldInfo.height - playerInfo.size) {
        return true;
      }
      else {
        return false;
      }
      break;
  }
}

function drawPlayer(x, y, teamNum) {
  canvasContext.beginPath();
  canvasContext.fillStyle = teamColors[teamNum];
  canvasContext.lineWidth = '2';
  canvasContext.strokeStyle = 'black';
  canvasContext.rect(x, y, playerInfo.size, playerInfo.size);
  canvasContext.fillRect(x, y, playerInfo.size, playerInfo.size);
  canvasContext.stroke();
}

function drawFood(x, y, r, teamNum) {
  canvasContext.beginPath();
  canvasContext.arc(x, y, r, 0, 2*Math.PI);
  canvasContext.fillStyle = teamColors[teamNum];
  canvasContext.fill();
  canvasContext.lineWidth = 2;
  canvasContext.strokeStyle = 'black';
  canvasContext.stroke();
}

function clearCanvas() {
  canvasContext.rect(0, 0, worldInfo.width, worldInfo.height);
  canvasContext.fillStyle = 'white';
  canvasContext.fillRect(0, 0, worldInfo.width, worldInfo.height);
}

function drawGame() {

/**
  for (var i = 0; i <= worldInfo.width; i += gridInfo.width) {
    canvasContext.beginPath();
    canvasContext.moveTo(i, 0);
    canvasContext.lineTo(i, worldInfo.height);
    canvasContext.strokeStyle = 'gray';
    canvasContext.stroke();
  }
  for (var i = 0; i <= worldInfo.height; i += gridInfo.height) {
    canvasContext.beginPath();
    canvasContext.moveTo(0, i);
    canvasContext.lineTo(worldInfo.width, i);
    canvasContext.strokeStyle = 'gray';
    canvasContext.stroke();
  }

  //drawPlayer(myPlayer.coord.x, myPlayer.coord.y, myPlayer.teamId);

  //TODO draw players
  for (p in players) {
    var player = players[p];
    drawPlayer(player.coord.x, player.coord.y, player.teamId);
  }

  //TODO draw Food
  //**
  for (f in food) {
    var fd = food[f];
    drawFood(fd.coord.x, fd.coord.y, foodInfo.size, fd.team);
  }
  //*/

  //clearCanvas();

  canvasContext.translate(bufferX - myPlayer.coord.x, bufferY - myPlayer.coord.y);

  for (var i = 0; i <= worldInfo.width; i += gridInfo.width) {
    canvasContext.beginPath();
    canvasContext.moveTo(i, 0);
    canvasContext.lineTo(i, worldInfo.height);
    canvasContext.strokeStyle = 'gray';
    canvasContext.stroke();
  }
  for (var i = 0; i <= worldInfo.height; i += gridInfo.height) {
    canvasContext.beginPath();
    canvasContext.moveTo(0, i);
    canvasContext.lineTo(worldInfo.width, i);
    canvasContext.strokeStyle = 'gray';
    canvasContext.stroke();
  }


  for (f in food) {
    var fd = food[f];
    drawFood(fd.coord.x, fd.coord.y, foodInfo.size, fd.team);
  }

  for (p in players) {
    var player = players[p];
    drawPlayer(player.coord.x, player.coord.y, player.teamId);
  }

  drawPlayer(myPlayer.coord.x, myPlayer.coord.y, myPlayer.teamId);

  canvasContext.setTransform(1, 0, 0, 1, 0, 0);

}

socket.on('connect', function() {
  connected = true;
});

// When losing connection to server
socket.on('disconnect', function() {

});

// When some player disconnects
socket.on('disconnect', function(MessagePlayerId) {
  console.log("Player %d has disconnected\n", MessagePlayerId.id);
});

socket.on('gameStart', function() {
  console.log('Game has started');
  startGame = true;
});

socket.on('gameEnd', function() {
  console.log('Game has ended');
  startGame = false;
});

socket.on('playerId', function(MessagePlayerId) {
  myPlayer = MessagePlayerId;

  console.log("Got identity from server: Id = %d, Team ID = %d at (%d, %d) %o\n",
      myPlayer.id, myPlayer.teamId,
      myPlayer.coord.x, myPlayer.coord.y,
      MessagePlayerId);

});

/**
  INFO
  1. Updates a pre-existing player's coordinates
  2. Updates my Player's current positioning
  3. Adds a new player to the game if the player ID is not pre-existing
  4. If the server sends something nonsensical then an error message is printed to the console

  ASSUMPTIONS
  1. When the server has a new player to add to the game then it sends a player with a new and unique player ID

  DETAILS
  1. At game start all of the pertinent data will be sent through this function

  NOTE
  1. The 'players' array is an associative array therefore it becomes an object and loses all of it's array properties
  2. The first player sent when the game first starts is myPlayer

  TODO
 **COMPLETED** 1. A way to check if the new player is myPlayer or just a different user and add the new player to myPlayer if it is me
 2. If a player disconnects then player count needs to decrease
 3. myPlayer is a part of the players array. I don't want this to be there.
 */
socket.on('playerUpdate', function(MessagePlayerCoord) {
  var playerID = MessagePlayerCoord.player.id;

  if (playerID == myPlayer.id) {
    myPlayer.coord = MessagePlayerCoord.newCoord;
  }
  else {
    players[playerID] = MessagePlayerCoord.player;
    players[playerID].coord = MessagePlayerCoord.newCoord;
  }
});

/**
  INFO
  1. Updates the food content
  2. Removes food from the playing field

  DETAILS
  1. At game start a of the food will be sent via messages with gone == false
  */
socket.on('foodUpdate', function(MessageFoodCoord) {
  var f = MessageFoodCoord.food;
  var gone = MessageFoodCoord.gone;
  if (gone) {
    // Delete food from field
    delete food[f.id];
    console.log("Deleted food %d\n", f.id);
  }
  else {
    food[f.id] = f;
    console.log("Added food %d at (%d, %d): %o\n",
        food[f.id].id,
        food[f.id].coord.x,
        food[f.id].coord.y,
        food[f.id]);
  }
});

socket.on('pointsUpdate', function(mes) {
  var teamId = mes.teamId;
  var newPoint = mes.newPoint;
  document.getElementById(teamId).innerHTML = newPoint;
});

window.requestAnimationFrame = (function() {
  return window.requestAnimationFrame       ||
    window.webkitRequestAnimationFrame ||
    window.mozRequestAnimationFrame    ||
    function(callback) {
      // window.setTimeout(callback, 60);
      window.setTimeout(callback, 1000, 60);
    };
})();

(function animationLoop() {
  requestAnimationFrame(animationLoop);
  gameLoop();
})();

function gameLoop() {

  // if (connected) {
  if (startGame) {
    clearCanvas();
    playerControls();
    drawGame();
  }
  else {
    //TODO Lost
  }
  // }
  // else {
  //     //TODO disconnected
  // }

}

/**
  GAME DETAILS
  1. AT GAME START DATA FOR FOOD AND PLAYERS WILL ALL BE sent
  2. THE CLIENT ONLY RECIEVES PERTINENT DATA FOR THEIR PLAYERS
  3. NOT ALL OF THE FOOD IS SENT TO A SPECIFIC CLIENT, ONLY THE FOOD THAT THE CLIENT CAN SEE
  4. A PLAYER OF TEAM ONE IF HE CAN SEE OTHER PLAYERS WILL ONLY SEE ONE COLOR OF PLAYER
  */

/**
  NOTE
  1. MOVE PLAYER BY ONE GRID AT A TIME
  2. YOU NEED TO KEEP TRACK OF THE NUMBER OF PLAYERS
  3. PLAYER DISCONNECTS WILL BE SENT VIA A DIFFERENT METHOD THAT ISN'T IN THE CURRENT SERVER Application
  */

/**
  CRITICAL TODO
  1. FOR EACH PRESS OF AN ARROW KEY THEN THE USER MOVES ONE GRID SPACE
  2. NOT ALL OF THE FOOD IS VISIBLE AND THE FOOD THAT IS VISIBLE WILL BE OF THE SAME COLOR OR AT LEAST THE SERVER
  3. **COMPLETED**When connecting there are always two new players.... 6/16/2015
  4. THE GAME IS ENTIRELY RELIANT ON THERE BEING A MYPLAYER SET OTHERWISE IT WON'T DRAW ANYTHING
  */

/**
  TODO PROBLEMS (NON-CRITICAL ERRORS)
  1. Player can still be outside of the map if the start property is outside of the world
  2. Player can be outside of the map if the speed is greater than one pixel
  3. Infact player is leaving the map. Probably needs an approximate method to determine if the player will leave the map

*/

/**
  CONSIDERATION TODO
  1. Implement a checkerboard look in so that the user can tell that they are moving while they're in the middle of the map
  2. Diagonal Movement
  */

/**
  TODO ASK NAOKI
  1. Can two players who can't see each other occupy one space?
  2. CAN A PLAYER WHO IS SEEING A FOOD THAT DOES NOT ACTUALLY BELONG TO ITS TEAM BE EATEN BY THE PLAYER OR WILL IT REMAIN IN PLAY?
  */

/**
  DON'T HAVE A CLUE WHY A DRAW METHOD ISN'T WORKING... LOOK HERE
  1. THERE MUST BE VALUES FOR MYPLAYER
  2. DRAWCIRCLE DRAWS FROM THE CENTER
  */
