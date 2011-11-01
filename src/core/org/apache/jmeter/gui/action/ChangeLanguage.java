// $Header$
/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * @version $Revision$
 */
public class ChangeLanguage implements Command {
	private static final Set commands = new HashSet();

	public final static String CHANGE_LANGUAGE = "change_language";

	private Logger log = LoggingManager.getLoggerForClass();

	static {
		commands.add(CHANGE_LANGUAGE);
	}

	/**
	 * @see org.apache.jmeter.gui.action.Command#doAction(ActionEvent)
	 */
	public void doAction(ActionEvent e) {
		String locale = ((Component) e.getSource()).getName();
		Locale loc;

		int sep = locale.indexOf('_');
		if (sep > 0) {
			loc = new Locale(locale.substring(0, sep), locale.substring(sep + 1));
		} else {
			loc = new Locale(locale, "");
		}
		log.debug("Changing locale to " + loc.toString());
		JMeterUtils.setLocale(loc);
	}

	/**
	 * @see org.apache.jmeter.gui.action.Command#getActionNames()
	 */
	public Set getActionNames() {
		return commands;
	}
}