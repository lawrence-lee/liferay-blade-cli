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

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.TestUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author David Truong
 */
public class ConvertThemeCommandTest {

	@Test
	public void testListThemes() throws Exception {
		File workspace = _createWorkspace();

		String[] args = {"--base", workspace.getAbsolutePath(), "convert", "-l"};

		BladeTestResults bladeTestResults = TestUtil.runBlade(temporaryFolder.getRoot(), args);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output, output.contains("compass-theme"));
	}

	@Ignore
	@Test
	public void testMigrateCompassTheme() throws Exception {
		File workspace = _createWorkspace();

		String[] args = {"--base", workspace.getAbsolutePath(), "convert", "-a"};

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PrintStream out = new PrintStream(baos);

		BladeTest bladeTest = new BladeTest(out, null, System.in, temporaryFolder.getRoot());

		bladeTest.run(args);

		File oldCompassTheme = new File(workspace, "plugins-sdk/themes/compass-theme");

		Assert.assertTrue(!oldCompassTheme.exists());

		File compassTheme = new File(workspace, "themes/compass-theme");

		Assert.assertTrue(compassTheme.exists());

		File packageJsonFile = new File(compassTheme, "package.json");

		String json = new String(Files.readAllBytes(packageJsonFile.toPath()));

		Assert.assertTrue(json.contains("\"supportCompass\": true"));

		File nonCompassTheme = new File(workspace, "themes/non-compass-theme");

		Assert.assertTrue(compassTheme.exists());

		packageJsonFile = new File(nonCompassTheme, "package.json");

		json = new String(Files.readAllBytes(packageJsonFile.toPath()));

		Assert.assertTrue(json.contains("\"supportCompass\": false"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _createTheme(File workspace, String themeName, boolean compass) throws Exception {
		File theme = new File(workspace, "plugins-sdk/themes/" + themeName);

		File diffs = new File(theme, "/docroot/_diffs/css");

		diffs.mkdirs();

		String css = "";

		if (compass) {
			css = "@import \"compass\";";
		}

		File customCss = new File(diffs, "custom.css");

		Files.write(customCss.toPath(), css.getBytes());

		File webInf = new File(theme, "/docroot/WEB-INF/");

		webInf.mkdirs();

		String xml = "";

		File lookAndFeelXml = new File(webInf, "liferay-look-and-feel.xml");

		Files.write(lookAndFeelXml.toPath(), xml.getBytes());

		String properties = "liferay-versions=7.0.0+";

		File liferayPluginPackage = new File(webInf, "liferay-plugin-package.properties");

		Files.write(liferayPluginPackage.toPath(), properties.getBytes());
	}

	private File _createWorkspace() throws Exception {
		File workspace = new File(temporaryFolder.getRoot(), "build/test/workspace");

		File themesDir = new File(workspace, "themes");

		themesDir.mkdirs();

		String settings = "apply plugin: \"com.liferay.workspace\"";

		File settingsFile = new File(workspace, "settings.gradle");

		Files.write(settingsFile.toPath(), settings.getBytes());

		_createTheme(workspace, "compass-theme", true);

		_createTheme(workspace, "non-compass-theme", false);

		return workspace;
	}

}