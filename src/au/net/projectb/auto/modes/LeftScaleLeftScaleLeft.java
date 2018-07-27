package au.net.projectb.auto.modes;

import au.net.projectb.auto.AutoController.FieldPosition;
import au.net.projectb.subsystems.Drivetrain;
import au.net.projectb.subsystems.Intake;
import au.net.projectb.subsystems.Lift;
import au.net.projectb.subsystems.Lift.LiftPosition;
import edu.wpi.first.wpilibj.DriverStation;

public class LeftScaleLeftScaleLeft extends AutoMode {
	FieldPosition startPosition;  // Robot start position
	FieldPosition switchPosition;
	FieldPosition scalePosition;
	
	/**
	 * Set the starting position of the auto mode. (Left, Centre, Right)
	 */
	@Override
	public void setFieldPositions(FieldPosition startPos, FieldPosition switchPos, FieldPosition scalePos) {
		startPosition = startPos;
		switchPosition = switchPos;
		scalePosition = scalePos;
	}
	
	/**
	 * Run a given step in auto iteratively.
	 */
	@Override
	public void runStep(int stepIndex) {
		// Code here is step by step, and concurrent actions are possible if in the same step.
		switch (stepIndex) {
			case 0:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				Intake.getInstance().actionStow();
				Drivetrain.getInstance().actionSensorDrive(0.6, 0.0, 7.0);
				break;
			
			case 1:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				Intake.getInstance().actionStow();
				Drivetrain.getInstance().actionSensorDrive(0.4, 25.0, 7.1);
				break;
				
			case 2:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				Intake.getInstance().actionOpenWhileStowed();
				Drivetrain.getInstance().actionSensorDrive(0.4, 25.0, -0.6);
				break;
				
			case 3:
				Lift.getInstance().actionMoveTo(LiftPosition.GROUND);
				Intake.getInstance().actionStow();
				Drivetrain.getInstance().actionGyroTurn(155.0);
				break;
				
			case 4:
				Lift.getInstance().actionMoveTo(LiftPosition.GROUND);
				Intake.getInstance().actionIntakeStandby();
				Drivetrain.getInstance().actionSensorDrive(0.2, 155.0, 1.1);
				break;
				
			case 5:
				Lift.getInstance().actionMoveTo(LiftPosition.GROUND);
				if (Drivetrain.getInstance().getEncoderDistance() > 0.5) {
					Intake.getInstance().actionIntakeClose();
				} else {
					Intake.getInstance().actionStow();
				}
				Drivetrain.getInstance().actionSensorDrive(0.2, 165.0, 0.3);
				break;
			
			case 6:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				Intake.getInstance().actionStow();
				Drivetrain.getInstance().actionGyroTurn(15.0);
				break;
				
			case 7:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				if (Lift.getInstance().getArmIsInIllegalPos()) {
					Intake.getInstance().actionStow();
				} else {
					Intake.getInstance().actionIntakeClose();
				}
				Drivetrain.getInstance().actionSensorDrive(0.6, 15.0, 0.7);
				break;
				
			case 8:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				Intake.getInstance().actionIntakeStandby();
				Drivetrain.getInstance().actionSensorDrive(0.4, 20.0, 0.7);
				break;
				
		}
		
	}
	
	/**
	 * Each step has an exit/finish condition, which is defined in this method.
	 * @return True if exit condition of a step is reached.
	 */
	@Override
	public boolean getStepIsCompleted(int stepIndex) {
		
		switch (stepIndex) {
			case 0:
				return Drivetrain.getInstance().getEncoderWithinDistance(4.5, 0.1);
				
			case 1:
				if (Drivetrain.getInstance().getEncoderWithinDistance(7.1, 0.1)) {
					Drivetrain.getInstance().setEncoderCounts(0);
					return true;
				}
				return false;
			
			case 2:
				return Drivetrain.getInstance().getEncoderWithinDistance(-0.6, 0.1);
				
			case 3:
				if (Drivetrain.getInstance().getAngle() > 150.0 && Lift.getInstance().getElbowPosition() <= 1000) {
					Drivetrain.getInstance().setEncoderCounts(0);
					return true;
				}
				return false;
				
			case 4:
				return Drivetrain.getInstance().getEncoderWithinDistance(1.1, 0.1) || DriverStation.getInstance().getMatchTime() <= 4.0;
				
			case 5:
				return Drivetrain.getInstance().getEncoderWithinDistance(0.3, 0.1);
				
			case 6:
				if (Drivetrain.getInstance().getAngleWithinRange(15.0, 10.0)) {
					Drivetrain.getInstance().setEncoderCounts(0);
					return true;
				}
				return false;
				
			case 7:
				return Drivetrain.getInstance().getEncoderWithinDistance(0.7, 0.1)/** || DriverStation.getInstance().getMatchTime() <= 0.5**/;
				
			default:
				return false;
		}
		
	}
}
