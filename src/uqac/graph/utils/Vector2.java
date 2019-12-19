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

    public static Vector2 lerp(Vector2 a, Vector2 b, float f) {
        return new Vector2(
                lerp(a.x, b.x, f),
                lerp(a.y, b.y, f)
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
        return (Vector2.Distance(this, otherPos) < 0.001f);
        //return (x == otherPos.x && y == otherPos.y);
    }

    private static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + String.format("%.2f", x) + ", " + String.format("%.2f", y) + ")";
    }
}
