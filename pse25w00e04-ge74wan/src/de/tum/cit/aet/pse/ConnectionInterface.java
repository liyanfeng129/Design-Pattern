package de.tum.cit.aet.pse;

import java.net.URL;

public interface ConnectionInterface {

	void connect(URL url);

	void disconnect();

	boolean isConnected();

}
