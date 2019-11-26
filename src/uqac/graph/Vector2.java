package uqac.graph;

import java.util.Objects;

public class Vector2 {

    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2() {
        this(0, 0);
    }


    public static float Distance(Vector2 a, Vector2 b) {

        return (float)Math.sqrt(
                Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Vector2 otherPos = (Vector2)o;
        return (x == otherPos.x && y == otherPos.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
