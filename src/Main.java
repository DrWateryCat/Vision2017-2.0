import java.util.Timer;
import java.util.TimerTask;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		initializeNetworkTables();
		
		MjpegServer inputStream = new MjpegServer("Input Stream", 1185);
		MjpegServer cvOutput = new MjpegServer("OpenCV output", 1186);
		MjpegServer climber = new MjpegServer("Climber", 1187);
		
		UsbCamera cam0 = initializeCamera(0);
		UsbCamera cam1 = initializeCamera(1);
		
		inputStream.setSource(cam0);
		climber.setSource(cam1);
		
		CvSink imageSink = new CvSink("Image Grabber");
		imageSink.setSource(cam0);
		
		CvSource source = new CvSource("From OpenCV", VideoMode.PixelFormat.kMJPEG, 320, 240, 15);
		cvOutput.setSource(source);
		
		TargetFinder targetFinder = new TargetFinder();
		TargetProcessor targetProcessor = new TargetProcessor();
		
		Mat image = new Mat();
		
		//Do a GC every 5 minutes
		Timer gcTimer = new Timer();
		gcTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.gc();
			}
		}, 0, (5*60*1000));
		while (true) {
			imageSink.grabFrame(image);
			if (NetworkTable.getTable("/SmartDashboard").getBoolean("Vision/Run Vision", false)) {
				targetFinder.setInput(image);
				targetFinder.run();
				targetProcessor.process(targetFinder.getTargets());

				source.putFrame(targetFinder.drawContours());
			} else {
				source.putFrame(image);
			}
		}
	}
	
	private static void initializeNetworkTables() {
		NetworkTable.setClientMode();
		NetworkTable.setTeam(2186);
		NetworkTable.initialize();
	}
	
	private static UsbCamera initializeCamera(int deviceID) {
		UsbCamera ret = new UsbCamera("CoprocessorCamera", deviceID);
		ret.setResolution(320, 240);
		ret.setFPS(15);
		
		return ret;
	}

}
