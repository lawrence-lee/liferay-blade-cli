/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.File;

/**
 * @author Gregory Amerson
 */
@Parameters(commandDescription = "Opens or imports a file or project in Liferay IDE.", commandNames = "open")
public class OpenArgs extends BaseArgs {

	public String getFile() {
		return _file;
	}

	public String getWorkspace() {
		return _workspace;
	}

	public void setFile(File file) {
		_file = file.getAbsolutePath();
	}

	@Parameter(description = "file or directory to open/import")
	private String _file;

	@Parameter(description = "The workspace to open or import this file or project", names = {"-w", "--workspace"})
	private String _workspace;

}