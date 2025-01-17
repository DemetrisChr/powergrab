package uk.ac.ed.inf.powergrab;

public class Move {
    private Position positionBefore;
    private final Direction moveDirection;
    private Position positionAfter;
    private double coinsAfter;
    private double powerAfter;
    private final Drone droneToMove;

    public Move(Drone droneToMove, Direction moveDirection) {
        this.moveDirection = moveDirection;
        this.droneToMove = droneToMove;
    }

    // Executes the move by calling the drone's move() method. Stores in the fields of this Move object the position
    // of the drone before/after and its coins & power after the move.
    public void executeMove() {
        this.positionBefore = droneToMove.getPosition();
        this.droneToMove.move(moveDirection);
        this.positionAfter = droneToMove.getPosition();
        this.powerAfter = droneToMove.getPower();
        this.coinsAfter = droneToMove.getCoins();
    }

    // Returns a String representation of the attributes of the Move separated by commas
    @Override
    public String toString() {
        return this.positionBefore.latitude + "," +
                this.positionBefore.longitude + "," +
                this.moveDirection + "," +
                this.positionAfter.latitude + "," +
                this.positionAfter.longitude + "," +
                this.coinsAfter + "," +
                this.powerAfter;
    }

    public Direction getMoveDirection() { return moveDirection; }

    public Position getPositionAfter() { return positionAfter; }

    public Position getPositionBefore() { return positionBefore; }
}
