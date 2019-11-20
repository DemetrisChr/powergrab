package uk.ac.ed.inf.powergrab;

public class Move {
    private Position positionBefore = null;
    private Direction moveDirection;
    private Position positionAfter = null;
    private Double coinsAfter = null;
    private Double powerAfter = null;
    private Drone droneToMove;

    public Move(Drone droneToMove, Direction moveDirection) {
        this.moveDirection = moveDirection;
        this.droneToMove = droneToMove;
    }

    public void executeMove() {
        this.positionBefore = droneToMove.getPosition();
        this.positionAfter = positionBefore.nextPosition(moveDirection);
        this.droneToMove.move(moveDirection);
        this.positionAfter = droneToMove.getPosition();
        this.powerAfter = droneToMove.getPower();
        this.coinsAfter = droneToMove.getCoins();
    }

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
