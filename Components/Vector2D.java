package Components;

public class Vector2D {
    public int x;
    public int y;

    public static final Vector2D ZERO = new Vector2D(0, 0);
    
    public Vector2D(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other){
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    public Vector2D subtract(Vector2D other){
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public String toString(){
        return "Vector(" + x + ", " + y + ")";
    }

    public boolean equals(Object other){
        if(other instanceof Vector2D){
            Vector2D otherVector = (Vector2D) other;
            return otherVector.x == this.x && otherVector.y == this.y;
        }

        return false;
    }
}
