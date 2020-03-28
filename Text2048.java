import java.util.ArrayList;
import java.util.Scanner;

public class Text2048 {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		int[][] board = {{0,0,0,0},
						 {0,0,0,0},
		                 {0,0,0,0},
		                 {0,0,0,0}};
		String move;
		int turn = 0;
		playRandomMove(board, turn);
		
		while (true) {
			turn++;
			printBoard(board);
			System.out.println("Score: " + getScore(board));
			do {
				System.out.println("wasd?");
				move = input.nextLine();
			} while (!(move.equals("w") || move.equals("a") || move.equals("s") || move.equals("d")));
			
			switch (move) {
				case "w":
					moveUp(board);
					break;
				case "a":
					moveLeft(board);
					break;
				case "s":
					moveDown(board);
					break;
				case "d":
					moveRight(board);
					break;
			}
			
			if (getRemainingSpots(board) > 0) {
				playRandomMove(board, turn); 
			} else {
				System.out.println("");
				System.out.println("You ran out of space! You lose!");
				break;
			}		
		}
		input.close();
	}
	
	public static void printBoard(int[][] board) {
		
		System.out.print("+");
		for (int topLine = 0; topLine < 12; topLine ++) {
			System.out.print("-");
		}
		System.out.print("+");
		System.out.println("");
		
		for (int row = 0; row < 4; row++) {
			for (int column = 0; column < 4; column++) {
				if (board[row][column] != 0) {
					if (column == 0) {
						System.out.print("| " + board[row][column] + " ");
					} else if (column == 3) {
						System.out.print(" " + board[row][column] + " |");
					} else {
						System.out.print(" " + board[row][column] + " ");
					}	
				} else {
					if (column == 0) {
						System.out.print("| " + " " + " ");
					} else if (column == 3) {
						System.out.print(" " + " " + " |");
					} else {
						System.out.print(" " + " " + " ");
					}
				}
			}
			System.out.println("");
		}
		
		System.out.print("+");
		for (int bottomLine = 0; bottomLine < 12; bottomLine++) {
			System.out.print("-");
		}
		System.out.println("+");
	}
	
	public static int[][] playRandomMove(int[][] board, int turn) {
		int spawnCount = 2;
		
		for (int i = 0; i < spawnCount; i++) {
			int[][] availableSpots = getAvailableSpots(board);	
			int randCords = (int) Math.floor((Math.random() * availableSpots.length));		
			int randMove = (int) Math.floor((Math.random() * 10));
				
			// In 2048 there is a 10% chance to randomly spawn a 4 instead of 2
			if (randMove == 0) {
				randMove = 4;
			} else {
				randMove = 2;
			}
			
			board[availableSpots[randCords][0]][availableSpots[randCords][0]] = randMove;
		}
		return board;
	}
		
	public static int getRemainingSpots(int[][] board) {
		int emptyCells = 0;
		for (int row = 0; row < board.length; row++) {
			for (int column = 0; column < board[row].length; column++) {
				if (board[row][column] == 0) {
					emptyCells++;
				}
			}
		}
		return emptyCells;
	}
	
	public static int[][] getAvailableSpots(int[][] board) {
		int[][] takenSpots = new int[getRemainingSpots(board)][2];
		int count = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[i][j] == 0) {
					takenSpots[count][0] = i;
					takenSpots[count][1] = j;
					count++;
				}
			}
		}
		return takenSpots;
	}
	
	public static int[][] moveUp(int[][] board) {
		// ArrayList of the coordinates of tiles that have already merged in the same move
		// to follow the "non-greedy" movement rule
		ArrayList<Integer> mergedTiles = new ArrayList<Integer>();
		
		for (int row = 1; row < board.length; row++) {
			for (int column = 0; column < board[row].length; column++) {
				if (board[row][column] != 0) {
					int x = column;
					int y = row;
					int numPlaceholder = board[row][column];
					int numMerges = 0;
					
					boolean movingDown = true;
					while (movingDown) {
						if (y - 1 >= 0) {
							board[y][x] = 0;
							if (board[y - 1][x] == 0) {
								y--;
							// Check if the tile above is possible to merge into and 
							// the tile has not already merged in the same turn
							} else if (board[y - 1][x] == numPlaceholder && numMerges < 1) {
								boolean alreadyMerged = false;
								// If any tile on the board has already merged check if 
								// the tile currently merging is trying to merge with
								// one of those tiles
								if (mergedTiles.size() > 1) {
									for (int mergedY = 0; mergedY < mergedTiles.size() / 2; mergedY += 2) {
										if (mergedTiles.get(mergedY) == y - 1 && mergedTiles.get(mergedY + 1) == x) {
											alreadyMerged = true;
											break;
										}
									}
								}							
								if (alreadyMerged) {
									board[y][x] = numPlaceholder;
									break;
								// if clear to merge -> double value of tile then add that
								// tile's Y and X coordinates to the list of tiles
								// that have already merged that turn
								} else {
									numPlaceholder *= 2;
									numMerges++;
									y--;
									mergedTiles.add(y);
									mergedTiles.add(x);
								}
							} else {
								board[y][x] = numPlaceholder;
								movingDown = false;
							}
						} else {
							board[y][x] = numPlaceholder;
							movingDown = false;
						}
					}
				}
			}
		}
		return board;
	}
	
	public static int[][] moveDown(int[][] board) {
		ArrayList<Integer> mergedTiles = new ArrayList<Integer>();
		for (int row = board.length - 1; row >= 0; row--) {
			for (int column = board[row].length - 1; column >= 0; column--) {
				if (board[row][column] != 0) {		
					int x = column;
					int y = row;
					int numPlaceholder = board[row][column];
					int numMerges = 0;
					
					boolean movingDown = true;
					while (movingDown) {
						if (y + 1 <= 3) {
							board[y][x] = 0; 
							if (board[y + 1][x] == 0) {
								y++;;
							} else if (board[y + 1][x] == numPlaceholder && numMerges < 1) {
								boolean alreadyMerged = false;
								for (int mergedY = 0; mergedY < mergedTiles.size() / 2; mergedY += 2) {
									if (mergedTiles.get(mergedY) == y + 1 && mergedTiles.get(mergedY + 1) == x) {
										alreadyMerged = true;
										break;
									}
								}							
								if (alreadyMerged) {
									board[y][x] = numPlaceholder;
									movingDown = false;
								} else {
									numPlaceholder *= 2;
									numMerges++;
									y++;
									mergedTiles.add(y);
									mergedTiles.add(x);
								}
							} else {
								board[y][x] = numPlaceholder;	
								movingDown = false;
							}
						} else {
							board[y][x] = numPlaceholder;
							movingDown = false;
						}
					}
				}
			}
		}
		return board;
	}
	
	public static int[][] moveLeft(int[][] board) {
		ArrayList<Integer> mergedTiles = new ArrayList<Integer>();
		for (int row = board.length - 1; row >= 0; row--) {
			for (int column = board[row].length - 2; column >= 0; column--) {
				if (board[row][column] != 0) {
					int x = column;
					int y = row;
					int numPlaceholder = board[row][column];
					int numMerges = 0;
					
					boolean movingDown = true;
					while (movingDown) {
						if (x + 1 <= 3) {
							board[y][x] = 0;
							if (board[y][x + 1] == 0) {
								x++;
							} else if (board[y][x + 1] == numPlaceholder && numMerges < 1) {
								boolean alreadyMerged = false;
								for (int mergedY = 0; mergedY < mergedTiles.size() / 2; mergedY += 2) {
									if (mergedTiles.get(mergedY) == y  && mergedTiles.get(mergedY + 1) == x + 1) {
										alreadyMerged = true;
										break;
									}
								}							
								if (alreadyMerged) {
									board[y][x] = numPlaceholder;
									movingDown = false;
								} else {
									numPlaceholder *= 2;
									numMerges++;
									x++;
									mergedTiles.add(y);
									mergedTiles.add(x);
								}
							} else {
								board[y][x] = numPlaceholder;
								movingDown = false;;
							}
						} else {
							board[y][x] = numPlaceholder;
							movingDown = false;;
						}
					}
				}
			}
		}
		return board;
	}
	
	public static int[][] moveRight(int[][] board) {
		ArrayList<Integer> mergedTiles = new ArrayList<Integer>();
		for (int row = 0; row < board.length; row++) {
			for (int column = 1; column < board[row].length; column++) {
				if (board[row][column] != 0) {
					int x = column;
					int y = row;
					int numPlaceholder = board[row][column];
					int numMerges = 0;
					
					boolean movingDown = true;
					while (movingDown) {
						if (x - 1 >= 0) {
							board[y][x] = 0;
							if (board[y][x - 1] == 0) {
								x--;
							} else if (board[y][x - 1] == numPlaceholder && numMerges < 1) {
								boolean alreadyMerged = false;
								for (int mergedY = 0; mergedY < mergedTiles.size() / 2; mergedY += 2) {
									if (mergedTiles.get(mergedY) == y  && mergedTiles.get(mergedY + 1) == x - 1) {
										alreadyMerged = true;
										break;
									}
								}							
								if (alreadyMerged) {
									board[y][x] = numPlaceholder;
									movingDown = false;;
								} else {
									numPlaceholder *= 2;
									numMerges++;
									x--;
									mergedTiles.add(y);
									mergedTiles.add(x);
								}
							} else {
								board[y][x] = numPlaceholder;
								movingDown = false;
							}
						} else {
							board[y][x] = numPlaceholder;
							movingDown = false;
						}
					}
				}
			}
		}
		return board;
	}
	
	public static int getScore(int[][] board) {
		int score = 0;
		for (int[] row : board) {
			for (int num : row) {
				score += num;
			}
		}
		return score;
	}
}