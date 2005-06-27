package nv2d.plugins.standard.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefusex.force.Force;

/**
 */
public class JiggleAction extends AbstractAction {
    Force nBodyForce;
    int gravIndex;
    float origGravValue;
    Force springForce;
    int springIndex;
    float origSpringValue;
    double prev_frac = 1;
    
    public JiggleAction(Force n, int gi, Force s, int si) {
        nBodyForce = n;
        gravIndex = gi;
        springForce = s;
        springIndex = si;
    }
    
    /**
     */
    public void run(ItemRegistry registry, double frac) {
        // run once on first time
        if(prev_frac > frac && frac < 0.1) {
            // get original values
            origGravValue = nBodyForce.getParameter(gravIndex);
            origSpringValue = springForce.getParameter(springIndex);
            // set nBody very low
            System.out.println("Setting gravConst low:" + frac + " " + origGravValue);
            nBodyForce.setParameter(gravIndex, origGravValue - 100);
            prev_frac = frac;
        }
        else if(frac < 0.2) {
            // set spring coeff very high
            System.out.println("Setting spring:" + frac);
            springForce.setParameter(springIndex, 4.0E-4f);
        }
        else if(frac > 0.9) {
            System.out.println("Restoring Original Values");
            // restore original values
            nBodyForce.setParameter(gravIndex, origGravValue);
            springForce.setParameter(springIndex, origSpringValue);
        }
    } //

    
} // end of class JiggleAction
