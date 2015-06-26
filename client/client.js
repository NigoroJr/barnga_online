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
var myPlayer = {id: 42, teamId: 42, coord: {x: 42, y: 42}};

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
      if (myPlayer.coord.x >= playerInfo.size/2) {
        return true;
      }
      else {
        return false;
      }
      break;
    case 38:
      if (myPlayer.coord.y >= playerInfo.size/2) {
        return true;
      }
      else {
        return false;
      }
      break;
    case 39:
      if (myPlayer.coord.x < worldInfo.width - playerInfo.size/2) {
        return true;
      }
      else {
        return false;
      }
      break;
    case 40:
      if (myPlayer.coord.y < worldInfo.height - playerInfo.size/2) {
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
    drawFood(fd.coord.x , fd.coord.y, foodInfo.size, fd.team);
  }

  for (p in players) {
    var player = players[p];
    drawPlayer(player.coord.x - playerInfo.size / 2, player.coord.y - playerInfo.size / 2, player.teamId);
  }

  drawPlayer(myPlayer.coord.x - playerInfo.size / 2, myPlayer.coord.y - playerInfo.size / 2, myPlayer.teamId);

  canvasContext.setTransform(1, 0, 0, 1, 0, 0);

}

function toggleVisibility(id, state) {
  document.getElementById(id).style.visibility = state;
}

//**************** SPLASH CONNECTION ANIMATION ****************
var circleInfo = {
  smallSize : 25,
  largeSize : 35,
  smallBuffer : 20,
  largeBuffer : 25
}

var splashWidth = screenWidth - 20;
var splashHeight = screenHeight - 20;

var splashCanvas = document.getElementById('splash');
splashCanvas.width = splashWidth;
splashCanvas.height = splashHeight;

var splashContext = splashCanvas.getContext('2d');

function drawCircle(x, y, r) {
  splashContext.beginPath();
  splashContext.arc(x, y, r, 0, 2*Math.PI);
  splashContext.fillStyle = '#9B30FF';
  splashContext.fill();
  splashContext.lineWidth = 5;
  splashContext.strokeStyle = '#2E0854';
  splashContext.stroke();
}

function drawMessage(mes) {
  splashContext.font = '30pt Trebuchet MS';
  splashContext.textAlign = 'center';
  splashContext.fillStyle = 'black';
  splashContext.fillText(mes, splashWidth/2, splashHeight/2);
}

function clearSplashCanvas() {
  splashContext.clearRect(0, 0, splashCanvas.width, splashCanvas.height);
}

function drawConnectingAnimation(index) {
  var startX = (splashWidth/2) - 152.5;
  var startY = currentY = (splashHeight/2) + circleInfo.largeSize + 25;
  var currentX = startX;

  var i = 0;
  while(i<5) {
    if(i == index ){
      drawCircle(currentX, currentY, circleInfo.largeSize);
      currentX += 56.25 + circleInfo.smallBuffer;
    }else{
      drawCircle(currentX, currentY, circleInfo.smallSize);
      currentX += 56.25 + circleInfo.smallBuffer;
    }
    i++;
  }
}

function connectingSplash() {

  //Stop the game canvas
  canvas.style.display = "none";
  toggleVisibility('scoreboard', 'hidden');

  clearSplashCanvas();
  drawMessage('Connecting to other Players');
  drawConnectingAnimation(0);

  var timer = setInterval(function() {
    clearSplashCanvas();
    drawMessage('Connecting to other Players');
    spinner();

    if(startGame){
      //turn off the connecting splash canvas
      splashCanvas.style.display = "none";
      //turn on the game canvas
      canvas.style.display = "";
      //return;
      clearInterval(timer);
      (function animationLoop() {
        if(connected && startGame){
          requestAnimationFrame(animationLoop);
        }
        gameLoop();

      })();
    }

  }, 1000);

/**
  if(startGame){
    //turn off the connecting splash canvas
    splashCanvas.style.display = "none";
    //turn on the game canvas
    canvas.style.display = "";
    return;
  }
*/

}

var count = 1;
var direction = 1;

function spinner() {
  drawConnectingAnimation(count);
  count += direction;
  if(count == 4 || count == 0) {
    direction *= -1;
  }
}
//**************** SPLASH CONNECTION ANIMATION ****************

socket.on('connect', function() {
  connected = true;
});

// When some player disconnects
socket.on('disconnect', function(MessagePlayerId) {
  console.log("Player %d has disconnected\n", MessagePlayerId.id);
});

socket.on('gameStart', function() {
  console.log('Game has started');
  toggleVisibility('scoreboard', 'visible');
  startGame = true;
});

socket.on('gameEnd', function() {
  console.log('Game has ended');
  startGame = false;

  // Reset state variables
  players = {};
  food = {};
  // Dummy data
  myPlayer= {id: 42, teamId: 42, coord: {x: 42, y: 42}};
});

socket.on('playerId', function(MessagePlayerId) {
  myPlayer = MessagePlayerId;

  console.log("Got identity from server: Id = %d, Team ID = %d at (%d, %d) %o\n",
      myPlayer.id, myPlayer.teamId,
      myPlayer.coord.x, myPlayer.coord.y,
      MessagePlayerId);

});

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

socket.on('foodUpdate', function(MessageFoodCoord) {
  var f = MessageFoodCoord.food;
  var gone = MessageFoodCoord.gone;
  if (gone) {
    // Delete food from field
    delete food[f.id];
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
  document.getElementById(String(teamId)).innerHTML = newPoint;
});

/**
window.requestAnimationFrame = (function() {
    return window.requestAnimationFrame  ||
    window.webkitRequestAnimationFrame   ||
    window.mozRequestAnimationFrame      ||
    function(callback) {
      window.setTimeout(callback, 1000/60);
    };
})();
*/

(function animationLoop() {
  if(connected && startGame){
    requestAnimationFrame(animationLoop);
  }
  gameLoop();
})();

function gameLoop() {

  if (connected) {
    if (startGame) {
      clearCanvas();
      playerControls();
      drawGame();
    }else {
      //Connecting to other players
      connectingSplash();
    }
  }else {

  }

}
