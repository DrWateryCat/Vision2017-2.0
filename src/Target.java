import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class Target {
	public int x;
	public int y;
	public int width;
	public int height;
	public int area;
	
	public static Target fromMatOfPoint(MatOfPoint m) {
		Target t = new Target();
		Rect boundingRect = Imgproc.boundingRect(m);
		
		t.x = boundingRect.x;
		t.y = boundingRect.y;
		t.width = boundingRect.width;
		t.height = boundingRect.height;
		t.area = (t.width * t.height);
		
		return t;
	}
}
