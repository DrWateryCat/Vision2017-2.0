import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class TargetFinder {
	private Scalar m_minHSV = new Scalar(63.216, 0.0, 188.0);
	private Scalar m_maxHSV = new Scalar(94.886, 107.0, 255.0);
	
	@SuppressWarnings("unused")
	private Size m_resolution = new Size(320, 240);
	
	private double m_minContourArea = 50.0;
	private double m_maxContourArea = 500.0;
	
	private Mat hsv = new Mat();
	
	private ArrayList<MatOfPoint> m_mop = new ArrayList<>();
	
	public TargetFinder() {
		
	}
	
	public void setInput(Mat input) {
		Imgproc.cvtColor(input, hsv, Imgproc.COLOR_BGR2HSV);
	}
	
	public Mat drawContours() {
		Mat ret = new Mat();
		Imgproc.drawContours(ret, m_mop, -1, new Scalar(255, 255, 255));
		return ret;
	}
	
	public void run() {
		this.reset();

		//Thresholding
		Core.inRange(hsv, m_minHSV, m_maxHSV, hsv);

		//Find Contours
		this.m_mop = this.findContours(hsv);

		//Filter Contours
		this.m_mop = this.filterContours(this.m_mop);

		//Convex Hull
		this.m_mop = this.convexHulls(m_mop);
	}
	
	public ArrayList<Target> getTargets() {
		//Convert to Targets and return
		return convertToTarget(m_mop);
	}
	
	private void reset() {
		this.m_mop.clear();
	}
	
	private ArrayList<MatOfPoint> findContours(Mat input) {
		ArrayList<MatOfPoint> ret = new ArrayList<>();
		Mat unused = new Mat();
		Imgproc.findContours(input, ret, unused, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		return ret;
	}
	
	private ArrayList<MatOfPoint> filterContours(ArrayList<MatOfPoint> in) {
		ArrayList<MatOfPoint> ret = new ArrayList<>();
		
		for(MatOfPoint m : in) {
			double area = Imgproc.contourArea(m);
			
			if(area >= this.m_minContourArea && area <= this.m_maxContourArea) {
				ret.add(m);
			}
		}
		
		return ret;
	}
	
	private ArrayList<MatOfPoint> convexHulls(ArrayList<MatOfPoint> in) {
		ArrayList<MatOfPoint> ret = new ArrayList<>();
		MatOfInt hull = new MatOfInt();
		
		for(MatOfPoint m : in) {
			MatOfPoint temp = new MatOfPoint();
			
			Imgproc.convexHull(m, hull);
			
			temp.create((int)temp.size().height, 1, CvType.CV_32SC2);
			
			for(int i = 0; i < hull.size().height; i++) {
				int index = (int) hull.get(i, 0)[0];
				double[] point  = new double[] {m.get(index, 0)[0], m.get(index, 0)[1]};
				temp.put(i, 0, point);
			}
			
			ret.add(temp);
		}
		
		return ret;
	}
	
	public static ArrayList<Target> convertToTarget(ArrayList<MatOfPoint> in) {
		ArrayList<Target> ret = new ArrayList<>();
		
		for (MatOfPoint m : in) {
			ret.add(Target.fromMatOfPoint(m));
		}
		
		return ret;
	}
}
