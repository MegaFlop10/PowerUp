package au.net.projectb.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import au.net.projectb.Constants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * Robot's intake system. Intakes, stows, and scores power cubes.
 */
public class Intake extends Subsystem {
	private static Intake m_IntakeInstance;
	
	DoubleSolenoid pClaw;	
	TalonSRX mWrist;
	
	int timer;
	
	public static Intake getInstance() {
		if (m_IntakeInstance == null) {
			m_IntakeInstance = new Intake();
		}
		return m_IntakeInstance;
	}
	
	private Intake() {
		pClaw = new DoubleSolenoid(Constants.kPcm, Constants.kIntakeClawReverse, Constants.kIntakeClawForward);
		
		mWrist = new TalonSRX(Constants.kWristMotor);
		mWrist.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		mWrist.setSensorPhase(false); // Setting to true reverses sensor reading
		mWrist.enableVoltageCompensation(true);
		mWrist.setNeutralMode(NeutralMode.Coast);
		updateConstants();
		
		timer = -1;
	}
	
	/**
	 * Sometimes constants aren't constant, and you want to change them while the robot is running (tuning)
	 * This method should be called periodically by Tuning so the gains are actually changed, and once at construction to set up initially
	 */
	public void updateConstants() {
		mWrist.config_kP(0, Constants.kPWrist, 0);
		mWrist.config_kI(0, Constants.kIWrist, 0);
		mWrist.config_kD(0, Constants.kDWrist, 0);
		mWrist.configPeakOutputForward(Constants.kWristMaxDownwardVoltage / 12, 0);
		mWrist.configPeakOutputReverse(-Constants.kWristMaxUpwardVoltage / 12, 0);
	}
	
	/**
	 * Closes the claw, then raises the wrist
	 * @return True when action is complete
	 */
	public boolean actionStowFromIntake() {
		boolean actionComplete = mWrist.getClosedLoopError(0) < Constants.kWristErrorWindow;
		
		pClaw.set(Value.kReverse);
		
		if (timer == -1) {
			timer = 0;
		}
		if (timer >= Constants.kWristMoveDelay) {
			setWristPosition(Constants.kWristUpPosition);
		}
		
		timer++;
		
		if (actionComplete) {
			timer = -1;
		}
		return actionComplete;
	}
	
	/**
	 * Brings the intake down and open.
	 * @return True if action completed.
	 */
	public boolean actionIntakeStandby() {
		pClaw.set(Value.kForward);
		setWristPosition(Constants.kWristDnPosition);
		return mWrist.getClosedLoopError(0) < Constants.kWristErrorWindow;
	}
	
	/**
	 * Keeps the intake up and closed.
	 * @return True if action completed.
	 */
	public boolean actionStow() {
		pClaw.set(Value.kReverse);
		setWristPosition(Constants.kWristUpPosition);
		return mWrist.getClosedLoopError(0) < Constants.kWristErrorWindow;
	}
	
	/**
	 * Keeps the intake up and open.
	 * @return True if action completed.
	 */
	public boolean actionOpenWhileStowed() {
		pClaw.set(Value.kForward);
		setWristPosition(Constants.kWristUpPosition);
		return mWrist.getClosedLoopError(0) < Constants.kWristErrorWindow;
	}
	
	public void actionIntakeClose() {
		pClaw.set(Value.kReverse);
		setWristPosition(Constants.kWristDnPosition);
	}
	
	/**
	 * Brings the intake down, waits, then opens.
	 * @return True if action completed.
	 */
	public boolean actionScoreCube() {
		setWristPosition(Constants.kWristDnPosition);
		// wait timer
		pClaw.set(Value.kForward);
		return mWrist.getClosedLoopError(0) < Constants.kWristErrorWindow /* && timer is complete*/;
	}
	
	/**
	 * Tilt the wrist forward a little, stay closed
	 */
	public void actionTiltClosed() {
		setWristPosition(Constants.kWristTiltPosition);
		pClaw.set(Value.kReverse);
	}
	
	/**
	 * Tilt the wrist forward a little, stay open
	 */
	public void actionTiltOpen() {
		setWristPosition(Constants.kWristTiltPosition);
		pClaw.set(Value.kForward);
	}
	
	/**
	 * Get wrist position.
	 * @return True if wrist is more than halfway down.
	 */
	public boolean getWristIsDown() {
		return mWrist.getSelectedSensorPosition(0) > Constants.kWristDnPosition / 2;
	}
	
	public void setWristPosition(int setpoint) {
		if (Lift.getInstance().getArmIsInIllegalPos()) {
			mWrist.set(ControlMode.Position, Constants.kWristUpPosition);
		} else {
			mWrist.set(ControlMode.Position, setpoint);
		}
	}
	
	/**
	 * 
	 * @return Encoder position
	 */
	public int getWristPosition() {
		return mWrist.getSelectedSensorPosition(0);
	}
	
	public void setClawPosition(boolean closed) {
		if (closed) {
			pClaw.set(Value.kReverse);
		} else {
			pClaw.set(Value.kForward);
		}
	}
	
	public void zeroWristEncoder() {
		mWrist.setSelectedSensorPosition(0, 0, 0);
	}
}
