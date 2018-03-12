package au.net.projectb.auto;

import au.net.projectb.subsystems.Drivetrain;
import au.net.projectb.auto.modes.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoController {
	SendableChooser<FieldPosition> startSelector;
	SendableChooser<AutoSelection> autoSelector;
	
	FieldPosition startPosition;
	FieldPosition switchPosition;
	FieldPosition scalePosition;
	
	AutoSelection selectedAuto;
	boolean runTwoCube;
	AutoMode runningAuto;
	int currentStep;
	
	public enum FieldPosition {
		LEFT,
		CENTRE,
		RIGHT,
		DEFAULT
	}
	
	public enum AutoSelection {
		AUTO_LINE,
		
		ONLY_SWITCH,
		ONLY_SCALE,
		
		PREFER_SWITCH,
		PREFER_SCALE,
		
		TRY_TWOCUBE,
	}
	
	public AutoController() {
		startSelector = new SendableChooser<FieldPosition>();
		startSelector.addDefault("Select Starting Position", FieldPosition.DEFAULT);
		startSelector.addObject("Centre", FieldPosition.CENTRE);
		startSelector.addObject("Left", FieldPosition.LEFT);
		startSelector.addObject("Right", FieldPosition.RIGHT);
		SmartDashboard.putData("Start Position Selector", startSelector);
		
		autoSelector = new SendableChooser<AutoSelection>();	
		// Various auto modes
		autoSelector.addDefault("Auto Line", AutoSelection.AUTO_LINE);
		autoSelector.addObject("Prefer Switch", AutoSelection.PREFER_SWITCH);
		autoSelector.addObject("Prefer Scale", AutoSelection.PREFER_SCALE);
		autoSelector.addObject("Switch", AutoSelection.ONLY_SWITCH);
		autoSelector.addObject("Scale", AutoSelection.ONLY_SCALE);
		autoSelector.addObject("Test Two Cube", AutoSelection.TRY_TWOCUBE);
		
		SmartDashboard.setDefaultBoolean("Two Cube Auto", true);
		
		SmartDashboard.putData("Auto Selector", autoSelector);
	}
	
	public void init() {
		Drivetrain.getInstance().zeroGyro();
		Drivetrain.getInstance().setEncoderCounts(0);
		
		while (DriverStation.getInstance().getGameSpecificMessage() == null) {
			// wait for it to come in
			DriverStation.getInstance();
			DriverStation.reportWarning("AUTO: Game Message null!", false);
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Define field positions
		startPosition = startSelector.getSelected();
		
		if (DriverStation.getInstance().getGameSpecificMessage().charAt(0) == 'L') {  // Switch
			switchPosition = FieldPosition.LEFT;
		} else {
			switchPosition = FieldPosition.RIGHT;
		}		
		if (DriverStation.getInstance().getGameSpecificMessage().charAt(1) == 'L') {  // Scale
			scalePosition = FieldPosition.LEFT;
		} else {
			scalePosition = FieldPosition.RIGHT;
		}
		
		runTwoCube = SmartDashboard.getBoolean("Two Cube Auto", true);
		
		selectedAuto = autoSelector.getSelected();
		runningAuto = evaluateAutoSelection(selectedAuto);
				
		runningAuto.setFieldPositions(startPosition, switchPosition, scalePosition);
		currentStep = 0;
	}
	
	public void run() {
		// getStepIsCompleted default returns false.
		if (!runningAuto.getStepIsCompleted(currentStep)) {
			runningAuto.runStep(currentStep);
		} else {
			currentStep++;
		}
	}
	
	/**
	 * 
	 * @param auto
	 * @return
	 */
	public AutoMode evaluateAutoSelection(AutoSelection auto) {
		switch (auto) {
			case PREFER_SWITCH:
				if (switchPosition == startPosition) {
					if (switchPosition == FieldPosition.LEFT) {
						return new LeftSwitchLeft();
					} else {
						return new RightSwitchRight();
					}
				
				} else if (startPosition == FieldPosition.CENTRE) {
					if (switchPosition == FieldPosition.LEFT) {
						return new CentreSwitchLeft();
					} else {
						return new CentreSwitchRight();
					}
				
				} else if (scalePosition == startPosition) {
					if (scalePosition == FieldPosition.LEFT) {
						if (runTwoCube) {
							return new LeftScaleLeftScaleLeft();
						} else {
							return new LeftScaleLeft();
						}
					} else {
						return new RightScaleRight();
					}
				} else if (startPosition == FieldPosition.LEFT){
					return new LeftScaleRight();
				} else {
					return new RightScaleLeft();
				}
				
			case PREFER_SCALE: // Stay side
				if (scalePosition == startPosition) {
					if (scalePosition == FieldPosition.LEFT) {
						if (runTwoCube) {
							return new LeftScaleLeftScaleLeft();
						} else {
							return new LeftScaleLeft();
						}
					} else {
						return new RightScaleRight();
					}
				
				} else if (startPosition == FieldPosition.CENTRE) {
					if (switchPosition == FieldPosition.LEFT) {
						return new CentreSwitchLeft();
					} else {
						return new CentreSwitchRight();
					}
				
				} else if (switchPosition == startPosition) {
					if (switchPosition == FieldPosition.LEFT) {
						return new LeftSwitchLeft();
					} else {
						return new RightSwitchRight();
					}
				} else {
					return new Default();
				}
				
			case ONLY_SWITCH:
				if (switchPosition == startPosition) {
					if (switchPosition == FieldPosition.LEFT) {
						return new LeftSwitchLeft();
					} else {
						return new RightSwitchRight();
					}
				
				} else if (startPosition == FieldPosition.CENTRE) {
					if (switchPosition == FieldPosition.LEFT) {
						return new CentreSwitchLeft();
					} else {
						return new CentreSwitchRight();
					}
				} else {
					return new Default();
				}
				
			case ONLY_SCALE:
				if (scalePosition == startPosition) {
					if (scalePosition == FieldPosition.LEFT) {
						if (runTwoCube) {
							return new LeftScaleLeftScaleLeft();
						} else {
							return new LeftScaleLeft();
						}
					} else {
						return new RightScaleRight();
					}
				
				} else if (startPosition == FieldPosition.LEFT) {
					return new LeftScaleRight();
				} else if (startPosition == FieldPosition.RIGHT) {
					return new RightScaleLeft();
				} else {
					return new Default();
				}
				
			case TRY_TWOCUBE:
				if (startPosition == FieldPosition.LEFT) {
					return new LeftScaleLeftScaleLeft();
				}
						
			default:
				return new Default();
		}
	}
	
	/**
	 * Returns the amount of time elapsed since the start of the game period.
	 * @return seconds
	 */
	public double getElapsedTime() {
		return 15 - DriverStation.getInstance().getMatchTime();
	}
}
