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

import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Domain;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.LiferayBundleDeployer;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.FileWatcher;
import com.liferay.blade.cli.util.FileWatcher.Consumer;

import java.io.File;
import java.io.PrintStream;

import java.net.URI;

import java.nio.file.Path;

import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 */
public class DeployCommand extends BaseCommand<DeployArgs> {

	public DeployCommand() {
	}

	@Override
	public void execute() throws Exception {
		String host = "localhost";
		int port = 11311;

		if (!BladeUtil.canConnect(host, port)) {
			_addError("deploy", "Unable to connect to gogo shell on " + host + ":" + port);

			return;
		}

		BladeCLI bladeCLI = getBladeCLI();

		GradleExec gradleExec = new GradleExec(bladeCLI);

		Set<File> outputFiles = GradleTooling.getOutputFiles(bladeCLI.getCacheDir(), bladeCLI.getBase());

		DeployArgs deployArgs = getArgs();

		if (deployArgs.isWatch()) {
			_deployWatch(gradleExec, outputFiles, host, port);
		}
		else {
			_deploy(gradleExec, outputFiles, host, port);
		}
	}

	@Override
	public Class<DeployArgs> getArgsClass() {
		return DeployArgs.class;
	}

	private static void _deployWar(File file, LiferayBundleDeployer deployer) throws Exception {
		URI uri = file.toURI();

		long bundleId = deployer.install(uri);

		if (bundleId > 0) {
			deployer.start(bundleId);
		}
		else {
			throw new Exception("Failed to deploy war: " + file.getAbsolutePath());
		}
	}

	private void _addError(String msg) {
		getBladeCLI().addErrors("deploy", Collections.singleton(msg));
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private void _deploy(GradleExec gradle, Set<File> outputFiles, String host, int port) throws Exception {
		int retcode = gradle.executeGradleCommand("assemble -x check");

		if (retcode > 0) {
			_addError("Gradle assemble task failed.");

			return;
		}

		Stream<File> stream = outputFiles.stream();

		stream.filter(
			File::exists
		).forEach(
			outputFile -> {
				try {
					_installOrUpdate(outputFile, host, port);
				}
				catch (Exception e) {
					PrintStream err = getBladeCLI().err();

					err.println(e.getMessage());

					e.printStackTrace(err);
				}
			}
		);
	}

	private void _deployBundle(File file, LiferayBundleDeployer client, Domain bundle, Entry<String, Attrs> bsn)
		throws Exception {

		Entry<String, Attrs> fragmentHost = bundle.getFragmentHost();

		String hostBsn = null;

		if (fragmentHost != null) {
			hostBsn = fragmentHost.getKey();
		}

		Collection<BundleDTO> bundles = client.getBundles();

		long existingId = client.getBundleId(bundles, bsn.getKey());

		long hostId = client.getBundleId(bundles, hostBsn);

		URI uri = file.toURI();

		if (existingId > 0) {
			_reloadExistingBundle(client, fragmentHost, existingId, hostId, uri);
		}
		else {
			_installNewBundle(client, bsn, fragmentHost, hostId, uri);
		}
	}

	private void _deployWatch(final GradleExec gradleExec, final Set<File> outputFiles, String host, int port)
		throws Exception {

		_deploy(gradleExec, outputFiles, host, port);

		Stream<File> stream = outputFiles.stream();

		Collection<Path> outputPaths = stream.map(
			File::toPath
		).collect(
			Collectors.toSet()
		);

		new Thread() {

			@Override
			public void run() {
				try {
					gradleExec.executeGradleCommand("assemble -x check -t");
				}
				catch (Exception e) {
				}
			}

		}.start();

		Consumer<Path> consumer = new Consumer<Path>() {

			@Override
			public void consume(Path modified) {
				try {
					File file = modified.toFile();

					File modifiedFile = file.getAbsoluteFile();

					if (outputPaths.contains(modifiedFile.toPath())) {
						getBladeCLI().out("installOrUpdate " + modifiedFile);

						_installOrUpdate(modifiedFile, host, port);
					}
				}
				catch (Exception e) {
				}
			}

		};

		File base = getBladeCLI().getBase();

		new FileWatcher(base.toPath(), true, consumer);
	}

	private void _installNewBundle(
			LiferayBundleDeployer client, Entry<String, Attrs> bsn, Entry<String, Attrs> fragmentHost, long hostId,
			URI uri)
		throws Exception {

		PrintStream out = getBladeCLI().out();

		long existingId = client.install(uri);

		if ((fragmentHost != null) && (hostId > 0)) {
			client.refresh(hostId);

			out.println("Deployed fragment bundle " + existingId);
		}
		else {
			long checkedExistingId = client.getBundleId(bsn.getKey());

			try {
				if (!Objects.equals(existingId, checkedExistingId)) {
					out.print("Error: Bundle IDs do not match.");
				}
				else {
					if (checkedExistingId > 1) {
						client.start(checkedExistingId);

						out.println("Deployed bundle " + existingId);
					}
					else {
						out.println("Error: Bundle failed to deploy: " + bsn);
					}
				}
			}
			catch (Exception e) {
				out.println("Error: Bundle failed to deploy: " + bsn);

				e.printStackTrace(out);
			}
		}
	}

	private void _installOrUpdate(File file, String host, int port) throws Exception {
		file = file.getAbsoluteFile();

		try (LiferayBundleDeployer client = LiferayBundleDeployer.newInstance(host, port)) {
			String name = file.getName();

			name = name.toLowerCase();

			Domain bundle = Domain.domain(file);

			Entry<String, Attrs> bsn = bundle.getBundleSymbolicName();

			if (bsn != null) {
				_deployBundle(file, client, bundle, bsn);
			}
			else if (name.endsWith(".war")) {
				_deployWar(file, client);
			}
		}
	}

	private final void _reloadExistingBundle(
			LiferayBundleDeployer client, Entry<String, Attrs> fragmentHost, long existingId, long hostId, URI uri)
		throws Exception {

		if ((fragmentHost != null) && (hostId > 0)) {
			client.reloadFragment(existingId, hostId, uri);
		}
		else {
			client.reloadBundle(existingId, uri);
		}

		PrintStream out = getBladeCLI().out();

		out.println("Updated bundle " + existingId);
	}

}