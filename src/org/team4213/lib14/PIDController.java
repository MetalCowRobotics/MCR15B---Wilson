/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team4213.lib14;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Enumeration;
import java.util.Vector;

/**
 * An ErrorController implemented with a simple Proportional-(limited)Integral-Derivative controller
 * 
 * @author hughest1
 */
public class PIDController extends ErrorController {
    /**
     * Constants for the controller
     * @var kp the proportional gain
     * @var ki the integral gain
     * @var kd the derivative gain
     * @var integralLifespan how (in seconds) long the positionData will go for
     */
    public double kp, ki, kd, integralLifespan;
    
    // Previous position data
    Vector positionData = new Vector();
    
    // One of the data points for positions
    private class PositionDataPoint {
        public double value;
        public Timer time;
        /**
         * Creates a new PositionDataPoint and starts its timer.
         * @param val the value at time of creation
         */
        public PositionDataPoint(double val) {
            this.value = val;
            time = new Timer();
            time.reset();
            time.start();
        }
    }
    
    /**
     * Creates a new PID controller.
     * 
     * @param kp the proportional gain
     * @param ki the integral gain
     * @param maxIInfluence the maximum influence the integral is allowed to have on the response
     * @param kd the derivative gain
     */
    public PIDController(String name, double kp, double ki, double kd, double integralLifespan) {
        super(name);
        this.kp=kp;
        this.ki=ki;
        this.integralLifespan=integralLifespan;
        this.kd=kd;
    }
    
    /**
     * Resets the past data points to nothing.
     */
    public void reset() {
        positionData = new Vector();
    }
    
    
    public double feedAndGetValue(double currentValue) { 
        // Read constants values off of the CowDash
        kp=CowDash.getNumber(name+"::kp",kp);
        ki=CowDash.getNumber(name+"::ki",ki);
        kd=CowDash.getNumber(name+"::kd",kd);
        integralLifespan=CowDash.getNumber(name+"::integralLifespan", integralLifespan);
        
        
        // Current error is target minus current value
        PositionDataPoint thisValue = new PositionDataPoint(currentValue);
        CowDash.putNumber(name+"::error", target-thisValue.value);
        
        
        double integral = 0;
        double derivative = 0;
        // If we have data on the past, integral and derivative can be computed.
        if (positionData.size() > 0) {
            // Compute integral by summing up all errors contained within the positionData, weighted by time inbetween each.
            Enumeration e = positionData.elements();
            PositionDataPoint lastElement = (PositionDataPoint)e.nextElement();
            while( e.hasMoreElements() ) {
                PositionDataPoint currentElement = (PositionDataPoint) e.nextElement();
                integral += (target - currentElement.value) * (lastElement.time.get() - currentElement.time.get());
            }
        
            // Compute derivative by subtracting current value from last recorded, divided by time inbetween.
            PositionDataPoint lastData = ((PositionDataPoint)positionData.lastElement());
            derivative =  (lastData.value - thisValue.value)/lastData.time.get();
        }
        
        // Add this data point to the position data history
        positionData.addElement(thisValue);
        // Trim old entries from the position data history
        while(((PositionDataPoint)positionData.firstElement()).time.get() > integralLifespan)
            positionData.removeElementAt(0);
        
        // Log info about the integral and derivative
        CowDash.putNumber(name+"::integral", integral);
        CowDash.putNumber(name+"::derivative", derivative);
        
        // Return summed terms: Proportional, Integral, Derivative
        return (target-thisValue.value)*kp/1000 + integral*ki/1000 + derivative*kd/1000;
        
    }
}
