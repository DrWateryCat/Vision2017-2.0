import java.util.ArrayList;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class TargetProcessor {
	private final double FOCAL_LENGTH = 297.73;
	private final int CAMERA_WIDTH = 320;
	private final int CAMERA_HEIGHT = 240;
	private final double TARGET_WIDTH = 10.25;
	
	private double m_centerX;
	private double m_distanceToTarget;
	
	private boolean m_foundHook;
	
	private NetworkTable sd = NetworkTable.getTable("/SmartDashboard");
	
	public double getCenterX() {
		return m_centerX;
	}
	
	public double getTurn() {
		return (this.getCenterX() - 160) / 160;
	}
	
	public double getDistanceToTarget() {
		return this.m_distanceToTarget;
	}
	
	public boolean didFindHook() {
		return m_foundHook;
	}
	
	private double findDistance(Target left, Target right) {
		double targetWidth = (left.width + right.width);
		
		return (TARGET_WIDTH * FOCAL_LENGTH) / targetWidth;
	}
	
	public void process(ArrayList<Target> targets) {
		if (targets.size() == 2) {
			Target left = targets.get(0);
			Target right = targets.get(1);
			
			double leftCenterX = (left.x + (left.width / 2));
			double rightCenterX = (right.x + (right.width / 2));
			
			m_centerX = (leftCenterX + rightCenterX) / 2;
			
			m_distanceToTarget = this.findDistance(left, right);
			
			m_foundHook = true;
		} else {
			m_foundHook = false;
			m_centerX = 0;
			m_distanceToTarget = 0;
		}
		
		sd.putNumber("Vision/Turn", this.getTurn());
		sd.putBoolean("Vision/Found Hook", this.didFindHook());
		sd.putNumber("Vision/Distance To Target", this.getDistanceToTarget());
	}
}
