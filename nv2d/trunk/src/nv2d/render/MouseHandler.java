package nv2d.render;

import java.lang.ClassCastException;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

public class MouseHandler extends PBasicInputEventHandler {
	/** Not null between time that mouse clicks and releases on a node. This
	 * only applies to graph elements (RElement). */
	private PNode _grabbed = null;

	public void mouseClicked(PInputEvent event) {
		// super.mouseClicked(event);

		_grabbed = event.getPickedNode();

		try
		{
			_grabbed = (VertexNode) _grabbed;
			System.out.println("_grabbed = " + _grabbed);
		}
		catch(ClassCastException err)
		{
			_grabbed = null;
		}
	}
	public void mouseDragged(PInputEvent event) {
		//super.mouseDragged(event);
		
		mouseClicked(event);

		if(_grabbed != null) {
			// we are dragging a node around
			PDimension delta = event.getDelta();
			_grabbed.translate(delta.getWidth(), delta.getHeight());
		}
	}
	public void mouseEntered(PInputEvent event) {
		super.mouseEntered(event);
		
		// highlight a node if over it
	}
	public void mouseExited(PInputEvent event) {
		super.mouseExited(event);

		// highlight a node if over it
	}
	public void mouseMoved(PInputEvent event) {
		super.mouseMoved(event);
	}
	public void mousePressed(PInputEvent event) {
		super.mousePressed(event);
	}
	public void mouseReleased(PInputEvent event)  {
		super.mouseReleased(event);

		_grabbed = null;
	}
}
