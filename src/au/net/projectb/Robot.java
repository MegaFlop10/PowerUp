package au.net.projectb;

import au.net.projectb.auto.AutoController;
import au.net.projectb.subsystems.Drivetrain;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	Compressor compressor;
	
	TeleopController teleop;
	AutoController auto;
	Tuning tuner;
	
	@Override
	public void robotInit() {
		compressor = new Compressor(Constants.kPcm);
		
		teleop = new TeleopController();
		auto = new AutoController();
		tuner = new Tuning();
		
		Drivetrain.getInstance().setEncoderCounts(0);
		}

	@Override
	public void autonomousInit() {
		compressor.start();
		auto.init();
	}

	@Override
	public void autonomousPeriodic() {
		SmartDashboard.putNumber("Gyro", Drivetrain.getInstance().getAngle());
		auto.run();
	}

	@Override
	public void teleopPeriodic() {
		compressor.start();
//		compressor.stop(); // Debugging at this point
		teleop.run();
//		tuner.run();
	}
}
