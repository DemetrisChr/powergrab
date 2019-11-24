package uk.ac.ed.inf.powergrab;

public enum Direction {
    N   (0),
    NNE (Math.PI / 8),
    NE  (2 * Math.PI / 8),
    ENE (3 * Math.PI / 8),
    E   (4 * Math.PI / 8),
    ESE (5 * Math.PI / 8),
    SE  (6 * Math.PI / 8),
    SSE (7 * Math.PI / 8),
    S   (8 * Math.PI / 8),
    SSW (9 * Math.PI / 8),
    SW  (10 * Math.PI / 8),
    WSW (11 * Math.PI / 8),
    W	(12 * Math.PI / 8),
    WNW (13 * Math.PI / 8),
    NW  (14 * Math.PI / 8),
    NNW (15 * Math.PI / 8);

    private final double angle;

    // The sine and cosine of the angle are stored as fields to avoid unnecessary
    // repetition of the calculation which would impact performance
    public final double sine;
    public final double cosine;

    Direction(double angle) {
        this.angle = angle;
        this.sine = Math.sin(this.angle);
        this.cosine = Math.cos(this.angle);
    }

    public Direction getOppositeDirection() {
        switch (this) {
            case N:   return S;
            case NNE: return SSW;
            case NE:  return SW;
            case ENE: return WSW;
            case E:   return W;
            case ESE: return WNW;
            case SE:  return NW;
            case SSE: return NNW;
            case S:   return N;
            case SSW: return NNE;
            case SW:  return NE;
            case WSW: return ENE;
            case W:   return E;
            case WNW: return ESE;
            case NW:  return SE;
            case NNW: return SSE;
            default:  throw new RuntimeException(); // Should not happen since the switch is exhaustive
        }
    }
}
