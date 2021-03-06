package org.team4213.wilson;


import edu.wpi.first.wpilibj.IterativeRobot;
import org.team4213.lib14.AIRFLOController;
import org.team4213.lib14.MCRIterativeRobot;

/**
 * This is the entry point!
 * 
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation.
 */
public class Wilson extends MCRIterativeRobot {
    AIRFLOController user = new AIRFLOController(1);
    KiwiDrive drive = new KiwiDrive();
    
    public Wilson() {
        
    }
    
    
    /**
     * This function is run once when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        
    }
    
    /**
     * This function is called once when autonomous starts.
     * It is blocking.
     */
    public void autonomousInit() {
        
    }

    /**
     * This function is called periodically (at about 100Hz) during autonomous.
     * It is blocking.
     */
    public void autonomousPeriodic() {

    }
    
    /**
     * This function is called once when teleop starts.
     * It is blocking.
     * 
     * This should be used for updating what each subsystem should do.
     */
    public void teleopInit() {
        drive.headingController.reset();
    }

    /**
     * This function is called periodically (at about 100Hz) during teleop.
     * It is blocking.
     */
    public void teleopPeriodic() {
        // Toggles
        if (user.getButtonTripped(9)) drive.toggleRegulateHeading();
        if (user.getButtonTripped(10)) drive.toggleFieldOriented();
        if (user.getButtonTripped(11)) drive.toggleHaloDrive();
        
        // Maps buttons 1-4 of the gamepad to cardinal directions on the field
        drive.setHeading(user.getHeadingPadCardinal());
        
        // Sends joystick data to the drivetrain
        drive.driveXYW(user.getLX(), user.getLY(), user.getRX(), user.getThrottle());
    }
    
}
