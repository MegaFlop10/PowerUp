package au.net.projectb;

import au.net.projectb.subsystems.Drivetrain;
import au.net.projectb.subsystems.Drivetrain.ThrottlePreset;
import au.net.projectb.subsystems.Intake;
import au.net.projectb.subsystems.Lift;
import au.net.projectb.subsystems.Lift.LiftPosition;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * Coordinates each Subsystem according to operator inputs.
 */
public class TeleopController {
	Joystick stick;	// Driver's joystick
	XboxController xbox;	// Operator's XBox Controller
	
	Intake intake;
	Lift lift;
	Drivetrain drive;
	
	UsbCamera driveCamera;
	UsbCamera wristCamera;
	CvSink driveSink;
	CvSink wristSink;
	VideoSink cameraServer;
	
	SuperstructureState currentState;
	
	private enum SuperstructureState {
		INTAKING,
		STOWED,
		RAISED
	}
	
	TeleopController() {
		currentState = SuperstructureState.STOWED;
		
		intake = Intake.getInstance();
		lift = Lift.getInstance();
		drive = Drivetrain.getInstance();
		
		stick = new Joystick(0);
		xbox = new XboxController(1);
		
		driveCamera = CameraServer.getInstance().startAutomaticCapture(0);
		
		
		wristCamera = CameraServer.getInstance().startAutomaticCapture(1);
//		cameraServer = CameraServer.getInstance().getServer();
//		
//		driveSink = new CvSink("DriverCam");
//		driveSink.setSource(driveCamera);
//		driveSink.setEnabled(true);
//		
//		wristSink = new CvSink("WristCam");
//		wristSink.setSource(wristCamera);
//		wristSink.setEnabled(true);
	}
	
	/**
	 * Called by teleopPeriodic in Robot
	 */
	void run() {
//		SuperstructureState desiredState = handleInputs();
		
		switch (currentState) {
			case INTAKING:
				// State
//				cameraServer.setSource(wristCamera);
				
				if (stick.getRawButton(1)) {
					intake.actionIntakeStandby();
				} else {
					intake.actionIntakeClose();
				}
				
				//Transition
				if (stick.getRawButtonPressed(3) || -xbox.getY(Hand.kRight) > 0.75) {
					currentState = SuperstructureState.STOWED;
				}
				if (lift.getArmIsInIllegalPos()) {
					currentState = SuperstructureState.RAISED;
				}
				break;
				
			case STOWED:
				// State
//				cameraServer.setSource(driveCamera);
				
				if (stick.getRawButton(1)) {
					intake.actionOpenWhileStowed();
				} else if (stick.getRawButton(4)) {
					intake.actionTiltClosed();
				} else {
				
					intake.actionStow();
				}
				
				// Transition
				if (stick.getRawButtonPressed(3) || -xbox.getY(Hand.kRight) < -0.75) {
					currentState = SuperstructureState.INTAKING;
				}
				if (lift.getArmIsInIllegalPos()) {
					currentState = SuperstructureState.RAISED;
				}
				break;
				
			case RAISED:
				// State
//				cameraServer.setSource(driveCamera);
				
				if (stick.getRawButton(1)) {
					intake.actionOpenWhileStowed();
				} else {
					intake.actionStow();
				}
				
				// Transition
				if (lift.getArmIsInIllegalPos()) {
					currentState = SuperstructureState.RAISED;
				} else {
					currentState = SuperstructureState.STOWED;
				}
				break;
				
			default:
				currentState = SuperstructureState.STOWED;
		}
		
		// Lift Control
		double xboxElbowManualPower = -xbox.getY(Hand.kLeft);
		if (Math.abs(xboxElbowManualPower) > Constants.kElbowManualDeadzone || xbox.getBumper(Hand.kRight)) {
			lift.setElbowPower(-xbox.getY(Hand.kLeft));
			
		} else if (xbox.getAButtonPressed()) {
			lift.setPositionPreset(LiftPosition.GROUND);
			
		} else if (xbox.getXButtonPressed() || xbox.getBButtonPressed()) {
			lift.setPositionPreset(LiftPosition.SWITCH);
			
		} else if (xbox.getYButtonPressed()) {
			switch (xbox.getPOV(0)) {
				case 0:
					lift.setPositionPreset(LiftPosition.SCALE_HI);
					break;
				case 90:
					lift.setPositionPreset(LiftPosition.SCALE_MI);
					break;
				case 270:
					lift.setPositionPreset(LiftPosition.SCALE_MI);
					break;
				case 180:
					lift.setPositionPreset(LiftPosition.SCALE_LO);
					break;
				default:
					lift.setPositionPreset(LiftPosition.SCALE_HI);
					break;
			}
		} else {
			lift.actionMoveToPreset();  // Runs if no new presets are being set or if using manual control. (most of the time)
		}
		
		// Driver's buttons
		SmartDashboard.putBoolean("Forward?", drive.getDirectionIsReversed());
		
		SmartDashboard.putNumber("stick X", stick.getX());
		SmartDashboard.putNumber("stick Y", stick.getY());
		
		if (stick.getRawButtonPressed(2)) {
			drive.reverseDirection();
		}
		if (stick.getRawButtonPressed(9)) {
			drive.setThrottlePreset(ThrottlePreset.ANALOGUE);
		}
		if (stick.getRawButtonPressed(10)) {
			drive.setThrottlePreset(ThrottlePreset.LOW);
		}
		if (stick.getRawButtonPressed(7)) {
			drive.setThrottlePreset(ThrottlePreset.MID);
		}
		if (stick.getRawButtonPressed(8)) {
			drive.setThrottlePreset(ThrottlePreset.HIGH);
		}
		drive.arcadeDrive(-stick.getY(), stick.getX(), (-stick.getThrottle() + 1) / 2);
	}
	
	/**
	 * Interprets what state the operators want the robot to be in.
	 * @return Desired state as read from controller bindings.
	 * @deprecated
	 */
	private SuperstructureState handleInputs() {
		SuperstructureState retState = SuperstructureState.STOWED;
		if (stick.getRawButton(1) && currentState == SuperstructureState.STOWED) { // Only start to intake if the arm is stow(ed/ing)
			retState = SuperstructureState.INTAKING;
		}
		
		return retState;
	}
}
