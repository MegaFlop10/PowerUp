package au.net.projectb.auto.modes;

import au.net.projectb.auto.AutoController.FieldPosition;
import au.net.projectb.subsystems.Drivetrain;
import au.net.projectb.subsystems.Intake;
import au.net.projectb.subsystems.Lift;
import au.net.projectb.subsystems.Lift.LiftPosition;
import edu.wpi.first.wpilibj.DriverStation;

public class LeftScaleLeft extends AutoMode {
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
				Lift.getInstance().actionMoveTo(LiftPosition.GROUND);
				Intake.getInstance().actionStow();
				Drivetrain.getInstance().actionSensorDrive(0.75, 0.0, 7.5);
				break;
			
			case 1:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				Intake.getInstance().actionStow();
				Drivetrain.getInstance().actionSensorDrive(0.75, 0.0, 7.5);
				break;
				
			case 2:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				Intake.getInstance().actionStow();
				Drivetrain.getInstance().actionGyroTurn(90);
				break;
				
			case 3:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				if (Intake.getInstance().getWristPosition() < 200) {
					Intake.getInstance().actionTiltClosed();
				} else {
					Intake.getInstance().actionTiltOpen();
				}
				Drivetrain.getInstance().setMotorPower(0, 0);
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
				return Drivetrain.getInstance().getEncoderDistance() > 4;
				
			case 1:
				return Drivetrain.getInstance().getEncoderWithinDistance(7.5, 0.1);
			
			case 2:
				return Drivetrain.getInstance().getAngleWithinRange(90, 6);
				
			default:
				return false;
		}
		
	}
}
