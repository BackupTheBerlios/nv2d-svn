package nv2d.plugins.standard.layout;

import java.awt.geom.Line2D;
import edu.berkeley.guir.prefusex.force.AbstractForce;
import edu.berkeley.guir.prefusex.force.ForceItem;

/**
 * Extends WallForce to allow dynamically setting the line points.
 */
public class SmartWallForce extends AbstractForce {
    
    private static String[] pnames = new String[] { "GravitationalConstant" };
    
    public static final float DEFAULT_GRAV_CONSTANT = -0.1f;
    public static final int GRAVITATIONAL_CONST = 0;
    
    private float x1, y1, x2, y2;
    private float dx, dy;
    
    private boolean _isEnabled;
    
    public SmartWallForce(float gravConst, 
        float x1, float y1, float x2, float y2) 
    {
        params = new float[] { gravConst };
        this.x1 = x1; this.y1 = y1;
        this.x2 = x2; this.y2 = y2;
        dx = x2-x1;
        dy = y2-y1;
        float r = (float)Math.sqrt(dx*dx+dy*dy);
        if ( dx != 0.0 ) dx /= r;
        if ( dy != 0.0 ) dy /= r;
        
        _isEnabled = true;
    } //
    
    public SmartWallForce(float x1, float y1, float x2, float y2) {
        this(DEFAULT_GRAV_CONSTANT,x1,y1,x2,y2);
    } //
    
    public boolean isItemForce() {
        return true;
    } //
    
    protected String[] getParameterNames() {
        return pnames;
    } //
    
    public void getForce(ForceItem item) {
        if(_isEnabled) {
            float[] n = item.location;
            int ccw = Line2D.relativeCCW(x1,y1,x2,y2,n[0],n[1]);
            float r = (float)Line2D.ptSegDist(x1,y1,x2,y2,n[0],n[1]);
            if ( r == 0.0 ) r = (float)Math.random() / 100.0f;
            float v = params[GRAVITATIONAL_CONST]*item.mass / (r*r*r);
            if ( n[0] >= Math.min(x1,x2) && n[0] <= Math.max(x1,x2) )
                item.force[1] += ccw*v*dx;
            if ( n[1] >= Math.min(y1,y2) && n[1] <= Math.max(y1,y2) )
                item.force[0] += -1*ccw*v*dy;
        }
    } //

    
    // -- Getters and Setters --
    
    public float getX1() {
        return x1;
    }

    public float getX2() {
        return x2;
    }
    
    public float getY1() {
        return y1;
    }
    
    public float getY2() {
        return y2;
    }
    
    public void setX1(float x) {
        x1 = x;
    }

    public void setX2(float x) {
        x2 = x;
    }

    public void setY1(float y) {
        y1 = y;
    }

    public void setY2(float y) {
        y2 = y;
    }

    public float getGravConst() {
        return params[GRAVITATIONAL_CONST];
    }

    public void setGravConst(float f) {
        params[GRAVITATIONAL_CONST] = f;
    }

    public void setEnabled(boolean b) {
        _isEnabled = b;
    }

    public boolean isEnabled() {
        return _isEnabled;
    }
    
    
}

