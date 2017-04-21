
# Artificial Intelligence
Controlling Game Characters with Neural Networks and Fuzzy Logic

Ross Byrne
Martin Coleman 

Github:
https://github.com/Ross-Byrne/neuralNetworkMazeGame


# ABOUT THE GAME
The Aim of the game is to traverse around the maze fighting spiders and collecting pickups to 
become strong enough to beat the boss spider. Spiders will either flee or fight the player.

The game can be player or AI controlled, Popup at start to decide if AI or Player controlled.

## The spiders are as follows:
Red - Boss, Strongest, Only one in maze, Attacks player when he has bombs and swords.

Blue - Next strongest, Only a few in maze, Attack player when he has a sword.

Black - Common, Weaker, Attack player.

Grey - Common, Weaker, Attack player.

Orange - Common stronger than Black or Grey, Attack player.

Yellow - Flees from player, won’t attack player until he attacks a yellow spider then turn hostile.

Brown - Newer attack player, flees from player.

Green - Heal the player


## The Pickups are as follows:
Sword - gives the player a sword to increase attack

Random Pickup(help box) - Give the player either health, a sword or a bomb

Bomb - gives the player a bomb

Hydrogen Bomb - Gives the player two bombs


# FUZZY LOGIC
Fuzzy Logic is used to make decisions for the player.

The players health status is decided by a fuzzy logic classifier that will return injury status e.g. Minor

The players risk associated with attacking is decided by Fuzzy logic, player can then decide to attack or not.



# NEURAL NETWORK

 A Neural Network to decide what happens when a player
 engages in combat with a spider.
 The Neural net is trained in the constructor when the object is created.
 The network takes 4 inputs.
 Health, which is classified by a Fuzzy logic system,
 Sword, which is whether or not the player has one,
 Bomb, which is whether or not the player has any
 Enemies, which is classified using a Fuzzy logic system.
 The result is either Attack, panic, heal or run.
 The logic for this functionality is in PlayerNode.java.

# AI SEARCH ALGORITHMS
Spider nodes implement their own traverses to locate the player and to move either toward of away from the player.

Player node uses a variety of traverses to navigate around the maze, find pickups and locate spiders in the maze.

Depth Limited DFS is used as a scanner to see if a node(spider or player) is in range e.g. 10 nodes away.

Best first is used by spider nodes to decide the best path to move towards the player. 
This is used after the player is found with the Depth Limited DFS as the depth limited path is more often that not 
a bad path to the player and spider may appear to be moving away from the player. Best first tries to pick the 
best available path to the goal. Spiders move toward the player in a more realistic way.

When player is strong the tougher spiders use Best first to find them anywhere on the map.

AStar is used by the player to find the best path to a given Node. 
The Node might be a pickup or a spider to attack depending to the players health and attack.



# THREADED CHARACTERS

All spiders are threaded and player node is also threaded. 
Searches are preformed in threads by each node individually.

# EXTRAS
Redesign of Maze:
We redesigned the maze.
It uses ints to decide sprites etc.

Combat system:
Player node uses fuzzy logic to help decide to engage spiders or look for pickups to get stronger or to heal.
Player decides to Attack,Heal,Panic or attempt to run away.

Redesign AStar:
We had to redesign the AStar traverser for it to be compatible with our maze.

The player is fully automated, using A* to walking around the maze.

Adapted the Depth limited depth first search to find all enemies and pickups up to a set depth.






