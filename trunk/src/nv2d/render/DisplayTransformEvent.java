package nv2d.render;

import java.util.EventObject;

public class DisplayTransformEvent extends EventObject {

    public static final int PAN_TRANSFORM_EVENT      = 0;
    public static final int ZOOM_TRANSFORM_EVENT     = 1;
    public static final int PAN_ZOOM_TRANSFORM_EVENT = 2;
    public static final int AFFINE_TRANSFORM_EVENT   = 3;

    private int _type;

    
    public DisplayTransformEvent(Object source, int type) {
        super(source);
        _type = type;
    }

    public int getEventType() {
        return _type;
    }

}