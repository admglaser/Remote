package jubula;

import java.util.List;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.toolkit.swing.SwingComponents;
import org.eclipse.jubula.toolkit.swing.config.SwingAUTConfiguration;
import org.eclipse.jubula.tools.AUTIdentifier;

public class AUTUtils {

	public static AUT getRunningAut(AUTAgent autAgent, String id) {
		List<AUTIdentifier> allRegisteredAUTIdentifier = autAgent.getAllRegisteredAUTIdentifier();
		for (AUTIdentifier autIdentifier : allRegisteredAUTIdentifier) {
			if (autIdentifier.getID().equals(id)) {
				return autAgent.getAUT(autIdentifier, SwingComponents.getToolkitInformation());
			}
		}
		throw new RuntimeException("AUT with id " + id + " is not running.");
	}

	public static AUT startAut(AUTAgent autAgent, String id, String executable, String workDir) {
		AUTConfiguration autConfiguration = new SwingAUTConfiguration(id, id, executable, workDir, null);
		AUTIdentifier autIdentifier = autAgent.startAUT(autConfiguration);
		return autAgent.getAUT(autIdentifier, SwingComponents.getToolkitInformation());
	}

}
