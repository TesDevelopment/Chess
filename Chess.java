import Components.Draw;
import Components.Listener;

public class Chess {
    
    public static void main(String[] args) {
        Draw canvas = new Draw();
        Listener listener = new Listener(canvas);

        canvas.setTitle("Chess");
        canvas.enableDoubleBuffering();
        canvas.addListener(listener);
        canvas.enableTimer(60);
    }   
}