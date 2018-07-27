package au.net.projectb.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import au.net.projectb.Constants;

/**
 * Makes the robot move.
 */
public class Drivetrain extends Subsystem {
	private static Drivetrain m_DrivetrainInstance;
	
	TalonSRX mLeftMaster, mLeftSlaveA, mLeftSlaveB;
	TalonSRX mRightMaster, mRightSlaveA, mRightSlaveB;
	
	AHRS navx;
//	ADXRS450_Gyro navx;  // FIRST Choice gyro, only called navx bc i'm lazy
	
	boolean driveDirectionIsForwards;
	double throttlePreset;
	
	public enum ThrottlePreset {
		ANALOGUE,
		LOW,
		MID,
		HIGH
	}
	
	public static Drivetrain getInstance() {
		if (m_DrivetrainInstance == null) {
			m_DrivetrainInstance = new Drivetrain();
		}
		return m_DrivetrainInstance;
	}
	
	private Drivetrain() {
		navx = new AHRS(Port.kMXP);
//		navx = new ADXRS450_Gyro();
		navx.reset();
		
		// Left Side
		mLeftMaster = new TalonSRX(Constants.kLeftDriveMaster);	// CIM
		mLeftSlaveA = new TalonSRX(Constants.kLeftDriveSlaveA);	// CIM
		mLeftSlaveB = new TalonSRX(Constants.kLeftDriveSlaveB);	// MiniCIM
		
//		mLeftMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		mLeftMaster.configOpenloopRamp(Constants.kDriveVoltageRamp, 0);
		mLeftMaster.configContinuousCurrentLimit(40, 0);
		mLeftMaster.configPeakCurrentLimit(60, 0);
		mLeftMaster.configPeakCurrentDuration(100, 0);
		mLeftMaster.enableCurrentLimit(true);
		mLeftMaster.enableVoltageCompensation(true);
		
		mLeftMaster.setNeutralMode(Constants.kDriveNeutralMode);
		mLeftSlaveA.setNeutralMode(Constants.kDriveNeutralMode);
		mLeftSlaveB.setNeutralMode(Constants.kDriveNeutralMode);
		mLeftMaster.setInverted(false);
		mLeftSlaveA.setInverted(false);
		mLeftSlaveB.setInverted(false);
		
		mLeftSlaveA.set(ControlMode.Follower, Constants.kLeftDriveMaster);
		mLeftSlaveB.set(ControlMode.Follower, Constants.kLeftDriveMaster);
		
		// Right Side
		mRightMaster = new TalonSRX(Constants.kRightDriveMaster);	// CIM
		mRightSlaveA = new TalonSRX(Constants.kRightDriveSlaveA);	// MiniCIM
		mRightSlaveB = new TalonSRX(Constants.kRightDriveSlaveB);	// CIM
		
		mRightMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		mRightMaster.setSensorPhase(false);
		mRightMaster.configOpenloopRamp(Constants.kDriveVoltageRamp, 0);
		mRightMaster.configContinuousCurrentLimit(40, 0);
		mRightMaster.configPeakCurrentLimit(60, 0);
		mRightMaster.configPeakCurrentDuration(100, 0);
		mRightMaster.enableCurrentLimit(true);
		mRightMaster.enableVoltageCompensation(true);
		
		mRightMaster.setNeutralMode(Constants.kDriveNeutralMode);
		mRightSlaveA.setNeutralMode(Constants.kDriveNeutralMode);
		mRightSlaveB.setNeutralMode(Constants.kDriveNeutralMode);
		mRightMaster.setInverted(true);
		mRightSlaveA.setInverted(true);
		mRightSlaveB.setInverted(true);
		
		mRightSlaveA.set(ControlMode.Follower, Constants.kRightDriveMaster);
		mRightSlaveB.set(ControlMode.Follower, Constants.kRightDriveMaster);
		
		driveDirectionIsForwards = true;
		throttlePreset = -1.0;
	}
	
	/**
	 * Arcade drive, sets motor powers.
	 * @param power
	 * @param steering
	 * @param throttle
	 */
	public void arcadeDrive(double power, double steering, double throttle) {
		if (throttlePreset != -1) {
			throttle = throttlePreset;
		}
		
		double leftPower;
		double rightPower;
		leftPower = (power + steering) * throttle;
		rightPower = (power - steering) * throttle;
		
		if (driveDirectionIsForwards) {
			setMotorPower(leftPower, rightPower);
		} else {
			setMotorPower(-rightPower, -leftPower);  // Reverses direction
		}
		
		SmartDashboard.putNumber("Throttle", throttle);
		SmartDashboard.putNumber("LeftPower", leftPower);
		SmartDashboard.putNumber("RightPower", rightPower);
	}
	
