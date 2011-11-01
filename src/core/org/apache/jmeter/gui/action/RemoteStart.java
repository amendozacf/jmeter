// $Header$
/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.apache.jmeter.gui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.jmeter.engine.ClientJMeterEngine;
import org.apache.jmeter.engine.JMeterEngine;
import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * @author Michael Stover
 * @author Drew Gulino
 * @version $Revision$ Last updated $Date$
 */
public class RemoteStart extends AbstractAction {
	transient private static Logger log = LoggingManager.getLoggerForClass();

	private static Set commands = new HashSet();
	static {
		commands.add("remote_start");
		commands.add("remote_stop");
		commands.add("remote_start_all");
		commands.add("remote_stop_all");
		commands.add("remote_exit");
		commands.add("remote_exit_all");
	}

	private Map remoteEngines = new HashMap();

	public RemoteStart() {
	}

	public void doAction(ActionEvent e) {
		String name = ((Component) e.getSource()).getName();
		if (name != null) {
			name = name.trim();
		}
		String action = e.getActionCommand();
		if (action.equals("remote_stop")) {
			doRemoteStop(name);
		} else if (action.equals("remote_start")) {
			popupShouldSave(e);
			doRemoteInit(name);
			doRemoteStart(name);
		} else if (action.equals("remote_start_all")) {
			popupShouldSave(e);
			String remote_hosts_string = JMeterUtils.getPropDefault("remote_hosts", "127.0.0.1");
			java.util.StringTokenizer st = new java.util.StringTokenizer(remote_hosts_string, ",");
			while (st.hasMoreElements()) {
				String el = (String) st.nextElement();
				doRemoteInit(el.trim());
			}
			st = new java.util.StringTokenizer(remote_hosts_string, ",");
			while (st.hasMoreElements()) {
				String el = (String) st.nextElement();
				doRemoteStart(el.trim());
			}
		} else if (action.equals("remote_stop_all")) {
			String remote_hosts_string = JMeterUtils.getPropDefault("remote_hosts", "127.0.0.1");
			java.util.StringTokenizer st = new java.util.StringTokenizer(remote_hosts_string, ",");
			while (st.hasMoreElements()) {
				String el = (String) st.nextElement();
				doRemoteStop(el.trim());
			}
		} else if (action.equals("remote_exit")) {
			doRemoteExit(name);
		} else if (action.equals("remote_exit_all")) {
			String remote_hosts_string = JMeterUtils.getPropDefault("remote_hosts", "127.0.0.1");
			java.util.StringTokenizer st = new java.util.StringTokenizer(remote_hosts_string, ",");
			while (st.hasMoreElements()) {
				String el = (String) st.nextElement();
				doRemoteExit(el.trim());
			}
		}
	}

	/**
	 * Stops a remote testing engine
	 * 
	 * @param name
	 *            the DNS name or IP address of the remote testing engine
	 * 
	 */
	private void doRemoteStop(String name) {
		GuiPackage.getInstance().getMainFrame().showStoppingMessage(name);
		JMeterEngine engine = (JMeterEngine) remoteEngines.get(name);
		engine.stopTest();
	}

	/**
	 * Exits a remote testing engine
	 * 
	 * @param name
	 *            the DNS name or IP address of the remote testing engine
	 * 
	 */
	private void doRemoteExit(String name) {
		JMeterEngine engine = (JMeterEngine) remoteEngines.get(name);
		if (engine == null)
			return;
		// GuiPackage.getInstance().getMainFrame().showStoppingMessage(name);
		engine.exit();
	}

	/**
	 * Starts a remote testing engine
	 * 
	 * @param name
	 *            the DNS name or IP address of the remote testing engine
	 * 
	 */
	private void doRemoteStart(String name) {
		JMeterEngine engine = (JMeterEngine) remoteEngines.get(name);
		if (engine == null) {
			try {
				engine = new ClientJMeterEngine(name);
				remoteEngines.put(name, engine);
			} catch (Exception ex) {
				log.error("", ex);
				JMeterUtils.reportErrorToUser("Bad call to remote host");
				return;
			}
		} else {
			engine.reset();
		}
		startEngine(engine, name);
	}

	/**
	 * Initializes remote engines
	 */
	private void doRemoteInit(String name) {
		JMeterEngine engine = (JMeterEngine) remoteEngines.get(name);
		if (engine == null) {
			try {
				engine = new ClientJMeterEngine(name);
				remoteEngines.put(name, engine);
			} catch (Exception ex) {
				log.error("", ex);
				JMeterUtils.reportErrorToUser("Bad call to remote host");
				return;
			}
		} else {
			engine.reset();
		}
		initEngine(engine, name);
	}

	public Set getActionNames() {
		return commands;
	}

	/**
	 * Initializes test on engine.
	 * 
	 * @param engine
	 *            remote engine object
	 * @param host
	 *            host the engine will run on
	 */
	private void initEngine(JMeterEngine engine, String host) {
		GuiPackage gui = GuiPackage.getInstance();
		HashTree testTree = gui.getTreeModel().getTestPlan();
		convertSubTree(testTree);
		testTree.add(testTree.getArray()[0], gui.getMainFrame());
		engine.configure(testTree);
	}

	/**
	 * Starts the test on the remote engine.
	 */
	private void startEngine(JMeterEngine engine, String host) {
		GuiPackage gui = GuiPackage.getInstance();
		try {
			engine.runTest();
		} catch (JMeterEngineException e) {
			JOptionPane.showMessageDialog(gui.getMainFrame(), e.getMessage(), JMeterUtils
					.getResString("Error Occurred"), JOptionPane.ERROR_MESSAGE);
		}
	}
}