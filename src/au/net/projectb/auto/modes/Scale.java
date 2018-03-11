package au.net.projectb.auto.modes;

import au.net.projectb.auto.AutoController.FieldPosition;
import au.net.projectb.subsystems.Drivetrain;
import au.net.projectb.subsystems.Intake;
import au.net.projectb.subsystems.Lift;
import au.net.projectb.subsystems.Lift.LiftPosition;
import edu.wpi.first.wpilibj.DriverStation;

@Deprecated
public class Scale extends AutoMode {
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
				switch (startPosition) {
					case LEFT:
						if (scalePosition == FieldPosition.LEFT) {
							// do L-L
							Drivetrain.getInstance().setMotorPower(0.6, -0.6);
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do L-R
						}
						break;
					case CENTRE:
						if (scalePosition == FieldPosition.LEFT) {
//							Drivetrain.getInstance().setMotorPower(0.1, -0.5);
						} else if (scalePosition == FieldPosition.RIGHT) {
//							Drivetrain.getInstance().setMotorPower(0.4, -0.1);
						}
						break;
					case RIGHT:
						if (scalePosition == FieldPosition.LEFT) {
							// do R-L
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do R-R
							Drivetrain.getInstance().setMotorPower(0.6, -0.6);
						}
						break;
					default:
						break;
				}
				break;
				
			case 1:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				Intake.getInstance().actionStow();
				switch (startPosition) {
					case LEFT:
						if (scalePosition == FieldPosition.LEFT) {
							// do L-L
							Drivetrain.getInstance().setMotorPower(0.0, -0.0);
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do L-R
						}
						break;
					case CENTRE:
						if (scalePosition == FieldPosition.LEFT) {
//							Drivetrain.getInstance().setMotorPower(0.5, -0.1);
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do C-R
//							Drivetrain.getInstance().setMotorPower(0.1, -0.5);
						}
						break;
					case RIGHT:
						if (scalePosition == FieldPosition.LEFT) {
							// do R-L
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do R-R
							Drivetrain.getInstance().setMotorPower(0.0, -0.0);
						}
						break;
					default:
						break;
				}
				break;
				
			case 2:
				Lift.getInstance().actionMoveTo(LiftPosition.SCALE_HI);
				Intake.getInstance().actionStow();
				switch (startPosition) {
					case LEFT:
						if (scalePosition == FieldPosition.LEFT) {
							// do L-L
							Drivetrain.getInstance().setMotorPower(0.3, 0.2);
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do L-R
						}
						break;
					case CENTRE:
						if (scalePosition == FieldPosition.LEFT) {
							Drivetrain.getInstance().setMotorPower(0.0, -0.0);
						} else if (scalePosition == FieldPosition.RIGHT) {
							Drivetrain.getInstance().setMotorPower(0.0, -0.0);
						}
						break;
					case RIGHT:
						if (scalePosition == FieldPosition.LEFT) {
							// do R-L
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do R-R
							Drivetrain.getInstance().setMotorPower(-0.2, -0.3);
						}
						break;
					default:
						break;
				}
				break;
				
			case 3:
				Intake.getInstance().actionScoreCube();
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
				switch (startPosition) {
					case LEFT:
						if (scalePosition == FieldPosition.LEFT) {
							// do L-L
							return DriverStation.getInstance().getMatchTime() < 12.75;
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do L-R
							return false;
						} else {
							return true;
						}
					case CENTRE:
						if (scalePosition == FieldPosition.LEFT) {
							return DriverStation.getInstance().getMatchTime() < 11.75;
						} else if (scalePosition == FieldPosition.RIGHT) {
							return Drivetrain.getInstance().getAngle() >= 40;
						} else {
							return true;
						}
					case RIGHT:
						if (scalePosition == FieldPosition.LEFT) {
							// do R-L
							return false;
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do R-R
							return DriverStation.getInstance().getMatchTime() < 12.75;
						} else {
							return true;
						}
					default:
						return false;
				}
				
			case 1:
				switch (startPosition) {
					case LEFT:
						if (scalePosition == FieldPosition.LEFT) {
							// do L-L
							return Lift.getInstance().getElbowPosition() > 5000;
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do L-R
							return false;
						} else {
							return true;
						}
					case CENTRE:
						if (scalePosition == FieldPosition.LEFT) {
							return Drivetrain.getInstance().getAngle() >= -20;
						} else if (scalePosition == FieldPosition.RIGHT) {
							return Drivetrain.getInstance().getAngle() <= 20;
						} else {
							return true;
						}
					case RIGHT:
						if (scalePosition == FieldPosition.LEFT) {
							// do R-L
							return false;
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do R-R
							return Lift.getInstance().getElbowPosition() > 5000;
						} else {
							return true;
						}
					default:
						return false;
				}
			
			case 2:
				switch (startPosition) {
					case LEFT:
						if (scalePosition == FieldPosition.LEFT) {
							// do L-L
							return Drivetrain.getInstance().getAngle() >= 55;
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do L-R
							return false;
						} else {
							return true;
						}
					case CENTRE:
						if (scalePosition == FieldPosition.LEFT) {
							return Drivetrain.getInstance().getAngle() >= -20;
						} else if (scalePosition == FieldPosition.RIGHT) {
							return Drivetrain.getInstance().getAngle() <= 20;
						} else {
							return true;
						}
					case RIGHT:
						if (scalePosition == FieldPosition.LEFT) {
							// do R-L
							return false;
						} else if (scalePosition == FieldPosition.RIGHT) {
							// do R-R
							return Drivetrain.getInstance().getAngle() <= -55;
						} else {
							return true;
						}
					default:
						return false;
				}
				
			default:
				return false;
		}
		
	}
}