	/**
	 * Reverse the driving direction for teleopDrive.
	 */
	public void reverseDirection() {
		driveDirectionIsForwards = !driveDirectionIsForwards;
	}
	
	public boolean getDirectionIsReversed() {
		return driveDirectionIsForwards;
	}
	
	/**
	 * Wrapper for setting motor powers.
	 * @param left
	 * @param right
	 */
	public void setMotorPower(double left, double right) {
		mLeftMaster.set(ControlMode.PercentOutput, left);
		mLeftSlaveA.follow(mLeftMaster);
		mLeftSlaveB.follow(mLeftMaster);
		mRightMaster.set(ControlMode.PercentOutput, right);  // TODO: Reverse this
		mRightSlaveA.follow(mRightMaster);
		mRightSlaveB.follow(mRightMaster);
	}
	
	public void setThrottlePreset(ThrottlePreset preset) {
		switch (preset) {
			case LOW:
				throttlePreset = 0.40;
				break;
			case MID:
				throttlePreset = 0.75;
				break;
			case HIGH:
				throttlePreset = 1.00;
				break;
			default: // Also handles ANALOGUE
				throttlePreset = -1; // see line 90
		}
	}
	
	public double getAngle() {
		return navx.getAngle();
	}
	
	/**
	 * 
	 * @param angle in degrees
	 * @param threshRange in degrees +/-
	 * @return true if angle of gyro is within the range around the set angle
	 */
	public boolean getAngleWithinRange(double angle, double threshRange) {
		return Math.abs(angle - getAngle()) < threshRange; 
	}
	
	/**
	 * Gets distance covered by the encoder. On the right side of the robot.
	 * @return distance in counts (2048) 
	 */
	public int getEncoderCounts() {
		return mRightMaster.getSelectedSensorPosition(0);
	}
	
	/**
	 * Converts getEncoderCounts to metres.
	 * @return distance covered by the robot in metres.
	 */
	public double getEncoderDistance() {
		return .00006329 * getEncoderCounts(); 
	}
	
	/**
	 * Set the encoder to a position
	 * @param counts
	 */
	public void setEncoderCounts(int counts) {
		mRightMaster.setSelectedSensorPosition(counts,  0,  0);
	}
	
	/**
	 * 
	 * @param distance in m
	 * @param threshRange in m +/-
	 * @return
	 */
	public boolean getEncoderWithinDistance(double distance, double threshRange) {
		return Math.abs(distance - getEncoderDistance()) < threshRange;
	}
	
	public void zeroGyro() {
		navx.reset();
	}
	
	public void driveMotor(int canID, double power) {
		switch (canID) {
		case 1:
			mLeftMaster.set(ControlMode.PercentOutput, power);
			break;
		case 2:
			mLeftSlaveA.set(ControlMode.PercentOutput, power);
			break;
		case 3:
			mLeftSlaveB.set(ControlMode.PercentOutput, power);
			break;
		case 4:
			mRightMaster.set(ControlMode.PercentOutput, power);
			break;
		case 5:
			mRightSlaveA.set(ControlMode.PercentOutput, power);
			break;
		case 6:
			mRightSlaveB.set(ControlMode.PercentOutput, power);
			break;
		default:
			DriverStation.reportWarning("=== Invalid CAN ID input! ===", false);
			break;	
		}
	}
	
	/**
	 * Drives using encoder and gyro.
	 * Set power to 0 to point to a heading.
	 * 
	 * @param maxPower (0-1, no negatives)
	 * @param heading in degrees
	 * @param distance in metres
	 * @return true if within 0.01m (1cm) of target
	 */
	public boolean actionSensorDrive(double maxPower, double heading, double distance) {
		double steering = (heading - getAngle()) * Constants.kPGyroSteering;
		double power = (distance - getEncoderDistance()) * Constants.kPDistancePower;
		if (power >= 0 && power > maxPower) {
			power = maxPower;
		} else if (power < 0 && power < -maxPower) {
			power = -maxPower;
		}
		
		arcadeDrive(power, steering, 1);
		return getEncoderWithinDistance(distance, 0.01);
	}
	
	public boolean actionGyroTurn(double heading) {
		double steering = (heading - getAngle()) * Constants.kPGyroSteering;
		
		arcadeDrive(0, steering, 1);
		return getAngleWithinRange(90, 3);
	}
}
