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

import aQute.lib.io.IO;

import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.GradleRunnerUtil;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import org.gradle.testkit.runner.BuildTask;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
public class SamplesCommandTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		IO.copy(new File("wrapper.zip"), new File("build/classes/java/test/wrapper.zip"));
	}

	@Test
	public void testGetSample() throws Exception {
		String[] args = {"samples", "-d", temporaryFolder.getRoot().getPath() + "/test", "friendly-url"};

		TestUtil.runBlade(temporaryFolder.getRoot(), args);

		File projectDir = new File(temporaryFolder.getRoot(), "test/friendly-url");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.friendly.url-1.0.0.jar");
	}

	@Test
	public void testGetSampleMaven() throws Exception {
		String[] args = {"samples", "-d", temporaryFolder.getRoot().getPath() + "/test", "-b", "maven", "friendly-url"};

		TestUtil.runBlade(temporaryFolder.getRoot(), args);

		File projectDir = new File(temporaryFolder.getRoot(), "test/friendly-url");

		Assert.assertTrue(projectDir.exists());

		File gradleBuildFile = IO.getFile(projectDir, "build.gradle");
		File mavenBuildFile = IO.getFile(projectDir, "pom.xml");

		Assert.assertFalse(gradleBuildFile.exists());
		Assert.assertTrue(mavenBuildFile.exists());
	}

	@Test
	public void testGetSampleWithDependencies() throws Exception {
		String[] args = {"samples", "-d", temporaryFolder.getRoot().getPath() + "/test", "rest"};

		TestUtil.runBlade(temporaryFolder.getRoot(), args);

		File projectDir = new File(temporaryFolder.getRoot(), "test/rest");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.rest-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithGradleWrapper() throws Exception {
		String[] args = {"samples", "-d", temporaryFolder.getRoot().getPath() + "/test", "authenticator-shiro"};

		TestUtil.runBlade(temporaryFolder.getRoot(), args);

		File projectDir = new File(temporaryFolder.getRoot(), "test/authenticator-shiro");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		File gradleWrapperJar = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.jar");

		File gradleWrapperProperties = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		File gradleWrapperShell = IO.getFile(projectDir, "gradlew");

		Assert.assertTrue(buildFile.exists());
		Assert.assertTrue(gradleWrapperJar.exists());
		Assert.assertTrue(gradleWrapperProperties.exists());
		Assert.assertTrue(gradleWrapperShell.exists());

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.authenticator.shiro-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithGradleWrapperExisting() throws Exception {
		String[] initArgs = {"--base", temporaryFolder.getRoot().getPath() + "/test/workspace", "init"};

		BladeTestResults bladeTestResults = TestUtil.runBlade(initArgs);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output, output == null || output.isEmpty());

		String[] samplesArgs =
			{"samples", "-d", temporaryFolder.getRoot().getPath() + "/test/workspace/modules", "auth-failure"};

		bladeTestResults = TestUtil.runBlade(samplesArgs);

		output = bladeTestResults.getOutput();

		Assert.assertTrue(output, output == null || output.isEmpty());

		File projectDir = new File(temporaryFolder.getRoot(), "test/workspace/modules/auth-failure");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		File gradleWrapperJar = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.jar");

		File gradleWrapperProperties = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		File gradleWrapperShell = IO.getFile(projectDir, "gradlew");

		Assert.assertTrue(buildFile.exists());
		Assert.assertFalse(gradleWrapperJar.exists());
		Assert.assertFalse(gradleWrapperProperties.exists());
		Assert.assertFalse(gradleWrapperShell.exists());

		File workspaceDir = new File(temporaryFolder.getRoot(), "test/workspace");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspaceDir.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectDir.toString(), "com.liferay.blade.auth.failure-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithVersions() throws Exception {
		String[] args70 = {"samples", "-d", temporaryFolder.getRoot().getPath() + "/test", "-v", "7.0", "jsp-portlet"};

		TestUtil.runBlade(temporaryFolder.getRoot(), args70);

		File projectDir = new File(temporaryFolder.getRoot(), "test/jsp-portlet");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		String content = new String(IO.read(buildFile));

		Assert.assertTrue(buildFile.exists());

		Assert.assertTrue(content, content.contains("\"com.liferay.portal.kernel\", version: \"2.0.0\""));

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.jsp.portlet-1.0.0.jar");

		String[] args71 =
			{"samples", "-d", temporaryFolder.getRoot().getPath() + "/test71", "-v", "7.1", "jsp-portlet"};

		TestUtil.runBlade(temporaryFolder.getRoot(), args71);

		projectDir = new File(temporaryFolder.getRoot(), "test71/jsp-portlet");

		Assert.assertTrue(projectDir.exists());

		buildFile = IO.getFile(projectDir, "build.gradle");

		content = new String(IO.read(buildFile));

		Assert.assertTrue(buildFile.exists());

		Assert.assertTrue(content, content.contains("\"com.liferay.portal.kernel\", version: \"3.0.0\""));

		projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.jsp.portlet-1.0.0.jar");
	}

	@Test
	public void testListSamples() throws Exception {
		BladeTestResults bladeTestResults = TestUtil.runBlade("samples");

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output.contains("ds-portlet"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

}