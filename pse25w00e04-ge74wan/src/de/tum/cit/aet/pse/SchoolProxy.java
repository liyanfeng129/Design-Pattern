package de.tum.cit.aet.pse;

import java.net.URL;
import java.util.Set;

public class SchoolProxy implements ConnectionInterface {
	private Set<String> denylistedHosts;
	private URL redirectPage;
	private Set<Integer> teacherIDs;
	private boolean authorized;
	private NetworkConnection networkConnection;

	public SchoolProxy(Set<String> denylistedHosts, URL redirectPage, Set<Integer> teacherIDs) {
		this.denylistedHosts = denylistedHosts;
		this.redirectPage = redirectPage;
		this.teacherIDs = teacherIDs;
		this.authorized = false;
		this.networkConnection = new NetworkConnection();
	}

	// TODO: Implement the SchoolProxy
	@Override
	public void connect(URL url) {
		// Implement connection logic
		if (!authorized && denylistedHosts.contains(url.getHost())) {
			System.err.println("Connection to " + url + " was rejected.");
			System.out.println("Redirecting to " + redirectPage);
			url = redirectPage;
		}
		networkConnection.connect(url);
	}

	@Override
	public void disconnect() {
		// Implement disconnection logic
		networkConnection.disconnect();
	}

	@Override
	public boolean isConnected() {
		return networkConnection.isConnected();
	}

	public void login(int teacherID) {
		if (teacherIDs.contains(teacherID)) {
			authorized = true;
			System.out.println("Login successful for teacher ID: " + teacherID);
		} else {
			System.err.println("Login failed for teacher ID: " + teacherID);
		}
	}

	public void logout() {
		authorized = false;
		System.out.println("Logged out successfully.");
	}
}
