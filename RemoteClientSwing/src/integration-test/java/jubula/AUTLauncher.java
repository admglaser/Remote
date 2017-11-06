package jubula;

import java.io.IOException;

import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;

public class AUTLauncher {

	public static void main(String[] args) throws IOException {
		Configuration configuration = new Configuration();
		
		AUTAgent autAgent = MakeR.createAUTAgent(configuration.getHost(), configuration.getPort());
		autAgent.connect();
		
		AUTUtils.startAut(autAgent, configuration.getId(), configuration.getExecutable(), configuration.getWorkDir());
	
		autAgent.disconnect();
	}
	
}
