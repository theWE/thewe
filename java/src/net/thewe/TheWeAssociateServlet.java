package net.thewe;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

//import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.wave.api.AbstractRobotServlet;
import com.google.wave.api.Annotation;
import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.Gadget;
import com.google.wave.api.GadgetView;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.impl.OperationImpl;
import com.google.wave.api.impl.OperationType;
import com.google.wave.api.impl.RobotMessageBundleImpl;



@SuppressWarnings("serial")
public class TheWeAssociateServlet extends AbstractRobotServlet {
	private static String SPACES = "                                                                               ";
	private HashSet<Object> set = new HashSet<Object>();

	private Gadget cloneGadget(Gadget ggg) {

		Gadget newGGG = new Gadget(ggg.getUrl());

		for (Map.Entry<String, Object> mEntry : ggg.getProperties().entrySet()) {
			newGGG.setField(mEntry.getKey(), mEntry.getValue().toString());
		}
		return newGGG;
	}

	private static String[] ME = {"wavesandbox.com!w+G0ubV_o8%H",
		"wavesandbox.com!conv+root",
		"b+G0ubV_o8%I"};
	
	public void processEvents(RobotMessageBundle bundle) {
		if (!bundle.getWavelet().getWaveId().equals(ME[0])) {
			for (Event e : bundle.getEvents()) {		
				log ("Got event: " + e.getType());
				
				String blipText = e.getBlip().getDocument().getText();
	
				log("BlipId: " + e.getBlip().getBlipId() + " Annotations: " + e.getBlip().getDocument().getAnnotations().size());
				
				((RobotMessageBundleImpl) bundle)
				.addOperation(new OperationImpl(
						OperationType.DOCUMENT_APPEND, ME[0], ME[1], ME[2], 0, "\nHiiiiii!"));
	
				
				log("Sending append operation to other wave.");
			}
		}
		else {
			log("Acted on ME, doing nothing");
		}
	}
}
