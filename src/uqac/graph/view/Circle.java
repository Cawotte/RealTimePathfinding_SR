package uqac.graph.view;


import uqac.graph.Vector2;

import java.awt.*;

public class Circle {

    public Vector2 center;
    public int radius;

    public Color color;

    public Circle(Vector2 center, int radius, Color color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    public Circle(int x, int y, int radius, Color color) {
        this(new Vector2(x, y), radius, color);
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval((int)center.x - radius / 2, (int)center.y - radius / 2,
                radius, radius);
    }
}
